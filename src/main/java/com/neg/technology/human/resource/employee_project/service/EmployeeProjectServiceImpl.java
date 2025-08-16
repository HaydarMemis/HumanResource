package com.neg.technology.human.resource.employee_project.service;

import com.neg.technology.human.resource.employee_project.model.entity.EmployeeProject;
import com.neg.technology.human.resource.employee_project.model.mapper.EmployeeProjectMapper;
import com.neg.technology.human.resource.employee_project.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.employee_project.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.employee_project.repository.EmployeeProjectRepository;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.project.repository.ProjectRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeProjectServiceImpl implements EmployeeProjectService {

    private final EmployeeProjectRepository employeeProjectRepository;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;

    @Override
    public List<EmployeeProjectResponse> getAllEmployeeProjects() {
        return employeeProjectRepository.findAll()
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<EmployeeProjectResponse> getEmployeeProjectById(Long id) {
        return employeeProjectRepository.findById(id)
                .map(EmployeeProjectMapper::toDTO);
    }

    @Override
    public EmployeeProjectResponse createEmployeeProject(CreateEmployeeProjectRequest request) {
        EmployeeProject entity = EmployeeProjectMapper.toEntity(request,
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() -> new ResourceNotFoundException("Employee", request.getEmployeeId())),
                projectRepository.findById(request.getProjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Project", request.getProjectId()))
        );

        EmployeeProject saved = employeeProjectRepository.save(entity);
        Logger.logCreated(EmployeeProject.class, saved.getId(), "EmployeeProject");
        return EmployeeProjectMapper.toDTO(saved);
    }

    @Override
    public Optional<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest request) {
        return employeeProjectRepository.findById(request.getId())
                .map(existing -> {
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
    public void deleteEmployeeProject(Long id) {
        if (!employeeProjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee Project", id);
        }
        employeeProjectRepository.deleteById(id);
        Logger.logDeleted(EmployeeProject.class, id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        if (!employeeProjectRepository.existsByEmployee_Id(employeeId)) {
            throw new ResourceNotFoundException("Employee Project", employeeId);
        }
        employeeProjectRepository.deleteByEmployee_Id(employeeId);
        Logger.logDeleted(EmployeeProject.class, employeeId);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        if (!employeeProjectRepository.existsByProject_Id(projectId)) {
            throw new ResourceNotFoundException("Employee Project", projectId);
        }
        employeeProjectRepository.deleteByProject_Id(projectId);
        Logger.logDeleted(EmployeeProject.class, projectId);
    }

    @Override
    public List<EmployeeProjectResponse> getByEmployeeId(Long employeeId) {
        return employeeProjectRepository.findByEmployeeId(employeeId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeProjectResponse> getByProjectId(Long projectId) {
        return employeeProjectRepository.findByProjectId(projectId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        return employeeProjectRepository.existsByEmployee_IdAndProject_Id(employeeId, projectId);
    }
}
