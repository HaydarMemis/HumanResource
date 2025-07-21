package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.EmployeeProject;
import com.neg.hr.human.resouce.repository.EmployeeProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeProjecetService implements EmployeeProjectInterface{
    private final EmployeeProjectRepository employeeProjectRepository;

    public EmployeeProject save(EmployeeProject employeeProject) {
        return employeeProjectRepository.save(employeeProject);
    }

    public EmployeeProject findById(Long id) {
        return employeeProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id " + id));
    }

    public List<EmployeeProject> findAll() {
        return employeeProjectRepository.findAll();
    }

    public void delete(Long id) {
        employeeProjectRepository.deleteById(id);
    }
    public EmployeeProject update(Long id, EmployeeProject employeeProject) {
        EmployeeProject existing = findById(id);
        existing.setEmployee(employeeProject.getEmployee());
        existing.setProject(employeeProject.getProject());
        return employeeProjectRepository.save(existing);
    }
}
