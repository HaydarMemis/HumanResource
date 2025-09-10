package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveBalanceMapper;
import com.neg.technology.human.resource.leave.model.request.*;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveBalanceResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.leave.service.LeavePolicyService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    public static final String MESSAGE = "LeaveBalance";

    private final LeaveBalanceRepository leaveBalanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceMapper leaveBalanceMapper;
    private final LeavePolicyService leavePolicyService;

    @Override
    public Mono<LeaveBalanceResponseList> getAll() {
        return Mono.fromCallable(() -> leaveBalanceMapper.toResponseList(leaveBalanceRepository.findAll()));
    }

    @Override
    public Mono<LeaveBalanceResponse> getById(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id zorunludur"));
        }
        return Mono.fromCallable(() ->
                leaveBalanceRepository.findById(request.getId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        );
    }

    /**
     * Burada LeavePolicyService üzerinden izin hakedişini, cinsiyet, yaş, kıdem gibi parametrelerle çekiyoruz.
     * Ayrıca eski yıllardan kalan izinleri son yıla topluyoruz.
     * Aynı tarihte izin isteği varsa hata döner.
     */
    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId ve LeaveTypeId zorunlu"));
        }

        Mono<Employee> employeeMono = Mono.fromCallable(() ->
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı: " + request.getEmployeeId()))
        );

        Mono<LeaveType> leaveTypeMono = Mono.fromCallable(() ->
                leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new RuntimeException("İzin tipi bulunamadı: " + request.getLeaveTypeId()))
        );

        return Mono.zip(employeeMono, leaveTypeMono)
                .flatMap(tuple -> {
                    Employee employee = tuple.getT1();
                    LeaveType leaveType = tuple.getT2();

                    // Doğum tarihi kontrolü (hata logundaki gibi)
                    if (employee.getPerson() == null || employee.getPerson().getBirthDate() == null) {
                        return Mono.error(new IllegalArgumentException("Çalışanın doğum tarihi bilgisi eksik veya erişilemiyor."));
                    }

                    int requestYear = request.getDate() != null ? request.getDate() : LocalDate.now().getYear();
                    int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : 0;
                    if (requestedDays <= 0) {
                        return Mono.error(new RuntimeException("Talep edilen gün sayısı 0'dan büyük olmalı"));
                    }

                    // Aynı tarihte izin isteği var mı kontrolü
                    List<LeaveBalance> allBalances = leaveBalanceRepository
                            .findByEmployeeIdAndLeaveTypeIdOrderByDateAsc(employee.getId(), leaveType.getId());
                    boolean sameYearRequestExists = allBalances.stream()
                            .anyMatch(b -> b.getDate().equals(requestYear));
                    if (sameYearRequestExists) {
                        return Mono.error(new RuntimeException("Aynı yıl için tekrar izin eklenemez!"));
                    }

                    // LeavePolicyService ile hakediş hesapla (yaş, cinsiyet, kıdem vs.)
                    int earnedDays = 0;
                    try {
                        earnedDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, requestYear);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("İzin hakedişi hesaplanamadı: " + e.getMessage()));
                    }

                    // Eski yıllardan kalan izinleri bul ve son yıla topla
                    int lastYear = requestYear;
                    int totalUnused = 0;
                    for (LeaveBalance b : allBalances) {
                        if (b.getDate() < lastYear) {
                            int unused = b.getAmount().intValue() - b.getUsedDays();
                            if (unused > 0) totalUnused += unused;
                        }
                    }

                    // Son yıl için toplam hakediş = hakediş + devreden
                    int totalRight = earnedDays + totalUnused;

                    // Borç izni varsa ekle
                    int borrowableLimit = leaveType.getBorrowableLimit() != null ? leaveType.getBorrowableLimit() : 0;
                    int maxUsable = totalRight + borrowableLimit;

                    // Kullanılmış günleri bul
                    int usedDays = allBalances.stream()
                            .filter(b -> b.getDate() == lastYear)
                            .mapToInt(LeaveBalance::getUsedDays)
                            .sum();

                    if (usedDays + requestedDays > maxUsable) {
                        return Mono.error(new RuntimeException(
                                "İzin talebi maksimum kullanılabilir gün sayısını aşıyor! (Hak: " + totalRight + ", Borç: " + borrowableLimit + ")"
                        ));
                    }

                    // Son yıl için LeaveBalance oluştur/güncelle
                    LeaveBalance yearBalance = allBalances.stream()
                            .filter(b -> b.getDate() == lastYear)
                            .findFirst()
                            .orElse(null);

                    if (yearBalance != null) {
                        yearBalance.setAmount(yearBalance.getAmount().add(BigDecimal.valueOf(requestedDays)));
                        yearBalance.setUsedDays(yearBalance.getUsedDays() + requestedDays);
                        leaveBalanceRepository.save(yearBalance);
                    } else {
                        LeaveBalance newBalance = LeaveBalance.builder()
                                .employee(employee)
                                .leaveType(leaveType)
                                .date(lastYear)
                                .amount(BigDecimal.valueOf(requestedDays))
                                .usedDays(requestedDays)
                                .build();
                        leaveBalanceRepository.save(newBalance);
                        yearBalance = newBalance;
                    }

                    LeaveBalanceResponse response = LeaveBalanceResponse.builder()
                            .id(yearBalance.getId())
                            .employeeFirstName(employee.getPerson().getFirstName())
                            .employeeLastName(employee.getPerson().getLastName())
                            .leaveTypeName(leaveType.getName())
                            .leaveTypeBorrowableLimit(leaveType.getBorrowableLimit())
                            .leaveTypeIsUnpaid(leaveType.getIsUnpaid())
                            .date(yearBalance.getDate())
                            .amount(yearBalance.getAmount())
                            .build();

                    return Mono.just(response);
                });
    }

    @Override
    public Mono<LeaveBalanceResponse> update(UpdateLeaveBalanceRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id zorunludur"));
        }

        return Mono.fromCallable(() -> {
            LeaveBalance existing = leaveBalanceRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

            Employee employee = null;
            if (request.getEmployeeId() != null) {
                employee = employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            }

            LeaveType leaveType = null;
            if (request.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));
            }

            leaveBalanceMapper.updateEntity(existing, request, employee, leaveType);
            LeaveBalance updated = leaveBalanceRepository.save(existing);

            Logger.logUpdated(LeaveBalance.class, updated.getId(), MESSAGE);
            return leaveBalanceMapper.toResponse(updated);
        });
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("Id boş olamaz"));
        }

        return Mono.fromRunnable(() -> {
            if (!leaveBalanceRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getId());
            }
            leaveBalanceRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveBalance.class, request.getId());
        });
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployee(IdRequest request) {
        if (request == null || request.getId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId boş olamaz"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(leaveBalanceRepository.findByEmployeeId(request.getId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByEmployeeAndYear(EmployeeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId ve Year zorunlu"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByEmployeeIdAndDate(request.getEmployeeId(), request.getYear())
                )
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeAndLeaveType(EmployeeLeaveTypeRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId ve LeaveTypeId zorunlu"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponse> getByEmployeeLeaveTypeAndYear(EmployeeLeaveTypeYearRequest request) {
        if (request == null || request.getEmployeeId() == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("EmployeeId, LeaveTypeId ve Year zorunlu"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndDate(
                                request.getEmployeeId(), request.getLeaveTypeId(), request.getYear())
                        .map(leaveBalanceMapper::toResponse)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE,
                                "Employee: " + request.getEmployeeId() + ", LeaveType: " + request.getLeaveTypeId() + ", Year: " + request.getYear()))
        );
    }

    @Override
    public Mono<LeaveBalanceResponseList> getByLeaveTypeAndYear(LeaveTypeYearRequest request) {
        if (request == null || request.getLeaveTypeId() == null || request.getYear() == null) {
            return Mono.error(new IllegalArgumentException("LeaveTypeId ve Year zorunlu"));
        }

        return Mono.fromCallable(() ->
                leaveBalanceMapper.toResponseList(
                        leaveBalanceRepository.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                )
        );
    }

    /**
     * Belirli bir çalışanın, belirli bir izin tipi ve yıl için toplam kullanılabilir izin gününü döner.
     * Bu metod, LeaveRequestServiceImpl gibi servisler tarafından çağrılır.
     */
    @Override
    public int getTotalUsableLeaveDaysByEmployeeAndType(Long employeeId, Long leaveTypeId, int year) {
        if (employeeId == null || leaveTypeId == null) {
            throw new IllegalArgumentException("EmployeeId ve LeaveTypeId zorunlu");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı", employeeId));
        LeaveType leaveType = leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("İzin tipi bulunamadı", leaveTypeId));

        // Doğum tarihi kontrolü (hata logundaki gibi)
        if (employee.getPerson() == null || employee.getPerson().getBirthDate() == null) {
            throw new IllegalArgumentException("Çalışanın doğum tarihi bilgisi eksik veya erişilemiyor.");
        }

        // Hakediş gününü LeavePolicyService'den al
        int earnedDays = 0;
        try {
            earnedDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, year);
        } catch (Exception e) {
            throw new RuntimeException("İzin hakedişi hesaplanamadı: " + e.getMessage());
        }

        // Eski yıllardan devreden kullanılmamış izinleri bul
        List<LeaveBalance> allBalances = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdOrderByDateAsc(employeeId, leaveTypeId);

        int totalUnused = 0;
        for (LeaveBalance b : allBalances) {
            if (b.getDate() < year) {
                int unused = b.getAmount().intValue() - b.getUsedDays();
                if (unused > 0) totalUnused += unused;
            }
        }

        // Borç izni limiti
        int borrowableLimit = leaveType.getBorrowableLimit() != null ? leaveType.getBorrowableLimit() : 0;

        // O yıl için kullanılan günleri bul
        int usedDays = allBalances.stream()
                .filter(b -> b.getDate() == year)
                .mapToInt(LeaveBalance::getUsedDays)
                .sum();

        // Toplam kullanılabilir gün = hakediş + devreden + borç - kullanılan
        int totalUsable = earnedDays + totalUnused + borrowableLimit - usedDays;
        if (totalUsable < 0) totalUsable = 0;

        return totalUsable;
    }
}
