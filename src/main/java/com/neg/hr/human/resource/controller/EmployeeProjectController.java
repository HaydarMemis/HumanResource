package com.neg.hr.human.resource.controller;

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
import com.neg.hr.human.resource.service.impl.EmployeeProjectServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee-projects")
public class EmployeeProjectController {

    private final EmployeeProjectServiceImpl employeeProjectService;
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeProjectValidator employeeProjectValidator;

    public EmployeeProjectController(EmployeeProjectServiceImpl employeeProjectService,
                                     EmployeeRepository employeeRepository,
                                     ProjectRepository projectRepository,
                                     EmployeeProjectValidator employeeProjectValidator) {
        this.employeeProjectService = employeeProjectService;
        this.employeeRepository = employeeRepository;
        this.projectRepository = projectRepository;
        this.employeeProjectValidator = employeeProjectValidator;
    }

    // GET all
    @GetMapping
    public List<EmployeeProjectEntityDTO> getAll() {
        return employeeProjectService.findAll()
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // GET by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeProjectEntityDTO> getById(@PathVariable Long id) {
        return employeeProjectService.findById(id)
                .map(EmployeeProjectMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // POST - Create new record with CreateEmployeeProjectDTO
    @PostMapping
    public ResponseEntity<EmployeeProjectEntityDTO> create(@Valid @RequestBody CreateEmployeeProjectRequestDTO dto) {
        employeeProjectValidator.validateCreateDTO(dto);
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"));
        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"));

        EmployeeProject employeeProject = EmployeeProjectMapper.toEntity(dto, employee, project);
        EmployeeProject saved = employeeProjectService.save(employeeProject);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(saved));
    }

    // PUT - Update with UpdateEmployeeProjectDTO
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeProjectEntityDTO> update(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateEmployeeProjectRequestDTO dto) {
        Optional<EmployeeProject> existingOpt = employeeProjectService.findById(id);
        if (existingOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        employeeProjectValidator.validateUpdateDTO(id, dto);

        EmployeeProject existing = existingOpt.get();

        Employee employee = (dto.getEmployeeId() != null)
                ? employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee ID"))
                : null;

        Project project = (dto.getProjectId() != null)
                ? projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid project ID"))
                : null;

        EmployeeProjectMapper.updateEntity(existing, dto, employee, project);
        EmployeeProject updated = employeeProjectService.save(existing);

        return ResponseEntity.ok(EmployeeProjectMapper.toDTO(updated));
    }

    // DELETE by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        Optional<EmployeeProject> existing = employeeProjectService.findById(id);
        if (existing.isPresent()) {
            employeeProjectService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // GET list by Employee ID
    @GetMapping("/employee/list/{employeeId}")
    public List<EmployeeProjectEntityDTO> getByEmployeeId(@PathVariable Long employeeId) {
        return employeeProjectService.findByEmployeeId(employeeId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // GET list by Project ID
    @GetMapping("/project/list/{projectId}")
    public List<EmployeeProjectEntityDTO> getByProjectId(@PathVariable Long projectId) {
        return employeeProjectService.findByProjectId(projectId)
                .stream()
                .map(EmployeeProjectMapper::toDTO)
                .toList();
    }

    // Check if relationship exists
    @GetMapping("/exists")
    public boolean existsByEmployeeAndProject(@RequestParam Long employeeId,
                                              @RequestParam Long projectId) {
        return employeeProjectService.existsByEmployeeIdAndProjectId(employeeId, projectId);
    }
}
