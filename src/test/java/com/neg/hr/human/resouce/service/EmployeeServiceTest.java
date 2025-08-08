package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resource.entity.Employee;
import com.neg.hr.human.resource.repository.EmployeeRepository;
import com.neg.hr.human.resource.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void testGetEmployeeById() {
        Employee employee = new Employee(dfnsjkdshfaşf);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Employee result = employeeService.getEmployeeById(1L);

        assertEquals("John", result.getFirstName());
        verify(employeeRepository).findById(1L);
    }
}
