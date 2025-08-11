package com.neg.technology.human.resource.EmployeeProject.service;

import com.neg.technology.human.resource.business.BusinessLogger;
import com.neg.technology.human.resource.EmployeeProject.entity.EmployeeProject;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.EmployeeProject.repository.EmployeeProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeProjectServiceImpl implements EmployeeProjectService {

    private final EmployeeProjectRepository employeeProjectRepository;

    @Override
    public EmployeeProject save(EmployeeProject employeeProject) {
        EmployeeProject saved = employeeProjectRepository.save(employeeProject);
        BusinessLogger.logCreated(EmployeeProject.class, saved.getId(), "EmployeeProject");
        return saved;
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
        BusinessLogger.logDeleted(EmployeeProject.class, id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        if (!employeeProjectRepository.existsByEmployee_Id(employeeId)) {
            throw new ResourceNotFoundException("Employee Project", employeeId);
        }
        employeeProjectRepository.deleteByEmployee_Id(employeeId);
        BusinessLogger.logDeleted(EmployeeProject.class, employeeId);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        if (!employeeProjectRepository.existsByProject_Id(projectId)) {
            throw new ResourceNotFoundException("Employee Project", projectId);
        }
        employeeProjectRepository.deleteByProject_Id(projectId);
        BusinessLogger.logDeleted(EmployeeProject.class, projectId);
    }

    @Override
    public EmployeeProject update(Long id, EmployeeProject employeeProject) {
        EmployeeProject existing = employeeProjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee Project", id));

        existing.setEmployee(employeeProject.getEmployee());
        existing.setProject(employeeProject.getProject());
        EmployeeProject updated = employeeProjectRepository.save(existing);
        BusinessLogger.logUpdated(EmployeeProject.class, updated.getId(), "EmployeeProject");
        return updated;
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
        return employeeProjectRepository.existsByEmployee_IdAndProject_Id(employeeId, projectId);
    }
}
