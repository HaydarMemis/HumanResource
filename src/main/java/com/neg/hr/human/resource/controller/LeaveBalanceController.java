package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.validator.LeaveBalanceValidator;
import com.neg.hr.human.resource.dto.create.CreateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.dto.LeaveBalanceEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateLeaveBalanceRequestDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.LeaveBalance;
import com.neg.hr.human.resource.entity.LeaveType;
import com.neg.hr.human.resource.mapper.LeaveBalanceMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.LeaveTypeRepository;
import com.neg.hr.human.resource.service.impl.LeaveBalanceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/leave_balances")
public class LeaveBalanceController {

    private final LeaveBalanceServiceImpl leaveBalanceService;
    private final LeaveBalanceValidator leaveBalanceValidator;
    private final LeaveTypeRepository leaveTypeRepository;
    private final EmployeeRepository employeeRepository;

    public LeaveBalanceController(LeaveBalanceServiceImpl leaveBalanceService,
                                  LeaveBalanceValidator leaveBalanceValidator,
                                  LeaveTypeRepository leaveTypeRepository,
                                  EmployeeRepository employeeRepository) {
        this.leaveBalanceService = leaveBalanceService;
        this.leaveBalanceValidator = leaveBalanceValidator;
        this.leaveTypeRepository = leaveTypeRepository;
        this.employeeRepository = employeeRepository;
    }

    // GET all leave balances
    @GetMapping
    public List<LeaveBalanceEntityDTO> getAllLeaveBalances() {
        return leaveBalanceService.findAll()
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET leave balance by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceById(@PathVariable Long id) {
        return leaveBalanceService.findById(id)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create new leave balance
    @PostMapping
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

    // PUT - update leave balance
    @PutMapping("/{id}")
    public ResponseEntity<LeaveBalanceEntityDTO> updateLeaveBalance(@PathVariable Long id,
                                                                    @Valid @RequestBody UpdateLeaveBalanceRequestDTO dto) {
        Optional<LeaveBalance> existingOpt = leaveBalanceService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        leaveBalanceValidator.validateUpdateDTO(dto);

        LeaveBalance leaveBalance = existingOpt.get();

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

    // DELETE leave balance by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
        Optional<LeaveBalance> existingLeaveBalance = leaveBalanceService.findById(id);
        if (existingLeaveBalance.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        leaveBalanceService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // GET leave balances by employee ID
    @GetMapping("/employee/{employeeId}")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByEmployee(@PathVariable Long employeeId) {
        return leaveBalanceService.findByEmployeeId(employeeId)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET leave balances by employee ID and year
    @GetMapping("/employee/{employeeId}/year/{year}")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByEmployeeAndYear(@PathVariable Long employeeId,
                                                                         @PathVariable Integer year) {
        return leaveBalanceService.findByEmployeeIdAndDate(year, employeeId)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET by employee ID and leave type ID
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeAndLeaveType(@PathVariable Long employeeId,
                                                                                       @PathVariable Long leaveTypeId) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET by employee ID, leave type ID and year
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}/year/{year}")
    public ResponseEntity<LeaveBalanceEntityDTO> getLeaveBalanceByEmployeeLeaveTypeAndYear(@PathVariable Long employeeId,
                                                                                           @PathVariable Long leaveTypeId,
                                                                                           @PathVariable Integer year) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeIdAndDate(employeeId, leaveTypeId, year)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET leave balances by leave type ID and year
    @GetMapping("/leave_type/{leaveTypeId}/year/{year}")
    public List<LeaveBalanceEntityDTO> getLeaveBalancesByLeaveTypeAndYear(@PathVariable Long leaveTypeId,
                                                                          @PathVariable Integer year) {
        return leaveBalanceService.findByLeaveTypeIdAndDate(leaveTypeId, year)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }
}
