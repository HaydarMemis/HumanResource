package com.neg.technology.human.resource.employee.service;

import com.neg.technology.human.resource.employee.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeProjectResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EmployeeProjectService {

    Mono<List<EmployeeProjectResponse>> getAllEmployeeProjects();

    Mono<EmployeeProjectResponse> getEmployeeProjectById(Long id);

    Mono<EmployeeProjectResponse> createEmployeeProject(CreateEmployeeProjectRequest request);

    Mono<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest request);

    Mono<Void> deleteEmployeeProject(Long id);

    Mono<Void> deleteByEmployeeId(Long employeeId);

    Mono<Void> deleteByProjectId(Long projectId);

    Mono<List<EmployeeProjectResponse>> getByEmployeeId(Long employeeId);

    Mono<List<EmployeeProjectResponse>> getByProjectId(Long projectId);

    Mono<Boolean> existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);
}