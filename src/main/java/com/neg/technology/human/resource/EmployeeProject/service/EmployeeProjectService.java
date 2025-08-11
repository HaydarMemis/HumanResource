package com.neg.technology.human.resource.EmployeeProject.service;

import com.neg.technology.human.resource.EmployeeProject.model.entity.EmployeeProject;

import java.util.List;
import java.util.Optional;

public interface EmployeeProjectService {
    EmployeeProject save(EmployeeProject employeeProject);

    Optional<EmployeeProject> findById(Long id);

    List<EmployeeProject> findAll();

    List<EmployeeProject> findByEmployeeId(Long employeeId);

    List<EmployeeProject> findByProjectId(Long projectId);

    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    void deleteById(Long id);

    void deleteByEmployeeId(Long employeeId);

    void deleteByProjectId(Long projectId);

    EmployeeProject update(Long id, EmployeeProject employeeProject);
}
