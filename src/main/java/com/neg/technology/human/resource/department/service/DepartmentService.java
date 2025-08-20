package com.neg.technology.human.resource.department.service;

import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;
import reactor.core.publisher.Mono;

public interface DepartmentService {

    Mono<DepartmentResponse> createDepartment(CreateDepartmentRequest request);

    Mono<DepartmentResponse> updateDepartment(UpdateDepartmentRequest request);

    Mono<Void> deleteDepartment(IdRequest request);

    Mono<DepartmentResponse> getDepartmentById(IdRequest request);

    Mono<DepartmentResponse> getDepartmentByName(NameRequest request);

    Mono<Boolean> existsByName(NameRequest request);

    Mono<DepartmentResponseList> getAllDepartments();
}
