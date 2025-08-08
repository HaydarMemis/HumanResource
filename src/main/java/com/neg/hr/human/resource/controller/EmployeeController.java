package com.neg.hr.human.resource.controller;

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
    public List<EmployeeEntityDTO> getAllEmployees() {
        return employeeService.findAll()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    // GET by ID (DTO)
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeEntityDTO> getEmployeeById(@PathVariable Long id) {
        return employeeService.findById(id)
                .map(emp -> ResponseEntity.ok(EmployeeMapper.toDTO(emp)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create Employee
    @PostMapping
    public ResponseEntity<EmployeeEntityDTO> createEmployee(@Valid @RequestBody CreateEmployeeRequestDTO dto) {
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
    public ResponseEntity<EmployeeEntityDTO> updateEmployee(@PathVariable Long id,
                                                            @Valid @RequestBody UpdateEmployeeRequestDTO dto) {
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
    public List<EmployeeEntityDTO> getActiveEmployees() {
        return employeeService.findByIsActiveTrue()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/inactive")
    public List<EmployeeEntityDTO> getInactiveEmployees() {
        return employeeService.findByIsActiveFalse()
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/manager/{managerId}")
    public List<EmployeeEntityDTO> getEmployeesByManager(@PathVariable Long managerId) {
        return employeeService.findByManagerId(managerId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/department/{departmentId}")
    public List<EmployeeEntityDTO> getEmployeesByDepartment(@PathVariable Long departmentId) {
        return employeeService.findByDepartmentId(departmentId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/position/{positionId}")
    public List<EmployeeEntityDTO> getEmployeesByPosition(@PathVariable Long positionId) {
        return employeeService.findByPositionId(positionId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/company/{companyId}")
    public List<EmployeeEntityDTO> getEmployeesByCompany(@PathVariable Long companyId) {
        return employeeService.findByCompanyId(companyId)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/hired-before/{date}")
    public List<EmployeeEntityDTO> getEmployeesHiredBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByHireDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }

    @GetMapping("/ended-before/{date}")
    public List<EmployeeEntityDTO> getEmployeesEmploymentEndedBefore(@PathVariable String date) {
        LocalDateTime dateTime = LocalDateTime.parse(date);
        return employeeService.findByEmploymentEndDateBefore(dateTime)
                .stream().map(EmployeeMapper::toDTO)
                .toList();
    }
}
