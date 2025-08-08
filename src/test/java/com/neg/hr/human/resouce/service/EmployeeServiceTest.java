package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resource.entity.*;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void testGetEmployeeById() {
        // Arrange: create related entities to simulate DB data
        Person person = Person.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .build();

        Department department = Department.builder()
                .id(10L)
                .name("IT")
                .build();

        Position position = Position.builder()
                .id(20L)
                .title("Software Engineer")
                .build();

        Company company = Company.builder()
                .id(30L)
                .name("Tech Corp")
                .build();

        Employee manager = Employee.builder()
                .id(2L)
                .person(Person.builder().firstName("Jane").lastName("Smith").build())
                .build();

        Employee employee = Employee.builder()
                .id(1L)
                .person(person)
                .department(department)
                .position(position)
                .company(company)
                .manager(manager)
                .registrationNumber("REG123")
                .hireDate(LocalDateTime.of(2020, 1, 1, 9, 0))
                .employmentStartDate(LocalDateTime.of(2020, 1, 1, 9, 0))
                .isActive(true)
                .build();

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        Optional<Employee> result = employeeService.findById(1L);
        // Assert
        Employee foundEmployee = result.orElseThrow();

        assertEquals("John", foundEmployee.getPerson().getFirstName());
        assertEquals("IT", foundEmployee.getDepartment().getName());
        verify(employeeRepository, times(1)).findById(1L);

    }
}
