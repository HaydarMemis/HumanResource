package com.neg.hr.human.resouce.controller;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    // Constructor injection (Spring otomatik enjekte eder)
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employeeOpt = employeeService.findById(id);
        return employeeOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.save(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (!existingEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employee.setId(id);
        Employee updated = employeeService.save(employee);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (!existingEmployee.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Aktif çalışanları getir
    @GetMapping("/active")
    public List<Employee> getActiveEmployees() {
        return employeeService.findByIsActiveTrue();
    }

    // Pasif çalışanları getir
    @GetMapping("/inactive")
    public List<Employee> getInactiveEmployees() {
        return employeeService.findByIsActiveFalse();
    }

    // Manager’a göre çalışanları getir
    @GetMapping("/manager/{managerId}")
    public List<Employee> getEmployeesByManager(@PathVariable Long managerId) {
        return employeeService.findByManagerId(managerId);
    }

    // Departmana göre çalışanları getir
    @GetMapping("/department/{departmentId}")
    public List<Employee> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return employeeService.findByDepartmentId(departmentId);
    }

    // Pozisyona göre çalışanları getir
    @GetMapping("/position/{positionId}")
    public List<Employee> getEmployeesByPosition(@PathVariable Long positionId) {
        return employeeService.findByPositionId(positionId);
    }

    // Şirkete göre çalışanları getir
    @GetMapping("/company/{companyId}")
    public List<Employee> getEmployeesByCompany(@PathVariable Long companyId) {
        return employeeService.findByCompanyId(companyId);
    }

    // İşe giriş tarihinden önce işe başlamış çalışanları getir
    @GetMapping("/hired-before/{date}")
    public List<Employee> getEmployeesHiredBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByHireDateBefore(dateTime);
    }

    // İşten ayrılma tarihinden önce işten ayrılmış çalışanları getir
    @GetMapping("/ended-before/{date}")
    public List<Employee> getEmployeesEmploymentEndedBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByEmploymentEndDateBefore(dateTime);
    }
}
