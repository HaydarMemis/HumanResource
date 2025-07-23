package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.EmployeeProject;
import com.neg.hr.human.resouce.repository.EmployeeProjectRepository;
import jakarta.persistence.EntityNotFoundException;
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
        employeeProjectRepository.deleteById(id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
         employeeProjectRepository.deleteByEmployeeId(employeeId);

    }

    @Override
    public void deleteByProjectId(Long projectId) {
        employeeProjectRepository.deleteByProjectId(projectId);


    }

    @Override
    public EmployeeProject update(Long id, EmployeeProject employeeProject) {
        EmployeeProject existing = employeeProjectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("EmployeeProject not found with id " + id));

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
