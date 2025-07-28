package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.business.BusinessLogger;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
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
}
