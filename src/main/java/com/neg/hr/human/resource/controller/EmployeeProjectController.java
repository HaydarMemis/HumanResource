package com.neg.hr.human.resource.controller;

import com.neg.hr.human.resource.dto.IdRequest;
import com.neg.hr.human.resource.validator.EmployeeProjectValidator;
import com.neg.hr.human.resource.dto.create.CreateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeProjectRequestDTO;
import com.neg.hr.human.resource.dto.EmployeeProjectEntityDTO;
import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.entity.EmployeeProject;
import com.neg.hr.human.resource.entity.Project;
import com.neg.hr.human.resource.mapper.EmployeeProjectMapper;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.repository.ProjectRepository;
import com.neg.hr.human.resource.service.EmployeeProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectService employeeProjectService;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProjectValidator employeeProjectValidator;

    public EmployeeProjectController(EmployeeProjectService employeeProjectService,
                                     EmployeeRepository employeeRepository,
                                     ProjectRepository projectRepository,
                                     EmployeeProjectValidator employeeProjectValidator) {
        this.employeeProjectService = employeeProjectService;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.employeeProjectValidator = employeeProjectValidator;
    }

    // POST - get all
    @PostMapping("/getAll")
    public List<EmployeeProjectEntityDTO> getAll() {
        return employeeProjectService.findAll()
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // POST - get by ID
    @PostMapping("/getById")
    public ResponseEntity<EmployeeProjectEntityDTO> getById(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.findById(request.getId())
                .map(EmployeeProjectMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - create
    @PostMapping("/create")
    public ResponseEntity<EmployeeProjectEntityDTO> create(@Valid @RequestBody CreateEmployeeProjectRequestDTO dto) {
        employeeProjectValidator.validateCreateDTO(dto);

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        EmployeeProject entity = EmployeeProjectMapper.toEntity(dto, employee, project);
        EmployeeProject saved = employeeProjectService.save(entity);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(saved));
    }

    @PostMapping("/update")
    public ResponseEntity<EmployeeProjectEntityDTO> update(
            @Valid @RequestBody UpdateEmployeeProjectRequestDTO dto) {

        employeeProjectValidator.validateUpdateDTO(dto.getId(), dto);

        Employee employee = (dto.getEmployeeId() != null)
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        Project project = (dto.getProjectId() != null)
                ? projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"))
                : null;

        // 1️⃣ Get existing entity
        EmployeeProject existing = employeeProjectService.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("EmployeeProject not found with ID: " + dto.getId()));
        // 2️⃣ Update fields
        EmployeeProjectMapper.updateEntity(existing, dto, employee, project);

        // 3️⃣ Save
        EmployeeProject updated = employeeProjectService.update(dto.getId(), existing);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(updated));
    }


    // POST - delete by ID
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteById(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - delete by employee ID
    @PostMapping("/deleteByEmployee")
    public ResponseEntity<Void> deleteByEmployee(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByEmployeeId(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - delete by project ID
    @PostMapping("/deleteByProject")
    public ResponseEntity<Void> deleteByProject(@Valid @RequestBody IdRequest request) {
        employeeProjectService.deleteByProjectId(request.getId());
        return ResponseEntity.noContent().build();
    }

    // POST - get list by employee ID
    @PostMapping("/getByEmployee")
    public List<EmployeeProjectEntityDTO> getByEmployee(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.findByEmployeeId(request.getId())
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // POST - get list by project ID
    @PostMapping("/getByProject")
    public List<EmployeeProjectEntityDTO> getByProject(@Valid @RequestBody IdRequest request) {
        return employeeProjectService.findByProjectId(request.getId())
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // POST - check if exists by employee and project
    @PostMapping("/existsByEmployeeAndProject")
    public boolean existsByEmployeeAndProject(@RequestParam Long employeeId,
                                              @RequestParam Long projectId) {
        return employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId);
    }
}
