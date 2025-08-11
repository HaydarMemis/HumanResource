package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.Employee.EmployeeYearRequest;
import com.neg.hr.human.resource.dto.LeaveType.EmployeeLeaveTypeRequest;
import com.neg.hr.human.resource.dto.LeaveType.EmployeeLeaveTypeYearRequest;
import com.neg.hr.human.resource.dto.LeaveType.LeaveTypeYearRequest;
import com.neg.hr.human.resource.dto.create.CreateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.dto.entity.LeaveBalanceEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.dto.utilities.IdRequest;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveBalanceMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.LeaveBalanceService;
import com.neg.hr.human.resource.validator.LeaveBalanceValidator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/leave_balances")
public class LeaveBalanceController {

    private final LeaveBalanceService leaveBalanceService;
    private final LeaveBalanceValidator leaveBalanceValidator;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveBalanceController(LeaveBalanceService leaveBalanceService,
                                  LeaveBalanceValidator leaveBalanceValidator,
                                  LeaveTypeRepository leaveTypeRepository,
                                  EmployeeRepository employeeRepository) {
        this.leaveBalanceService = leaveBalanceService;
        this.leaveBalanceValidator = leaveBalanceValidator;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeRepository = employeeRepository;
    }

    @PostMapping("/getAll")
    public List<LeaveBalanceEntityDTO> getAllLeaveBalances() {
        return leaveBalanceService.findAll()
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    @PostMapping("/getById")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceById(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.findById(request.getId())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<LeaveBalanceEntityDTO> createLeaveBalance(@Valid @RequestBody CreateLeaveBalanceRequestDTO dto) {
        leaveBalanceValidator.validateCreateDTO(dto);

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"));

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));

        LeaveBalance leaveBalance = LeaveBalanceMapper.toEntity(dto, employee, leaveType);
        LeaveBalance saved = leaveBalanceService.save(leaveBalance);

        return ResponseEntity.ok(LeaveBalanceMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<LeaveBalanceEntityDTO> updateLeaveBalance(@Valid @RequestBody UpdateLeaveBalanceRequestDTO dto) {
        if (!leaveBalanceService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }

        leaveBalanceValidator.validateUpdateDTO(dto);

        LeaveBalance leaveBalance = leaveBalanceService.findById(dto.getId()).get();

        Employee employee = (dto.getEmployeeId() != null)
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        LeaveType leaveType = (dto.getLeaveTypeId() != null)
                ? leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid leave type ID"))
                : null;

        LeaveBalanceMapper.updateEntity(leaveBalance, dto, employee, leaveType);
        LeaveBalance updated = leaveBalanceService.save(leaveBalance);

        return ResponseEntity.ok(LeaveBalanceMapper.toDTO(updated));
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteLeaveBalance(@Valid @RequestBody IdRequest request) {
        if (!leaveBalanceService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/getByEmployee")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByEmployee(@Valid @RequestBody IdRequest request) {
        return leaveBalanceService.findByEmployeeId(request.getId())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByEmployeeAndYear")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByEmployeeAndYear(@Valid @RequestBody EmployeeYearRequest request) {
        return leaveBalanceService.findByEmployeeIdAndDate(request.getYear(), request.getEmployeeId())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    @PostMapping("/getByEmployeeAndLeaveType")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeAndLeaveType(
            @Valid @RequestBody EmployeeLeaveTypeRequest request) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeId(request.getEmployeeId(), request.getLeaveTypeId())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/getByEmployeeLeaveTypeAndYear")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeLeaveTypeAndYear(
            @Valid @RequestBody EmployeeLeaveTypeYearRequest request) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeIdAndDate(
                        request.getEmployeeId(),
                        request.getLeaveTypeId(),
                        request.getYear())
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/getByLeaveTypeAndYear")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByLeaveTypeAndYear(
            @Valid @RequestBody LeaveTypeYearRequest request) {
        return leaveBalanceService.findByLeaveTypeIdAndDate(request.getLeaveTypeId(), request.getYear())
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }
}