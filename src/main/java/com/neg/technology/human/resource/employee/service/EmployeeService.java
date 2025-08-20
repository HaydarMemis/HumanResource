package com.neg.technology.human.resource.employee.service;

import com.neg.technology.human.resource.company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.department.model.request.DepartmentIdRequest;
import com.neg.technology.human.resource.company.model.request.PositionIdRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.utility.module.entity.request.DateRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.employee.model.response.EmployeeListResponse;
import reactor.core.publisher.Mono;

public interface EmployeeService {

    Mono<EmployeeResponse> createEmployee(CreateEmployeeRequest request);

    Mono<EmployeeResponse> updateEmployee(UpdateEmployeeRequest request);

    Mono<EmployeeResponse> getEmployeeById(IdRequest request);

    Mono<EmployeeListResponse> getAllEmployees();

    Mono<Void> deleteEmployee(IdRequest request);

    Mono<EmployeeListResponse> getActiveEmployees();

    Mono<EmployeeListResponse> getInactiveEmployees();

    Mono<EmployeeListResponse> getEmployeesByDepartment(DepartmentIdRequest request);

    Mono<EmployeeListResponse> getEmployeesByPosition(PositionIdRequest request);

    Mono<EmployeeListResponse> getEmployeesByCompany(CompanyIdRequest request);

    Mono<EmployeeListResponse> getEmployeesHiredBefore(DateRequest request);

    Mono<EmployeeListResponse> getEmployeesEmploymentEndedBefore(DateRequest request);

    Mono<Employee> findEntityById(Long id);

    Mono<Object> findById(Long employeeId);
}