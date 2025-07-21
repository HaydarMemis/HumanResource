package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.repository.EmployeeRepository;
import com.neg.hr.human.resouce.repository.LeaveTypeRepository;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public class LeaveTypeServiceImp {

    private final LeaveTypeRepository leaveTypeRepository;

    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee findById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }

    public Employee update(Long id, Employee employee) {
        Employee existing = findById(id);
        existing.setHireDate(employee.getHireDate());
        existing.setPerson(employee.getPerson());
        existing.setCompany(employee.getCompany());
        existing.setPosition(employee.getPosition());
        return employeeRepository.save(existing);
    }
}
