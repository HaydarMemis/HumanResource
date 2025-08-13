package com.neg.technology.human.resource.Employee.service;

import com.neg.technology.human.resource.Utility.RequestLogger;
import com.neg.technology.human.resource.Company.repository.CompanyRepository;
import com.neg.technology.human.resource.Department.repository.DepartmentRepository;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.model.mapper.EmployeeMapper;
import com.neg.technology.human.resource.Employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.Utility.request.DepartmentIdRequest;
import com.neg.technology.human.resource.Employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.Employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Person.repository.PersonRepository;
import com.neg.technology.human.resource.Position.repository.PositionRepository;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.DateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private String message = "Employee";

    private final EmployeeRepository employeeRepository;
    private final PersonRepository personRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final CompanyRepository companyRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               PersonRepository personRepository,
                               DepartmentRepository departmentRepository,
                               PositionRepository positionRepository,
                               CompanyRepository companyRepository) {
        this.employeeRepository = employeeRepository;
        this.personRepository = personRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public ResponseEntity<EmployeeResponse> createEmployee(CreateEmployeeRequest request) {
        Employee employee = EmployeeMapper.toEntity(
                request,
                personRepository.findById(request.getPersonId()).orElseThrow(() -> new ResourceNotFoundException("Person", request.getPersonId())),
                departmentRepository.findById(request.getDepartmentId()).orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId())),
                positionRepository.findById(request.getPositionId()).orElseThrow(() -> new ResourceNotFoundException("Position", request.getPositionId())),
                companyRepository.findById(request.getCompanyId()).orElseThrow(() -> new ResourceNotFoundException("Company", request.getCompanyId())),
                request.getManagerId() != null ? employeeRepository.findById(request.getManagerId()).orElse(null) : null
        );

        Employee saved = employeeRepository.save(employee);
        RequestLogger.logEmployeeCreated(saved.getId(), saved.getPerson().getFirstName() + " " + saved.getPerson().getLastName());
        return ResponseEntity.ok(EmployeeMapper.toDTO(saved));
    }

    @Override
    public ResponseEntity<EmployeeResponse> updateEmployee(UpdateEmployeeRequest request) {
        Employee existing = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getId()));

        EmployeeMapper.updateEntity(
                existing,
                request,
                request.getPersonId() != null ? personRepository.findById(request.getPersonId()).orElse(null) : null,
                request.getDepartmentId() != null ? departmentRepository.findById(request.getDepartmentId()).orElse(null) : null,
                request.getPositionId() != null ? positionRepository.findById(request.getPositionId()).orElse(null) : null,
                request.getCompanyId() != null ? companyRepository.findById(request.getCompanyId()).orElse(null) : null,
                request.getManagerId() != null ? employeeRepository.findById(request.getManagerId()).orElse(null) : null
        );

        Employee updated = employeeRepository.save(existing);
        RequestLogger.logEmployeeUpdated(updated.getId(), updated.getPerson().getFirstName() + " " + updated.getPerson().getLastName());
        return ResponseEntity.ok(EmployeeMapper.toDTO(updated));
    }

    @Override
    public ResponseEntity<EmployeeResponse> getEmployeeById(IdRequest request) {
        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getId()));
        return ResponseEntity.ok(EmployeeMapper.toDTO(employee));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<Void> deleteEmployee(IdRequest request) {
        Employee employee = employeeRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(message, request.getId()));
        employeeRepository.delete(employee);
        RequestLogger.logEmployeeDeleted(employee.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getActiveEmployees() {
        List<Employee> employees = employeeRepository.findByIsActiveTrue();
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getInactiveEmployees() {
        List<Employee> employees = employeeRepository.findByIsActiveFalse();
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getEmployeesByDepartment(DepartmentIdRequest request) {
        List<Employee> employees = employeeRepository.findByDepartmentId(request.getDepartmentId());
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getEmployeesByPosition(IdRequest request) {
        List<Employee> employees = employeeRepository.findByPositionId(request.getId());
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getEmployeesByCompany(IdRequest request) {
        List<Employee> employees = employeeRepository.findByCompanyId(request.getId());
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getEmployeesHiredBefore(DateRequest request) {
        LocalDateTime date = LocalDateTime.parse(request.getDate());
        List<Employee> employees = employeeRepository.findByHireDateBefore(date);
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public ResponseEntity<EmployeeListResponse> getEmployeesEmploymentEndedBefore(DateRequest request) {
        LocalDateTime date = LocalDateTime.parse(request.getDate());
        List<Employee> employees = employeeRepository.findByEmploymentEndDateBefore(date);
        return ResponseEntity.ok(new EmployeeListResponse(EmployeeMapper.toDTO(employees)));
    }

    @Override
    public Optional<Employee> findEntityById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Object> findById(Long employeeId) {
        return Optional.empty();
    }
}
