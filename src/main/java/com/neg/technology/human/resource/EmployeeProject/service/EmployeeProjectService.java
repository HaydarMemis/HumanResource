package com.neg.technology.human.resource.EmployeeProject.service;

import com.neg.technology.human.resource.EmployeeProject.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.Utility.request.EmployeeIdRequest;
import com.neg.technology.human.resource.Utility.request.EmployeeProjectIdRequest;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.ProjectIdRequest;

import java.util.List;
import java.util.Optional;

public interface EmployeeProjectService {
    List<EmployeeProjectResponse> getAllEmployeeProjects();

    Optional<EmployeeProjectResponse> getEmployeeProjectById(EmployeeProjectIdRequest request);

    EmployeeProjectResponse createEmployeeProject(CreateEmployeeProjectRequest request);

    Optional<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest request);

    void deleteEmployeeProject(EmployeeProjectIdRequest request);

    void deleteByEmployeeId(EmployeeIdRequest request);

    void deleteByProjectId(ProjectIdRequest request);

    List<EmployeeProjectResponse> getByEmployeeId(Long employeeId);

    List<EmployeeProjectResponse> getByProjectId(Long projectId);

    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);
}
