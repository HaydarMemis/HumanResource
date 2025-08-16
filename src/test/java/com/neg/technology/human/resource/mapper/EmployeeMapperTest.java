package com.neg.technology.human.resource.mapper;

import com.neg.technology.human.resource.company.model.entity.Company;
import com.neg.technology.human.resource.department.model.entity.Department;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.employee.model.mapper.EmployeeMapper;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.person.model.entity.Person;
import com.neg.technology.human.resource.company.model.entity.Position;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    @Test
    void toDTOSingle_ShouldReturnNull_WhenEmployeeIsNull() {
        // Given
        Employee employee = getNullEmployee();

        // When
        EmployeeResponse result = EmployeeMapper.toDTO(employee);

        // Then
        assertNull(result, "Mapper should return null for null input");
    }

    @Test
    void toDTO_ShouldMapAllFieldsCorrectly() {
        // Given
        Employee employee = createSampleEmployee();

        // When
        EmployeeResponse result = EmployeeMapper.toDTO(employee);

        // Then
        assertEmployeeFieldsMappedCorrectly(result);
    }

    @Test
    void toDTOList_ShouldReturnEmptyList_WhenInputIsNull() {
        // Given
        List<Employee> employees = getNullEmployeeList();

        // When
        List<EmployeeResponse> result = EmployeeMapper.toDTO(employees);

        // Then
        assertNotNull(result, "Mapper should return empty list rather than null");
        assertTrue(result.isEmpty(), "Mapper should return empty list for null input");
    }

    @Test
    void toDTOList_ShouldReturnEmptyList_WhenInputIsEmpty() {
        // When
        List<EmployeeResponse> result = EmployeeMapper.toDTO(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void toDTOList_ShouldMapAllEmployees_WhenListProvided() {
        // Given
        Employee employee1 = createSampleEmployee();
        Employee employee2 = createSampleEmployeeWithDifferentName();

        List<Employee> employees = List.of(employee1, employee2);

        // When
        List<EmployeeResponse> result = EmployeeMapper.toDTO(employees);

        // Then
        assertEquals(2, result.size());
        assertEmployeeFieldsMappedCorrectly(result.get(0));
        assertEquals("Jane", result.get(1).getFirstName());
    }

    @Test
    void toEntity_ShouldMapAllFieldsFromCreateRequest() {
        // Given
        CreateEmployeeRequest request = createSampleCreateRequest();
        Person person = new Person();
        Department department = new Department();
        Position position = new Position();
        Company company = new Company();
        Employee manager = new Employee();

        // When
        Employee result = EmployeeMapper.toEntity(request, person, department, position, company, manager);

        // Then
        assertNotNull(result);
        assertEquals(person, result.getPerson());
        assertEquals(department, result.getDepartment());
        assertEquals(position, result.getPosition());
        assertEquals(company, result.getCompany());
        assertEquals(manager, result.getManager());
        assertEquals(request.getRegistrationNumber(), result.getRegistrationNumber());
        assertEquals(request.getHireDate(), result.getHireDate());
        assertEquals(request.getEmploymentStartDate(), result.getEmploymentStartDate());
        assertEquals(request.getIsActive(), result.getIsActive());
    }

    @Test
    void updateEntity_ShouldUpdateOnlyNonNullFields() {
        // Given
        Employee existing = createSampleEmployee();
        UpdateEmployeeRequest request = new UpdateEmployeeRequest();
        request.setRegistrationNumber("NEW");
        request.setIsActive(true);

        // When
        EmployeeMapper.updateEntity(existing, request, null, null, null, null, null);

        // Then
        assertEquals("NEW", existing.getRegistrationNumber());
        assertTrue(existing.getIsActive());
        // Verify unchanged fields
        assertNotNull(existing.getPerson());
        assertNotNull(existing.getDepartment());
        assertNotNull(existing.getPosition());
        assertNotNull(existing.getCompany());
        assertNotNull(existing.getManager());
    }

    // Helper methods
    private Employee getNullEmployee() {
        return null;
    }

    private List<Employee> getNullEmployeeList() {
        return null;
    }

    private Employee createSampleEmployee() {
        Person person = Person.builder().firstName("John").lastName("Doe").phone("1234567890").build();
        Department department = Department.builder().name("IT").build();
        Position position = Position.builder().title("Developer").build();
        Company company = Company.builder().name("Tech Corp").build();
        Employee manager = Employee.builder().person(Person.builder().firstName("Manager").lastName("Boss").build()).build();

        return Employee.builder()
                .id(1L)
                .person(person)
                .department(department)
                .position(position)
                .company(company)
                .manager(manager)
                .build();
    }

    private Employee createSampleEmployeeWithDifferentName() {
        Person person = Person.builder().firstName("Jane").lastName("Doe").phone("1234567890").build();
        Department department = Department.builder().name("IT").build();
        Position position = Position.builder().title("Developer").build();
        Company company = Company.builder().name("Tech Corp").build();
        Employee manager = Employee.builder().person(Person.builder().firstName("Manager").lastName("Boss").build()).build();

        return Employee.builder()
                .id(2L)
                .person(person)
                .department(department)
                .position(position)
                .company(company)
                .manager(manager)
                .build();
    }

    private void assertEmployeeFieldsMappedCorrectly(EmployeeResponse result) {
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("1234567890", result.getPhone());
        assertEquals("IT", result.getDepartmentName());
        assertEquals("Developer", result.getPositionTitle());
        assertEquals("Manager", result.getManagerFirstName());
        assertEquals("Boss", result.getManagerLastName());
        assertEquals("Tech Corp", result.getCompanyName());
    }

    private CreateEmployeeRequest createSampleCreateRequest() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setRegistrationNumber("EMP001");
        request.setHireDate(LocalDateTime.now());
        request.setEmploymentStartDate(LocalDateTime.now().plusDays(1));
        request.setIsActive(true);
        return request;
    }
}