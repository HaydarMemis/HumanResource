package com.neg.technology.human.resource.leave.service.impl;

import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.request.EmployeeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeLeaveTypeDateRangeRequest;
import com.neg.technology.human.resource.employee.model.request.EmployeeStatusRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.leave.model.entity.LeaveRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.leave.model.mapper.LeaveRequestMapper;
import com.neg.technology.human.resource.leave.model.request.ChangeLeaveRequestStatusRequest;
import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponse;
import com.neg.technology.human.resource.leave.model.response.LeaveRequestResponseList;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.service.LeaveRequestService;
import com.neg.technology.human.resource.leave.service.LeaveBalanceService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.StatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceService leaveBalanceService; // LeaveBalanceServiceImpl üzerinden çekilecek

    @Override
    public Mono<LeaveRequestResponseList> getAll() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> entities = leaveRequestRepository.findAll();
            List<LeaveRequestResponse> responses = entities.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponse> getById(IdRequest request) {
        return Mono.fromCallable(() ->
                leaveRequestRepository.findById(request.getId())
                        .map(LeaveRequestMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException("Leave Request", request.getId()))
        );
    }

    /**
     * Burada izin talebi oluşturulurken;
     * - Çalışan ve izin tipi kontrolü yapılır.
     * - Aynı tarihte izin çakışması kontrol edilir.
     * - Tüm yıllardan devreden ve o yılki hakediş + borç izni dahil toplam izin hakkı hesaplanır.
     * - Talep edilen gün bu toplam hakkı aşamaz.
     * - LeavePolicyServiceImpl'deki kurallar (yaş, cinsiyet, kıdem vs.) LeaveBalanceServiceImpl üzerinden uygulanır.
     */
    @Override
    public Mono<LeaveRequestResponse> create(CreateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
            // Çalışan ve izin tipi kontrolü
            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı", dto.getEmployeeId()));

            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("İzin tipi bulunamadı", dto.getLeaveTypeId()));

            // Aynı tarihte izin isteği var mı kontrolü (çakışma)
            List<LeaveRequest> mevcutIzinler = leaveRequestRepository.findByEmployeeId(dto.getEmployeeId());
            boolean cakismaVar = mevcutIzinler.stream().anyMatch(izin ->
                    !(dto.getEndDate().isBefore(izin.getStartDate()) || dto.getStartDate().isAfter(izin.getEndDate()))
            );
            if (cakismaVar) {
                throw new IllegalArgumentException("Aynı tarihlerde daha önce izin isteği gönderilmiş.");
            }

            // Toplam kullanılabilir izin hakkı: eski yıllardan devreden + o yılki hakediş + borç izni
            // LeaveBalanceServiceImpl.getTotalUsableLeaveDaysByEmployeeAndType metodu LeavePolicyServiceImpl kurallarını uygular
            int toplamIzinHakki = leaveBalanceService.getTotalUsableLeaveDaysByEmployeeAndType(
                    dto.getEmployeeId(),
                    dto.getLeaveTypeId(),
                    dto.getStartDate().getYear()
            );

            // Talep edilen gün sayısı
            long talepEdilenGun = dto.getEndDate().toEpochDay() - dto.getStartDate().toEpochDay() + 1;
            if (talepEdilenGun > toplamIzinHakki) {
                throw new IllegalArgumentException("Talep edilen gün sayısı toplam izin hakkını aşıyor. Kalan: " + toplamIzinHakki);
            }

            Employee approver = null;
            if (dto.getApprovedById() != null) {
                approver = employeeRepository.findById(dto.getApprovedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Onaylayan çalışan bulunamadı", dto.getApprovedById()));
            }

            LeaveRequest entity = LeaveRequestMapper.toEntity(dto, employee, leaveType, approver);
            LeaveRequest saved = leaveRequestRepository.save(entity);

            Logger.logCreated(LeaveRequest.class, saved.getId(), "LeaveRequest");

            return LeaveRequestMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<LeaveRequestResponse> update(UpdateLeaveRequestRequest dto) {
        return Mono.fromCallable(() -> {
            LeaveRequest existing = leaveRequestRepository.findById(dto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Request", dto.getId()));

            Employee employee = null;
            if (dto.getEmployeeId() != null) {
                employee = employeeRepository.findById(dto.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı", dto.getEmployeeId()));
            }

            LeaveType leaveType = null;
            if (dto.getLeaveTypeId() != null) {
                leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException("İzin tipi bulunamadı", dto.getLeaveTypeId()));
            }

            Employee approver = null;
            if (dto.getApprovedById() != null) {
                approver = employeeRepository.findById(dto.getApprovedById())
                        .orElseThrow(() -> new ResourceNotFoundException("Onaylayan çalışan bulunamadı", dto.getApprovedById()));
            }

            // Eğer tarih aralığı değişiyorsa, tekrar çakışma kontrolü yapılmalı
            if (dto.getStartDate() != null && dto.getEndDate() != null) {
                Long kontrolEdilecekEmployeeId = dto.getEmployeeId() != null ? dto.getEmployeeId() : existing.getEmployee().getId();
                List<LeaveRequest> mevcutIzinler = leaveRequestRepository.findByEmployeeId(kontrolEdilecekEmployeeId);
                boolean overlap = mevcutIzinler.stream()
                        .filter(izin -> !izin.getId().equals(existing.getId())) // Kendi kaydını hariç tut
                        .anyMatch(izin ->
                                !(dto.getEndDate().isBefore(izin.getStartDate()) || dto.getStartDate().isAfter(izin.getEndDate()))
                        );
                if (overlap) {
                    throw new IllegalArgumentException("Aynı tarihlerde daha önce izin isteği gönderilmiş.");
                }
            }

            LeaveRequestMapper.updateEntity(existing, dto, employee, leaveType, approver);

            LeaveRequest updated = leaveRequestRepository.save(existing);

            Logger.logUpdated(LeaveRequest.class, updated.getId(), "LeaveRequest");

            return LeaveRequestMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<Void> delete(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!leaveRequestRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException("Leave Request", request.getId());
            }
            leaveRequestRepository.deleteById(request.getId());
            Logger.logDeleted(LeaveRequest.class, request.getId());
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployee(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeId(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByStatus(StatusRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByStatus(request.getStatus());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getCancelled() {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByIsCancelledTrue();
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByApprover(IdRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByApprovedById(request.getId());
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployeeAndStatus(EmployeeStatusRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(
                    request.getEmployeeId(),
                    request.getStatus()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByDateRange(EmployeeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByStartDateBetween(
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getByEmployeeLeaveTypeAndDateRange(EmployeeLeaveTypeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndLeaveTypeIdAndStartDateBetween(
                    request.getEmployeeId(),
                    request.getLeaveTypeId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getOverlapping(EmployeeDateRangeRequest request) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findOverlappingRequests(
                    request.getEmployeeId(),
                    request.getStartDate(),
                    request.getEndDate()
            );
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponseList> getApprovedByEmployee(Long employeeId) {
        return Mono.fromCallable(() -> {
            List<LeaveRequest> list = leaveRequestRepository.findByEmployeeIdAndStatus(employeeId, "APPROVED");
            List<LeaveRequestResponse> responses = list.stream()
                    .map(LeaveRequestMapper::toDTO)
                    .toList();
            return new LeaveRequestResponseList(responses);
        });
    }

    @Override
    public Mono<LeaveRequestResponse> changeStatus(ChangeLeaveRequestStatusRequest dto) {
        return Mono.fromCallable(() -> {
            LeaveRequest existing = leaveRequestRepository.findById(dto.getLeaveRequestId())
                    .orElseThrow(() -> new ResourceNotFoundException("Leave Request", dto.getLeaveRequestId()));

            existing.setStatus(dto.getStatus());
            if (dto.getApprovalNote() != null) {
                existing.setApprovalNote(dto.getApprovalNote());
            }

            LeaveRequest updated = leaveRequestRepository.save(existing);

            Logger.logUpdated(LeaveRequest.class, updated.getId(), "LeaveRequest status changed");

            return LeaveRequestMapper.toDTO(updated);
        });
    }

}
