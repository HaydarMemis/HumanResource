package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.EmployeeDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.mapper.EmployeeMapper;
import com.neg.hr.human.resource.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // Get all employees (DTO view)
    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.findAll()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Get employee by ID (DTO view)
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeService.findById(id);
        return employeeOpt.map(emp -> ResponseEntity.ok(EmployeeMapper.toDTO(emp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create employee (raw entity for now)
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    // Update employee (raw entity for now)
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (existingEmployee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        employee.setId(id);
        Employee updated = employeeService.save(employee);
        return ResponseEntity.ok(updated);
    }

    // Delete employee
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (existingEmployee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Active employees (DTO view)
    @GetMapping("/active")
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeService.findByIsActiveTrue()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Inactive employees (DTO view)
    @GetMapping("/inactive")
    public List<EmployeeDTO> getInactiveEmployees() {
        return employeeService.findByIsActiveFalse()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees by manager (DTO view)
    @GetMapping("/manager/{managerId}")
    public List<EmployeeDTO> getEmployeesByManager(@PathVariable Long managerId) {
        return employeeService.findByManagerId(managerId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees by department (DTO view)
    @GetMapping("/department/{departmentId}")
    public List<EmployeeDTO> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return employeeService.findByDepartmentId(departmentId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees by position (DTO view)
    @GetMapping("/position/{positionId}")
    public List<EmployeeDTO> getEmployeesByPosition(@PathVariable Long positionId) {
        return employeeService.findByPositionId(positionId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees by company (DTO view)
    @GetMapping("/company/{companyId}")
    public List<EmployeeDTO> getEmployeesByCompany(@PathVariable Long companyId) {
        return employeeService.findByCompanyId(companyId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees hired before a date (DTO view)
    @GetMapping("/hired-before/{date}")
    public List<EmployeeDTO> getEmployeesHiredBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByHireDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // Employees whose employment ended before a date (DTO view)
    @GetMapping("/ended-before/{date}")
    public List<EmployeeDTO> getEmployeesEmploymentEndedBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByEmploymentEndDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }
}
