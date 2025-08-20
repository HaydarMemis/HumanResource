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
import reactor.core.publisher.Mono;

@Service
public class DepartmentServiceImpl implements DepartmentService {
    public static final String MESSAGE = "Department";
    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Mono<DepartmentResponse> createDepartment(CreateDepartmentRequest request) {
        return Mono.fromCallable(() -> {
            Department department = Department.builder()
                    .name(request.getName())
                    .location(request.getLocation())
                    .build();
            Department saved = departmentRepository.save(department);
            Logger.logCreated(Department.class, saved.getId(), saved.getName());
            return toResponse(saved);
        });
    }

    @Override
    public Mono<DepartmentResponse> updateDepartment(UpdateDepartmentRequest request) {
        return Mono.fromCallable(() ->
                departmentRepository.findById(request.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        ).map(existing -> {
            existing.setName(request.getName());
            existing.setLocation(request.getLocation());
            Department updated = departmentRepository.save(existing);
            Logger.logUpdated(Department.class, updated.getId(), updated.getName());
            return toResponse(updated);
        });
    }

    @Override
    public Mono<Void> deleteDepartment(IdRequest request) {
        return Mono.fromRunnable(() -> {
            if (!departmentRepository.existsById(request.getId())) {
                throw new ResourceNotFoundException(MESSAGE, request.getId());
            }
            departmentRepository.deleteById(request.getId());
            Logger.logDeleted(Department.class, request.getId());
        });
    }

    @Override
    public Mono<DepartmentResponse> getDepartmentById(IdRequest request) {
        return Mono.fromCallable(() ->
                departmentRepository.findById(request.getId())
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getId()))
        ).map(this::toResponse);
    }

    @Override
    public Mono<DepartmentResponse> getDepartmentByName(NameRequest request) {
        return Mono.fromCallable(() ->
                departmentRepository.findByName(request.getName())
                        .orElseThrow(() -> new ResourceNotFoundException(MESSAGE, request.getName()))
        ).map(this::toResponse);
    }

    @Override
    public Mono<Boolean> existsByName(NameRequest request) {
        return Mono.fromCallable(() ->
                departmentRepository.existsByName(request.getName())
        );
    }

    @Override
    public Mono<DepartmentResponseList> getAllDepartments() {
        return Mono.fromCallable(() ->
                new DepartmentResponseList(
                        departmentRepository.findAll()
                                .stream()
                                .map(this::toResponse)
                                .toList()
                )
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
