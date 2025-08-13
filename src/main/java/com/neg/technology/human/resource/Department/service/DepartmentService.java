package com.neg.technology.human.resource.Department.service;

import com.neg.technology.human.resource.Department.model.request.CreateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.request.UpdateDepartmentRequest;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponse;
import com.neg.technology.human.resource.Department.model.response.DepartmentResponseList;
import com.neg.technology.human.resource.Utility.request.IdRequest;
import com.neg.technology.human.resource.Utility.request.NameRequest;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse updateDepartment(UpdateDepartmentRequest request);

    void deleteDepartment(IdRequest request);

    DepartmentResponse getDepartmentById(IdRequest request);

    DepartmentResponse getDepartmentByName(NameRequest request);

    boolean existsByName(NameRequest request);

    DepartmentResponseList getAllDepartments();
}
