package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.LeaveBalanceValidator;
import com.neg.hr.human.resource.dto.CreateLeaveBalanceDTO;
import com.neg.hr.human.resource.dto.LeaveBalanceDTO;
import com.neg.hr.human.resource.dto.UpdateLeaveBalanceDTO;
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
    public List<LeaveBalanceDTO> getAllLeaveBalances() {
        return leaveBalanceService.findAll()
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET leave balance by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceById(@PathVariable Long id) {
        return leaveBalanceService.findById(id)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create new leave balance
    @PostMapping
    public ResponseEntity<LeaveBalanceDTO> createLeaveBalance(@Valid @RequestBody CreateLeaveBalanceDTO dto) {
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
    public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@PathVariable Long id,
                                                              @Valid @RequestBody UpdateLeaveBalanceDTO dto) {
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
    public List<LeaveBalanceDTO> getLeaveBalancesByEmployee(@PathVariable Long employeeId) {
        return leaveBalanceService.findByEmployeeId(employeeId)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET leave balances by employee ID and year
    @GetMapping("/employee/{employeeId}/year/{year}")
    public List<LeaveBalanceDTO> getLeaveBalancesByEmployeeAndYear(@PathVariable Long employeeId,
                                                                   @PathVariable Integer year) {
        return leaveBalanceService.findByEmployeeIdAndDate(year, employeeId)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }

    // GET by employee ID and leave type ID
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceByEmployeeAndLeaveType(@PathVariable Long employeeId,
                                                                                 @PathVariable Long leaveTypeId) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeId(employeeId, leaveTypeId)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET by employee ID, leave type ID and year
    @GetMapping("/employee/{employeeId}/leave_type/{leaveTypeId}/year/{year}")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceByEmployeeLeaveTypeAndYear(@PathVariable Long employeeId,
                                                                                     @PathVariable Long leaveTypeId,
                                                                                     @PathVariable Integer year) {
        return leaveBalanceService.findByEmployeeIdAndLeaveTypeIdAndDate(employeeId, leaveTypeId, year)
                .map(LeaveBalanceMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // GET leave balances by leave type ID and year
    @GetMapping("/leave_type/{leaveTypeId}/year/{year}")
    public List<LeaveBalanceDTO> getLeaveBalancesByLeaveTypeAndYear(@PathVariable Long leaveTypeId,
                                                                    @PathVariable Integer year) {
        return leaveBalanceService.findByLeaveTypeIdAndDate(leaveTypeId, year)
                .stream()
                .map(LeaveBalanceMapper::toDTO)
                .toList();
    }
}
