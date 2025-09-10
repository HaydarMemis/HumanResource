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
import com.neg.technology.human.resource.leave.validator.LeaveBalanceValidator;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeYearRequest;
import com.neg.technology.human.resource.leave.model.request.LeaveTypeYearRequest;
import jakarta.validation.ValidationException;
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
    private final LeaveBalanceValidator leaveBalanceValidator;


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

    @Override
    public Mono<LeaveBalanceResponse> create(CreateLeaveBalanceRequest request) {
        leaveBalanceValidator.validateCreateDTO(request);

        return Mono.fromCallable(() -> {
            Employee employee = employeeRepository.findById(request.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));

            int year = request.getDate() != null ? request.getDate() : LocalDate.now().getYear();
            int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : 0;

            // Hakediş gününü LeavePolicyService'den al
            int maxDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, year);

            // Maternity leave için multiplePregnancy kontrolü
            if ("maternity leave".equalsIgnoreCase(leaveType.getName()) && request.getMultiplePregnancy() != null) {
                maxDays = request.getMultiplePregnancy() ? Math.max(maxDays, 140) : Math.max(maxDays, 112);
            }

            if (maxDays > 0 && requestedDays > maxDays) {
                throw new ValidationException(
                        leaveType.getName() + " için toplam izin gün sayısı maksimumu aşıyor. Maks: " + maxDays
                );
            }

            // Eski balance kontrolü
            Optional<LeaveBalance> existingOpt = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndDate(employee.getId(), leaveType.getId(), year);

            if (existingOpt.isPresent()) {
                LeaveBalance existing = existingOpt.get();
                int total = existing.getAmount().intValue() + requestedDays;
                if (maxDays > 0 && total > maxDays) {
                    throw new ValidationException(
                            leaveType.getName() + " için toplam izin gün sayısı maksimumu aşıyor. Maks: " + maxDays
                    );
                }
                existing.setAmount(BigDecimal.valueOf(total));
                leaveBalanceRepository.save(existing);
                return leaveBalanceMapper.toResponse(existing);
            }

            // Yeni balance oluştur
            LeaveBalance newBalance = LeaveBalance.builder()
                    .employee(employee)
                    .leaveType(leaveType)
                    .date(year)
                    .amount(BigDecimal.valueOf(requestedDays))
                    .usedDays(0)
                    .build();

            leaveBalanceRepository.save(newBalance);
            return leaveBalanceMapper.toResponse(newBalance);
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

            Employee employee = existing.getEmployee();
            if (request.getEmployeeId() != null) {
                employee = employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()));
            }

            LeaveType leaveType = existing.getLeaveType();
            if (request.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("LeaveType", request.getLeaveTypeId()));
            }

            int year = request.getDate() != null ? request.getDate() : existing.getDate();
            int requestedDays = request.getAmount() != null ? request.getAmount().intValue() : existing.getAmount().intValue();

            int maxDays = leavePolicyService.getLeaveDaysByPolicy(employee, leaveType, year);

            // multiplePregnancy kontrolü
            if ("maternity leave".equalsIgnoreCase(leaveType.getName())) {
                boolean multiplePregnancy = request.getAmount() != null && request.getAmount().intValue() > 112; // örnek, ihtiyaca göre düzelt
                maxDays = multiplePregnancy ? Math.max(maxDays, 140) : Math.max(maxDays, 112);
            }

            if (maxDays > 0 && requestedDays > maxDays) {
                throw new ValidationException(
                        leaveType.getName() + " için toplam izin gün sayısı maksimumu aşıyor. Maks: " + maxDays
                );
            }

            existing.setEmployee(employee);
            existing.setLeaveType(leaveType);
            existing.setDate(year);
            existing.setAmount(BigDecimal.valueOf(requestedDays));

            leaveBalanceRepository.save(existing);
            return leaveBalanceMapper.toResponse(existing);
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
