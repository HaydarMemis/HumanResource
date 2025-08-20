package com.neg.technology.human.resource.department.validator;

import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class DepartmentValidator {

    private final DepartmentRepository departmentRepository;

    public DepartmentValidator(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public Mono<Void> validateCreate(CreateDepartmentRequest dto) {
        return Mono.fromCallable(() -> {
                    if (!StringUtils.hasText(dto.getName())) {
                        throw new IllegalArgumentException("Department name must not be empty");
                    }
                    return dto;
                })
                .then(Mono.defer(() -> {
                    boolean exists = departmentRepository.existsByName(dto.getName());
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Department name already exists"));
                    }
                    return Mono.empty();
                }))
                .then();
    }

    public Mono<Void> validateUpdate(UpdateDepartmentRequest dto) {
        return Mono.fromCallable(() -> {
                    if (!StringUtils.hasText(dto.getName())) {
                        throw new IllegalArgumentException("Department name must not be empty");
                    }
                    return dto;
                })
                .then(Mono.defer(() -> {
                    return departmentRepository.findByName(dto.getName())
                            .map(existing -> {
                                if (!existing.getId().equals(dto.getId())) {
                                    throw new IllegalArgumentException("Department name already exists");
                                }
                                return Mono.<Void>empty();
                            })
                            .orElse(Mono.empty());
                }))
                .then();
    }
}