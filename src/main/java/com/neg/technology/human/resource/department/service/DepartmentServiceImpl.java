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

import java.util.concurrent.Callable;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Mono<DepartmentResponse> createDepartment(CreateDepartmentRequest request) {
        return Mono.fromCallable((Callable<DepartmentResponse>) () -> {
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
        return Mono.fromCallable((Callable<Department>) () ->
                departmentRepository.findById(request.getId())
                        .orElseThrow(ResourceNotFoundException::departmentNotFound)
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
                throw ResourceNotFoundException.departmentNotFound();
            }
            departmentRepository.deleteById(request.getId());
            Logger.logDeleted(Department.class, request.getId());
        });
    }

    @Override
    public Mono<DepartmentResponse> getDepartmentById(IdRequest request) {
        return Mono.fromCallable((Callable<DepartmentResponse>) () ->
                toResponse(departmentRepository.findById(request.getId())
                        .orElseThrow(ResourceNotFoundException::departmentNotFound))
        );
    }

    @Override
    public Mono<DepartmentResponse> getDepartmentByName(NameRequest request) {
        return Mono.fromCallable((Callable<DepartmentResponse>) () ->
                toResponse(departmentRepository.findByName(request.getName())
                        .orElseThrow(ResourceNotFoundException::departmentNameNotFound))
        );
    }

    @Override
    public Mono<Boolean> existsByName(NameRequest request) {
        return Mono.fromCallable(() -> departmentRepository.existsByName(request.getName()));
    }

    @Override
    public Mono<DepartmentResponseList> getAllDepartments() {
        return Mono.fromCallable((Callable<DepartmentResponseList>) () ->
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
