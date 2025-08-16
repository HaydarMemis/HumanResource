package com.neg.technology.human.resource.controller;

import com.neg.technology.human.resource.employee.controller.EmployeeController;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeListResponse;
import com.neg.technology.human.resource.employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.employee.service.EmployeeService;
import com.neg.technology.human.resource.utility.module.entity.request.DateRequest;
import com.neg.technology.human.resource.department.model.request.DepartmentIdRequest;
import com.neg.technology.human.resource.utility.module.entity.request.IdRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @Test
    void createEmployee_ShouldReturnCreatedEmployee() {
        // Given
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        EmployeeResponse mockResponse = EmployeeResponse.builder().id(1L).build();
        when(employeeService.createEmployee(request))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeResponse> response = employeeController.createEmployee(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(employeeService, times(1)).createEmployee(request);
    }

    @Test
    void getEmployeeById_ShouldReturnEmployee() {
        // Given
        IdRequest request = new IdRequest(1L);
        EmployeeResponse mockResponse = EmployeeResponse.builder().id(1L).build();
        when(employeeService.getEmployeeById(request))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeResponse> response = employeeController.getEmployeeById(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {
        // Given
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setId(1L);
        EmployeeResponse mockResponse = EmployeeResponse.builder().id(1L).build();
        when(employeeService.updateEmployee(request))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeResponse> response = employeeController.updateEmployee(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void deleteEmployee_ShouldReturnNoContent() {
        // Given
        IdRequest request = new IdRequest(1L);
        when(employeeService.deleteEmployee(request))
                .thenReturn(ResponseEntity.noContent().build());

        // When
        ResponseEntity<Void> response = employeeController.deleteEmployee(request);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(employeeService, times(1)).deleteEmployee(request);
    }

    @Test
    void getAllEmployees_ShouldReturnEmployeeList() {
        // Given
        EmployeeResponse mockEmployee = EmployeeResponse.builder().id(1L).build();
        EmployeeListResponse mockResponse = new EmployeeListResponse(List.of(mockEmployee));
        when(employeeService.getAllEmployees())
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeListResponse> response = employeeController.getAllEmployees();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getEmployees().size());
    }

    @Test
    void getActiveEmployees_ShouldReturnActiveEmployees() {
        // Given
        EmployeeResponse mockEmployee = EmployeeResponse.builder().id(1L).build();
        EmployeeListResponse mockResponse = new EmployeeListResponse(List.of(mockEmployee));
        when(employeeService.getActiveEmployees())
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeListResponse> response = employeeController.getActiveEmployees();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getEmployees().size());
    }

    @Test
    void getEmployeesByDepartment_ShouldReturnDepartmentEmployees() {
        // Given
        DepartmentIdRequest request = new DepartmentIdRequest(1L);
        EmployeeResponse mockEmployee = EmployeeResponse.builder().id(1L).build();
        EmployeeListResponse mockResponse = new EmployeeListResponse(List.of(mockEmployee));
        when(employeeService.getEmployeesByDepartment(request))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeListResponse> response = employeeController.getEmployeesByDepartment(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getEmployees().size());
    }

    @Test
    void getEmployeesHiredBefore_ShouldReturnEmployees() {
        // Given
        DateRequest request = new DateRequest(LocalDateTime.now().toString());
        EmployeeResponse mockEmployee = EmployeeResponse.builder().id(1L).build();
        EmployeeListResponse mockResponse = new EmployeeListResponse(List.of(mockEmployee));
        when(employeeService.getEmployeesHiredBefore(request))
                .thenReturn(ResponseEntity.ok(mockResponse));

        // When
        ResponseEntity<EmployeeListResponse> response = employeeController.getEmployeesHiredBefore(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getEmployees().size());
    }
}