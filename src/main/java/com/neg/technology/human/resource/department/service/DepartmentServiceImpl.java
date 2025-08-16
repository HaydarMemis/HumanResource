package com.neg.technology.human.resource.department.service;

import com.neg.technology.human.resource.department.model.entity.Department;
import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.department.repository.DepartmentRepository;
import com.neg.technology.human.resource.exception.ResourceNotFoundException;
import com.neg.technology.human.resource.utility.Logger;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    public static final String MESSAGE ="Department";
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();
        Department saved = departmentRepository.save(department);
        Logger.logCreated(Department.class, saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public DepartmentResponse updateDepartment(UpdateDepartmentRequest request) {
        Department existing = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));
        existing.setName(request.getName());
        existing.setLocation(request.getLocation());
        Department updated = departmentRepository.save(existing);
        Logger.logUpdated(Department.class, updated.getId(), updated.getName());
        return toResponse(updated);
    }

    @Override
    public void deleteDepartment(IdRequest request) {
        if (!departmentRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException(MESSAGE, request.getId());
        }
        departmentRepository.deleteById(request.getId());
        Logger.logDeleted(Department.class, request.getId());
    }

    @Override
    public DepartmentResponse getDepartmentById(IdRequest request) {
        Department department = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()));
        return toResponse(department);
    }

    @Override
    public DepartmentResponse getDepartmentByName(NameRequest request) {
        Department department = departmentRepository.findByName(request.getName())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getName()));
        return toResponse(department);
    }

    @Override
    public boolean existsByName(NameRequest request) {
        return departmentRepository.existsByName(request.getName());
    }

    @Override
    public DepartmentResponseList getAllDepartments() {
        return new DepartmentResponseList(
                departmentRepository.findAll()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    // ----------------- UTILITY -----------------
    private DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .location(department.getLocation())
                .build();
    }
}
