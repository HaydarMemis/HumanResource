package com.neg.hr.human.resouce.service.impl;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.repository.EmployeeRepository;
import com.neg.hr.human.resouce.service.EmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    @Override
    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    @Override
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee update(Long id, Employee employee) {
        Employee existing = findById(id);
        existing.setHireDate(employee.getHireDate());
        existing.setPerson(employee.getPerson());
        existing.setCompany(employee.getCompany());
        existing.setPosition(employee.getPosition());
        return employeeRepository.save(existing);
    }
}
