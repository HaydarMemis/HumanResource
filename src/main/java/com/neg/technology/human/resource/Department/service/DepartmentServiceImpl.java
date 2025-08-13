package com.neg.technology.human.resource.Department.service;

import com.neg.technology.human.resource.Department.model.entity.Department;
import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.Department.repository.DepartmentRepository;
import com.neg.technology.human.resource.Exception.ResourceNotFoundException;
import com.neg.technology.human.resource.Utility.RequestLogger;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

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
        RequestLogger.logCreated(Department.class, saved.getId(), saved.getName());
        return toResponse(saved);
    }

    @Override
    public DepartmentResponse updateDepartment(UpdateDepartmentRequest request) {
        Department existing = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getId()));
        existing.setName(request.getName());
        existing.setLocation(request.getLocation());
        Department updated = departmentRepository.save(existing);
        RequestLogger.logUpdated(Department.class, updated.getId(), updated.getName());
        return toResponse(updated);
    }

    @Override
    public void deleteDepartment(IdRequest request) {
        if (!departmentRepository.existsById(request.getId())) {
            throw new ResourceNotFoundException("Department", request.getId());
        }
        departmentRepository.deleteById(request.getId());
        RequestLogger.logDeleted(Department.class, request.getId());
    }

    @Override
    public DepartmentResponse getDepartmentById(IdRequest request) {
        Department department = departmentRepository.findById(request.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getId()));
        return toResponse(department);
    }

    @Override
    public DepartmentResponse getDepartmentByName(NameRequest request) {
        Department department = departmentRepository.findByName(request.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.getName()));
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
                        .collect(Collectors.toList())
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
