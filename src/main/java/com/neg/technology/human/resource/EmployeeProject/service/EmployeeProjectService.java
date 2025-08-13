package com.neg.technology.human.resource.EmployeeProject.service;

import com.neg.technology.human.resource.EmployeeProject.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.response.EmployeeProjectResponse;

import java.util.List;
import java.util.Optional;

public interface EmployeeProjectService {

    List<EmployeeProjectResponse> getAllEmployeeProjects();

    Optional<EmployeeProjectResponse> getEmployeeProjectById(Long id);

    EmployeeProjectResponse createEmployeeProject(CreateEmployeeProjectRequest request);

    Optional<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest request);

    void deleteEmployeeProject(Long id);

    void deleteByEmployeeId(Long employeeId);

    void deleteByProjectId(Long projectId);

    List<EmployeeProjectResponse> getByEmployeeId(Long employeeId);

    List<EmployeeProjectResponse> getByProjectId(Long projectId);

    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);
}
