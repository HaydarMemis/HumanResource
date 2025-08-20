package com.neg.technology.human.resource.employee.service.impl;

import com.neg.technology.human.resource.employee.model.entity.EmployeeProject;
import com.neg.technology.human.resource.employee.model.mapper.EmployeeProjectMapper;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.employee.repository.EmployeeProjectRepository;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.employee.service.EmployeeProjectService;
import com.neg.technology.human.resource.company.repository.ProjectRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeProjectServiceImpl implements EmployeeProjectService {

    private final EmployeeProjectRepository employeeProjectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Mono<List<EmployeeProjectResponse>> getAllEmployeeProjects() {
        return Mono.fromCallable(() ->
                employeeProjectRepository.findAll()
                        .stream()
                        .map(EmployeeProjectMapper::toDTO)
                        .toList()
        );
    }

    @Override
    public Mono<EmployeeProjectResponse> getEmployeeProjectById(Long id) {
        return Mono.fromCallable(() ->
                employeeProjectRepository.findById(id)
                        .map(EmployeeProjectMapper::toDTO)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee Project", id))
        );
    }

    @Override
    public Mono<EmployeeProjectResponse> createEmployeeProject(CreateEmployeeProjectRequest request) {
        return Mono.fromCallable(() -> {
            EmployeeProject entity = EmployeeProjectMapper.toEntity(request,
                    employeeRepository.findById(request.getEmployeeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId())),
                    projectRepository.findById(request.getProjectId())
                            .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()))
            );

            EmployeeProject saved = employeeProjectRepository.save(entity);
            Logger.logCreated(EmployeeProject.class, saved.getId(), "EmployeeProject");
            return EmployeeProjectMapper.toDTO(saved);
        });
    }

    @Override
    public Mono<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest request) {
        return Mono.fromCallable(() -> {
            EmployeeProject existing = employeeProjectRepository.findById(request.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee Project", request.getId()));

            EmployeeProjectMapper.updateEntity(existing, request,
                    request.getEmployeeId() != null ? employeeRepository.findById(request.getEmployeeId())
                            .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId()))
                            : null,
                    request.getProjectId() != null ? projectRepository.findById(request.getProjectId())
                            .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()))
                            : null
            );

            EmployeeProject updated = employeeProjectRepository.save(existing);
            Logger.logUpdated(EmployeeProject.class, updated.getId(), "EmployeeProject");
            return EmployeeProjectMapper.toDTO(updated);
        });
    }

    @Override
    public Mono<Void> deleteEmployeeProject(Long id) {
        return Mono.fromRunnable(() -> {
            if (!employeeProjectRepository.existsById(id)) {
                throw new ResourceNotFoundException("Employee Project", id);
            }
            employeeProjectRepository.deleteById(id);
            Logger.logDeleted(EmployeeProject.class, id);
        });
    }

    @Override
    public Mono<Void> deleteByEmployeeId(Long employeeId) {
        return Mono.fromRunnable(() -> {
            if (!employeeProjectRepository.existsByEmployee_Id(employeeId)) {
                throw new ResourceNotFoundException("Employee Project", employeeId);
            }
            employeeProjectRepository.deleteByEmployee_Id(employeeId);
            Logger.logDeleted(EmployeeProject.class, employeeId);
        });
    }

    @Override
    public Mono<Void> deleteByProjectId(Long projectId) {
        return Mono.fromRunnable(() -> {
            if (!employeeProjectRepository.existsByProject_Id(projectId)) {
                throw new ResourceNotFoundException("Employee Project", projectId);
            }
            employeeProjectRepository.deleteByProject_Id(projectId);
            Logger.logDeleted(EmployeeProject.class, projectId);
        });
    }

    @Override
    public Mono<List<EmployeeProjectResponse>> getByEmployeeId(Long employeeId) {
        return Mono.fromCallable(() ->
                employeeProjectRepository.findByEmployeeId(employeeId)
                        .stream()
                        .map(EmployeeProjectMapper::toDTO)
                        .toList()
        );
    }

    @Override
    public Mono<List<EmployeeProjectResponse>> getByProjectId(Long projectId) {
        return Mono.fromCallable(() ->
                employeeProjectRepository.findByProjectId(projectId)
                        .stream()
                        .map(EmployeeProjectMapper::toDTO)
                        .toList()
        );
    }

    @Override
    public Mono<Boolean> existsByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        return Mono.fromCallable(() ->
                employeeProjectRepository.existsByEmployee_IdAndProject_Id(employeeId, projectId)
        );
    }
}