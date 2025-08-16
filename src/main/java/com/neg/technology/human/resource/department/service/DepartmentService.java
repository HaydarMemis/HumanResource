package com.neg.technology.human.resource.department.service;

import com.neg.technology.human.resource.department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.NameRequest;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse updateDepartment(UpdateDepartmentRequest request);

    void deleteDepartment(IdRequest request);

    DepartmentResponse getDepartmentById(IdRequest request);

    DepartmentResponse getDepartmentByName(NameRequest request);

    boolean existsByName(NameRequest request);

    DepartmentResponseList getAllDepartments();
}
