package com.neg.technology.human.resource.Employee.service;

import com.neg.technology.human.resource.Company.model.request.CompanyIdRequest;
import com.neg.technology.human.resource.Department.model.request.DepartmentIdRequest;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.Position.model.request.PositionIdRequest;
import com.neg.technology.human.resource.Utility.request.*;
import com.neg.technology.human.resource.Employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.Employee.model.response.EmployeeListResponse;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

public interface EmployeeService {

    ResponseEntity<EmployeeResponse> createEmployee(CreateEmployeeRequest request);

    ResponseEntity<EmployeeResponse> updateEmployee(UpdateEmployeeRequest request);

    ResponseEntity<EmployeeResponse> getEmployeeById(IdRequest request);

    ResponseEntity<EmployeeListResponse> getAllEmployees();

    ResponseEntity<Void> deleteEmployee(IdRequest request);

    ResponseEntity<EmployeeListResponse> getActiveEmployees();

    ResponseEntity<EmployeeListResponse> getInactiveEmployees();

    ResponseEntity<EmployeeListResponse> getEmployeesByDepartment(DepartmentIdRequest request);

    ResponseEntity<EmployeeListResponse> getEmployeesByPosition(PositionIdRequest request);

    ResponseEntity<EmployeeListResponse> getEmployeesByCompany(CompanyIdRequest request);

    ResponseEntity<EmployeeListResponse> getEmployeesHiredBefore(DateRequest request);

    ResponseEntity<EmployeeListResponse> getEmployeesEmploymentEndedBefore(DateRequest request);

    Optional<Employee> findEntityById(Long id);

    Optional<Object> findById(Long employeeId);
}
