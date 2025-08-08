package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.EmployeeEntityDTO;
import com.neg.hr.human.resource.validator.EmployeeValidator;
import com.neg.hr.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.hr.human.resource.dto.EmployeeEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.hr.human.resource.entity.*;
import com.neg.hr.human.resource.mapper.EmployeeMapper;
import com.neg.hr.human.resource.repository.*;
import com.neg.hr.human.resource.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController<DateRequest, CompanyIdRequest, PositionIdRequest, DepartmentIdRequest, ManagerIdRequest> {

    private final EmployeeService employeeService;
    private final EmployeeValidator employeeValidator;

    public EmployeeController(EmployeeService employeeService,
                              EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.employeeValidator = employeeValidator;
    }

    // POST - get all employees (istek parametresi yok)
    @PostMapping("/getAll")
    public List<EmployeeEntityDTO> getAllEmployees() {
        return employeeService.findAll()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employee by id
    @PostMapping("/getById")
    public ResponseEntity<EmployeeEntityDTO> getEmployeeById(@RequestBody @Valid IdRequest request) {
        return employeeService.findById(request.getId())
                .map(emp -> ResponseEntity.ok(EmployeeMapper.toDTO(emp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create employee
    @PostMapping("/create")
    public ResponseEntity<EmployeeEntityDTO> createEmployee(@Valid @RequestBody CreateEmployeeRequestDTO dto) {
        employeeValidator.validateCreateDTO(dto);
        Employee saved = employeeService.createEmployee(dto);
        return ResponseEntity.ok(EmployeeMapper.toDTO(saved));
    }

    // POST - update employee
    @PostMapping("/update")
    public ResponseEntity<EmployeeEntityDTO> updateEmployee(@Valid @RequestBody UpdateEmployeeRequestDTO dto) {
        if (!employeeService.existsById(dto.getId())) {
            return ResponseEntity.notFound().build();
        }
        employeeValidator.validateUpdateDTO(dto);
        Employee updated = employeeService.updateEmployee(dto);
        return ResponseEntity.ok(EmployeeMapper.toDTO(updated));
    }

    // POST - delete employee by id
    @PostMapping("/delete")
    public ResponseEntity<Void> deleteEmployee(@RequestBody @Valid IdRequest request) {
        if (!employeeService.existsById(request.getId())) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - get active employees
    @PostMapping("/getActive")
    public List<EmployeeEntityDTO> getActiveEmployees() {
        return employeeService.findByIsActiveTrue()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get inactive employees
    @PostMapping("/getInactive")
    public List<EmployeeEntityDTO> getInactiveEmployees() {
        return employeeService.findByIsActiveFalse()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employees by department
    @PostMapping("/getByDepartment")
    public List<EmployeeEntityDTO> getEmployeesByDepartment(@RequestBody @Valid DepartmentIdRequest request) {
        return employeeService.findByDepartmentId(request.getDepartmentId())
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employees by position
    @PostMapping("/getByPosition")
    public List<EmployeeEntityDTO> getEmployeesByPosition(@RequestBody @Valid PositionIdRequest request) {
        return employeeService.findByPositionId(request.getPositionId())
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employees by company
    @PostMapping("/getByCompany")
    public List<EmployeeEntityDTO> getEmployeesByCompany(@RequestBody @Valid CompanyIdRequest request) {
        return employeeService.findByCompanyId(request.getCompanyId())
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employees hired before a date
    @PostMapping("/getHiredBefore")
    public List<EmployeeEntityDTO> getEmployeesHiredBefore(@RequestBody @Valid DateRequest request) {
        LocalDateTime dateTime = LocalDateTime.parse(request.getDate());
        return employeeService.findByHireDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // POST - get employees employment ended before a date
    @PostMapping("/getEmploymentEndedBefore")
    public List<EmployeeEntityDTO> getEmployeesEmploymentEndedBefore(@RequestBody @Valid DateRequest request) {
        LocalDateTime dateTime = LocalDateTime.parse(request.getDate());
        return employeeService.findByEmploymentEndDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }
}
