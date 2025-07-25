package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.EmployeeProject;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.EmployeeProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeProjectService implements EmployeeProjectInterface {

    private final EmployeeProjectRepository employeeProjectRepository;

    @Override
    public EmployeeProject save(EmployeeProject employeeProject) {
        return employeeProjectRepository.save(employeeProject);
    }

    @Override
    public Optional<EmployeeProject> findById(Long id) {
        return employeeProjectRepository.findById(id);
    }

    @Override
    public List<EmployeeProject> findAll() {
        return employeeProjectRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        if (!employeeProjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee Project", id);
        }
        employeeProjectRepository.deleteById(id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        if (!employeeProjectRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee Project", employeeId);
        }
         employeeProjectRepository.deleteByEmployeeId(employeeId);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        if (!employeeProjectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Employee Project", projectId);
        }
        employeeProjectRepository.deleteByProjectId(projectId);
    }

    @Override
    public EmployeeProject update(Long id, EmployeeProject employeeProject) {
        EmployeeProject existing = employeeProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Project", id));

        existing.setEmployee(employeeProject.getEmployee());
        existing.setProject(employeeProject.getProject());
        return employeeProjectRepository.save(existing);
    }

    @Override
    public List<EmployeeProject> findByEmployeeId(Long employeeId) {
        return employeeProjectRepository.findByEmployeeId(employeeId);
    }

    @Override
    public List<EmployeeProject> findByProjectId(Long projectId) {
        return employeeProjectRepository.findByProjectId(projectId);
    }

    @Override
    public boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        return employeeProjectRepository.existsByEmployeeIdAndProjectId(employeeId, projectId);
    }
}
