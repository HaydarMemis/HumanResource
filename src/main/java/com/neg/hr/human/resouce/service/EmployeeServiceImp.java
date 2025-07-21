package com.neg.hr.human.resouce.service.impl;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.repository.EmployeeRepository;
import com.neg.hr.human.resouce.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee update(Long id, Employee updatedEmployee) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id " + id));

        existing.setPerson(updatedEmployee.getPerson());
        existing.setCompany(updatedEmployee.getCompany());
        existing.setDepartment(updatedEmployee.getDepartment());
        existing.setPosition(updatedEmployee.getPosition());
        existing.setStartDate(updatedEmployee.getStartDate());
        existing.setEndDate(updatedEmployee.getEndDate());
        existing.setEmploymentType(updatedEmployee.getEmploymentType());

        return employeeRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }
}
