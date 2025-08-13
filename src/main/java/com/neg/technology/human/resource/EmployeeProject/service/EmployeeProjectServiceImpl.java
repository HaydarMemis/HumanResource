package com.neg.technology.human.resource.EmployeeProject.service;

import com.neg.technology.human.resource.Utility.RequestLogger;
import com.neg.technology.human.resource.EmployeeProject.model.entity.EmployeeProject;
import com.neg.technology.human.resource.EmployeeProject.model.mapper.EmployeeProjectMapper;
import com.neg.technology.human.resource.EmployeeProject.model.request.CreateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.request.UpdateEmployeeProjectRequest;
import com.neg.technology.human.resource.EmployeeProject.model.response.EmployeeProjectResponse;
import com.neg.technology.human.resource.EmployeeProject.repository.EmployeeProjectRepository;
import com.neg.technology.human.resource.Employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.Project.repository.ProjectRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Utility.request.IdRequest;
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
                .toList();
    }

    @Override
    public Optional<EmployeeProjectResponse> getEmployeeProjectById(IdRequest request) {
        return employeeProjectRepository.findById(request.getId())
                .map(EmployeeProjectMapper::toDTO);
    }

    @Override
    public EmployeeProjectResponse createEmployeeProject(CreateEmployeeProjectRequest dto) {
        EmployeeProject entity = mapToEntity(dto);
        EmployeeProject saved = employeeProjectRepository.save(entity);
        RequestLogger.logCreated(EmployeeProject.class, saved.getId(), "EmployeeProject");
        return EmployeeProjectMapper.toDTO(saved);
    }

    private EmployeeProject mapToEntity(CreateEmployeeProjectRequest dto) {
        var employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        var project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        return EmployeeProjectMapper.toEntity(dto, employee, project);
    }

    @Override
    public Optional<EmployeeProjectResponse> updateEmployeeProject(UpdateEmployeeProjectRequest dto) {
        var employee = dto.getEmployeeId() != null
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        var project = dto.getProjectId() != null
                ? projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"))
                : null;

        return employeeProjectRepository.findById(dto.getId())
                .map(existing -> {
                    EmployeeProjectMapper.updateEntity(existing, dto, employee, project);
                    EmployeeProject updated = employeeProjectRepository.save(existing);
                    RequestLogger.logUpdated(EmployeeProject.class, updated.getId(), "EmployeeProject");
                    return EmployeeProjectMapper.toDTO(updated);
                });
    }

    @Override
    public void deleteEmployeeProject(Long id) {
        if (!employeeProjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee Project", id);
        }
        employeeProjectRepository.deleteById(id);
        RequestLogger.logDeleted(EmployeeProject.class, id);
    }

    @Override
    public void deleteByEmployeeId(Long employeeId) {
        if (!employeeProjectRepository.existsByEmployee_Id(employeeId)) {
            throw new ResourceNotFoundException("Employee Project", employeeId);
        }
        employeeProjectRepository.deleteByEmployee_Id(employeeId);
        RequestLogger.logDeleted(EmployeeProject.class, employeeId);
    }

    @Override
    public void deleteByProjectId(Long projectId) {
        if (!employeeProjectRepository.existsByProject_Id(projectId)) {
            throw new ResourceNotFoundException("Employee Project", projectId);
        }
        employeeProjectRepository.deleteByProject_Id(projectId);
        RequestLogger.logDeleted(EmployeeProject.class, projectId);
    }

    @Override
    public List<EmployeeProjectResponse> getByEmployeeId(Long employeeId) {
        return employeeProjectRepository.findByEmployeeId(employeeId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    @Override
    public List<EmployeeProjectResponse> getByProjectId(Long projectId) {
        return employeeProjectRepository.findByProjectId(projectId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    @Override
    public boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId) {
        return employeeProjectRepository.existsByEmployee_IdAndProject_Id(employeeId, projectId);
    }
}
