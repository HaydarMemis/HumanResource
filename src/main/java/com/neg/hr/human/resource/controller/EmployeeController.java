package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.business.EmployeeValidator;
import com.neg.hr.human.resource.dto.CreateEmployeeDTO;
import com.neg.hr.human.resource.dto.UpdateEmployeeDTO;
import com.neg.hr.human.resource.dto.EmployeeDTO;
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
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeValidator employeeValidator;

    public EmployeeController(EmployeeService employeeService,
                              PersonRepository personRepository,
                              DepartmentRepository departmentRepository,
                              PositionRepository positionRepository,
                              CompanyRepository companyRepository,
                              EmployeeRepository employeeRepository,
                              EmployeeValidator employeeValidator) {
        this.employeeService = employeeService;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
        this.employeeRepository = employeeRepository;
        this.employeeValidator = employeeValidator;
    }

    // GET all (DTO)
    @GetMapping
    public List<EmployeeDTO> getAllEmployees() {
        return employeeService.findAll()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // GET by ID (DTO)
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id)
                .map(emp -> ResponseEntity.ok(EmployeeMapper.toDTO(emp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create Employee
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody CreateEmployeeDTO dto) {
        employeeValidator.validateCreateDTO(dto);

        Person person = personRepository.findById(dto.getPersonId()).get();
        Department department = departmentRepository.findById(dto.getDepartmentId()).get();
        Position position = positionRepository.findById(dto.getPositionId()).get();
        Company company = companyRepository.findById(dto.getCompanyId()).get();

        Employee manager = null;
        if (dto.getManagerId() != null) {
            manager = employeeRepository.findById(dto.getManagerId()).get();
        }

        Employee employee = EmployeeMapper.toEntity(dto, person, department, position, company, manager);
        Employee saved = employeeService.save(employee);

        return ResponseEntity.ok(EmployeeMapper.toDTO(saved));
    }

    // PUT - Update Employee
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateEmployeeDTO dto) {
        Optional<Employee> existingOpt = employeeService.findById(id);
        if (existingOpt.isEmpty()) return ResponseEntity.notFound().build();

        employeeValidator.validateUpdateDTO(dto);

        Employee existing = existingOpt.get();

        Person person = (dto.getPersonId() != null) ? personRepository.findById(dto.getPersonId()).get() : null;
        Department department = (dto.getDepartmentId() != null) ? departmentRepository.findById(dto.getDepartmentId()).get() : null;
        Position position = (dto.getPositionId() != null) ? positionRepository.findById(dto.getPositionId()).get() : null;
        Company company = (dto.getCompanyId() != null) ? companyRepository.findById(dto.getCompanyId()).get() : null;
        Employee manager = (dto.getManagerId() != null) ? employeeRepository.findById(dto.getManagerId()).get() : null;

        EmployeeMapper.updateEntity(existing, dto, person, department, position, company, manager);
        Employee updated = employeeService.save(existing);

        return ResponseEntity.ok(EmployeeMapper.toDTO(updated));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        Optional<Employee> existingEmployee = employeeService.findById(id);
        if (existingEmployee.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Additional GETs (DTO views)
    @GetMapping("/active")
    public List<EmployeeDTO> getActiveEmployees() {
        return employeeService.findByIsActiveTrue()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/inactive")
    public List<EmployeeDTO> getInactiveEmployees() {
        return employeeService.findByIsActiveFalse()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/manager/{managerId}")
    public List<EmployeeDTO> getEmployeesByManager(@PathVariable Long managerId) {
        return employeeService.findByManagerId(managerId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/department/{departmentId}")
    public List<EmployeeDTO> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return employeeService.findByDepartmentId(departmentId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/position/{positionId}")
    public List<EmployeeDTO> getEmployeesByPosition(@PathVariable Long positionId) {
        return employeeService.findByPositionId(positionId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/company/{companyId}")
    public List<EmployeeDTO> getEmployeesByCompany(@PathVariable Long companyId) {
        return employeeService.findByCompanyId(companyId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/hired-before/{date}")
    public List<EmployeeDTO> getEmployeesHiredBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByHireDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/ended-before/{date}")
    public List<EmployeeDTO> getEmployeesEmploymentEndedBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByEmploymentEndDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }
}
