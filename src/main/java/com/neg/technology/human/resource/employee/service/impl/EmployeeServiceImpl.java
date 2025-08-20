package com.neg.technology.human.resource.employee.service.impl;

import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.department.model.request.DepartmentIdRequest;
import com.neg.technology.human.resource.company.model.request.PositionIdRequest;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.company.repository.CompanyRepository;
import com.neg.technology.human.resource.department.repository.DepartmentRepository;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.mapper.EmployeeMapper;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.utility.module.entity.request.DateRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.person.repository.PersonRepository;
import com.neg.technology.human.resource.company.repository.PositionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    public static final String MESSAGE = "Employee";

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
    public Mono<EmployeeResponse> createEmployee(CreateEmployeeRequest request) {
        return Mono.fromCallable(() -> {
            Employee employee = EmployeeMapper.toEntity(
                    request,
                    personRepository.findById(request.getPersonId())
                            .orElseThrow(() -> new ResourceNotFoundException("Person", request.getPersonId())),
                    departmentRepository.findById(request.getDepartmentId())
                            .orElseThrow(() -> new ResourceNotFoundException("Department", request.getDepartmentId())),
                    positionRepository.findById(request.getPositionId())
                            .orElseThrow(() -> new ResourceNotFoundException("Position", request.getPositionId())),
                    companyRepository.findById(request.getCompanyId())
                            .orElseThrow(() -> new ResourceNotFoundException("Company", request.getCompanyId())),
                    request.getManagerId() != null ? employeeRepository.findById(request.getManagerId()).orElse(null) : null
            );

            Employee saved = employeeRepository.save(employee);
            Logger.logEmployeeCreated(saved.getId(), saved.getPerson().getFirstName() + " " + saved.getPerson().getLastName());
            return EmployeeMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<EmployeeResponse> updateEmployee(UpdateEmployeeRequest request) {
        return Mono.fromCallable(() -> {
            Employee existing = employeeRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));

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
            Logger.logEmployeeUpdated(updated.getId(), updated.getPerson().getFirstName() + " " + updated.getPerson().getLastName());
            return EmployeeMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<EmployeeResponse> getEmployeeById(IdRequest request) {
        return Mono.fromCallable(() ->
                employeeRepository.findById(request.getId())
                        .map(EmployeeMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        );
    }

    @Override
    public Mono<EmployeeListResponse> getAllEmployees() {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findAll();
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<Void> deleteEmployee(IdRequest request) {
        return Mono.fromRunnable(() -> {
            Employee employee = employeeRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));
            employeeRepository.delete(employee);
            Logger.logEmployeeDeleted(employee.getId());
        });
    }

    @Override
    public Mono<EmployeeListResponse> getActiveEmployees() {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findByIsActiveTrue();
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getInactiveEmployees() {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findByIsActiveFalse();
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getEmployeesByDepartment(DepartmentIdRequest request) {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findByDepartmentId(request.getDepartmentId());
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getEmployeesByPosition(PositionIdRequest request) {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findByPositionId(request.getPositionId());
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getEmployeesByCompany(CompanyIdRequest request) {
        return Mono.fromCallable(() -> {
            List<Employee> employees = employeeRepository.findByCompanyId(request.getCompanyId());
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getEmployeesHiredBefore(DateRequest request) {
        return Mono.fromCallable(() -> {
            LocalDateTime date = LocalDateTime.parse(request.getDate());
            List<Employee> employees = employeeRepository.findByHireDateBefore(date);
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<EmployeeListResponse> getEmployeesEmploymentEndedBefore(DateRequest request) {
        return Mono.fromCallable(() -> {
            LocalDateTime date = LocalDateTime.parse(request.getDate());
            List<Employee> employees = employeeRepository.findByEmploymentEndDateBefore(date);
            return new EmployeeListResponse(EmployeeMapper.toDTO(employees));
        });
    }

    @Override
    public Mono<Employee> findEntityById(Long id) {
        return Mono.fromCallable(() ->
                employeeRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, id))
        );
    }

    @Override
    public Mono<Object> findById(Long employeeId) {
        return Mono.fromCallable(() ->
                employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, employeeId))
        );
    }
}