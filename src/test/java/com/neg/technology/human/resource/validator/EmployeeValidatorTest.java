package com.neg.technology.human.resource.validator;

import com.neg.technology.human.resource.company.repository.CompanyRepository;
import com.neg.technology.human.resource.department.repository.DepartmentRepository;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.employee.validator.EmployeeValidator;
import com.neg.technology.human.resource.person.repository.PersonRepository;
import com.neg.technology.human.resource.company.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeValidatorTest {

    @Mock
    private PersonRepository personRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PositionRepository positionRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private EmployeeValidator validator;

    private CreateEmployeeRequest createRequest;
    private UpdateEmployeeRequest updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = new CreateEmployeeRequest();
        createRequest.setPersonId(1L);
        createRequest.setDepartmentId(1L);
        createRequest.setPositionId(1L);
        createRequest.setCompanyId(1L);
        createRequest.setHireDate(LocalDateTime.now());
        createRequest.setEmploymentStartDate(LocalDateTime.now().plusDays(1));
        createRequest.setIsActive(true);

        updateRequest = new UpdateEmployeeRequest();
        updateRequest.setId(1L);
        updateRequest.setPersonId(1L);
        updateRequest.setDepartmentId(1L);
        updateRequest.setPositionId(1L);
        updateRequest.setCompanyId(1L);
    }

    @Test
    void validateCreateDTO_ShouldNotThrow_WhenValidRequest() {
        // Given
        mockAllRepositoriesExist();

        // When/Then
        assertDoesNotThrow(() -> validator.validateCreateDTO(createRequest));
    }

    @Test
    void validateCreateDTO_ShouldThrow_WhenMissingRequiredFields() {
        // Empty request missing all required fields
        CreateEmployeeRequest emptyRequest = new CreateEmployeeRequest();

        assertThrows(IllegalArgumentException.class, () -> validator.validateCreateDTO(emptyRequest));
    }

    @Test
    void validateCreateDTO_ShouldThrow_WhenEndDateBeforeStartDate() {
        // Given
        mockAllRepositoriesExist();
        createRequest.setEmploymentEndDate(LocalDateTime.now()); // Before start date

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateCreateDTO(createRequest));
    }

    @Test
    void validateCreateDTO_ShouldThrow_WhenPersonNotFound() {
        // Given
        when(personRepository.existsById(1L)).thenReturn(false);
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.existsById(1L)).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateCreateDTO(createRequest));
    }

    @Test
    void validateUpdateDTO_ShouldNotThrow_WhenValidRequest() {
        // Given
        mockAllRepositoriesExist();

        // When/Then
        assertDoesNotThrow(() -> validator.validateUpdateDTO(updateRequest));
    }

    @Test
    void validateUpdateDTO_ShouldThrow_WhenInvalidManagerId() {
        // Given
        mockAllRepositoriesExist();
        updateRequest.setManagerId(999L);
        when(employeeRepository.existsById(999L)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateUpdateDTO(updateRequest));
    }

    @Test
    void validateUpdateDTO_ShouldThrow_WhenEndDateBeforeStartDate() {
        // Given
        mockAllRepositoriesExist();
        updateRequest.setEmploymentStartDate(LocalDateTime.now().plusDays(2));
        updateRequest.setEmploymentEndDate(LocalDateTime.now().plusDays(1));

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateUpdateDTO(updateRequest));
    }

    @Test
    void validateUpdateDTO_ShouldThrow_WhenInvalidDepartmentId() {
        // Given
        when(personRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(1L)).thenReturn(false);
        when(positionRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.existsById(1L)).thenReturn(true);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> validator.validateUpdateDTO(updateRequest));
    }

    private void mockAllRepositoriesExist() {
        when(personRepository.existsById(1L)).thenReturn(true);
        when(departmentRepository.existsById(1L)).thenReturn(true);
        when(positionRepository.existsById(1L)).thenReturn(true);
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
    }
}