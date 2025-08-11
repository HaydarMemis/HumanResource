package com.neg.technology.human.resource.Employee.service;

import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Company.repository.CompanyRepository;
import com.neg.technology.human.resource.Department.model.entity.Department;
import com.neg.technology.human.resource.Department.repository.DepartmentRepository;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.Person.model.entity.Person;
import com.neg.technology.human.resource.Person.repository.PersonRepository;
import com.neg.technology.human.resource.Position.model.entity.Position;
import com.neg.technology.human.resource.Position.repository.PositionRepository;
import com.neg.technology.human.resource.Business.BusinessLogger;
import com.neg.technology.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.technology.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.hr.human.resource.entity.*;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Employee.model.mapper.EmployeeMapper;
import com.neg.hr.human.resource.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

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
    public Optional<Employee> findByPersonId(Long personId) {
        return employeeRepository.findByPersonId(personId);
    }

    @Override
    public List<Employee> findByManagerId(Long managerId) {
        return employeeRepository.findByManagerId(managerId);
    }

    @Override
    public List<Employee> findByDepartmentId(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    @Override
    public List<Employee> findByPositionId(Long positionId) {
        return employeeRepository.findByPositionId(positionId);
    }

    @Override
    public List<Employee> findByCompanyId(Long companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }

    @Override
    public List<Employee> findByIsActiveTrue() {
        return employeeRepository.findByIsActiveTrue();
    }

    @Override
    public List<Employee> findByIsActiveFalse() {
        return employeeRepository.findByIsActiveFalse();
    }

    @Override
    public List<Employee> findByHireDateBefore(LocalDateTime date) {
        return employeeRepository.findByHireDateBefore(date);
    }

    @Override
    public List<Employee> findByEmploymentEndDateBefore(LocalDateTime date) {
        return employeeRepository.findByEmploymentEndDateBefore(date);
    }

    @Override
    public List<Employee> findByPersonIdIn(List<Long> personIds) {
        return employeeRepository.findByPersonIdIn(personIds);
    }

    @Override
    public boolean existsByManagerId(Long managerId) {
        return employeeRepository.existsByManagerId(managerId);
    }

    @Override
    public Employee save(Employee employee) {
        Employee saved = employeeRepository.save(employee);

        String fullName = saved.getPerson().getFirstName() + " " + saved.getPerson().getLastName();
        BusinessLogger.logEmployeeCreated(saved.getId(), fullName);

        return saved;
    }

    @Override
    public void deleteById(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
        BusinessLogger.logEmployeeDeleted(id);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee update(Long id, Employee employee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        existing.setCompany(employee.getCompany());
        existing.setHireDate(employee.getHireDate());
        existing.setEmploymentStartDate(employee.getEmploymentStartDate());
        existing.setEmploymentEndDate(employee.getEmploymentEndDate());
        existing.setIsActive(employee.getIsActive());
        existing.setDepartment(employee.getDepartment());
        existing.setPosition(employee.getPosition());
        existing.setPerson(employee.getPerson());
        existing.setManager(employee.getManager());
        existing.setRegistrationNumber(employee.getRegistrationNumber());

        Employee updated = employeeRepository.save(existing);

        String fullName = updated.getPerson().getFirstName() + " " + updated.getPerson().getLastName();
        BusinessLogger.logEmployeeUpdated(updated.getId(), fullName);

        return updated;
    }

    @Override
    public Employee createEmployee(CreateEmployeeRequestDTO dto) {
        Person person = personRepository.findById(dto.getPersonId())
                .orElseThrow(() -> new ResourceNotFoundException("Person", dto.getPersonId()));
        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
        Position position = positionRepository.findById(dto.getPositionId())
                .orElseThrow(() -> new ResourceNotFoundException("Position", dto.getPositionId()));
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company", dto.getCompanyId()));

        Employee manager = null;
        if (dto.getManagerId() != null) {
            manager = employeeRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee(manager)", dto.getManagerId()));
        }

        Employee employee = EmployeeMapper.toEntity(dto, person, department, position, company, manager);
        Employee saved = employeeRepository.save(employee);

        String fullName = saved.getPerson().getFirstName() + " " + saved.getPerson().getLastName();
        BusinessLogger.logEmployeeCreated(saved.getId(), fullName);

        return saved;
    }

    @Override
    public Employee updateEmployee(UpdateEmployeeRequestDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("Employee ID must be provided for update.");
        }

        Employee existing = employeeRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", dto.getId()));

        Person person = null;
        if (dto.getPersonId() != null) {
            person = personRepository.findById(dto.getPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Person", dto.getPersonId()));
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
        }

        Position position = null;
        if (dto.getPositionId() != null) {
            position = positionRepository.findById(dto.getPositionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Position", dto.getPositionId()));
        }

        Company company = null;
        if (dto.getCompanyId() != null) {
            company = companyRepository.findById(dto.getCompanyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Company", dto.getCompanyId()));
        }

        Employee manager = null;
        if (dto.getManagerId() != null) {
            manager = employeeRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee(manager)", dto.getManagerId()));
        }

        EmployeeMapper.updateEntity(existing, dto, person, department, position, company, manager);

        Employee updated = employeeRepository.save(existing);

        String fullName = updated.getPerson().getFirstName() + " " + updated.getPerson().getLastName();
        BusinessLogger.logEmployeeUpdated(updated.getId(), fullName);

        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return employeeRepository.existsById(id);
    }
}
