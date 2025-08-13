package com.neg.technology.human.resource.Employee.model.mapper;

import com.neg.technology.human.resource.Company.model.entity.Company;
import com.neg.technology.human.resource.Department.model.entity.Department;
import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.Employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.request.UpdateEmployeeRequest;
import com.neg.technology.human.resource.Employee.model.response.EmployeeResponse;
import com.neg.technology.human.resource.Person.model.entity.Person;
import com.neg.technology.human.resource.Position.model.entity.Position;

import java.util.List;

public class EmployeeMapper {

    private EmployeeMapper() {}

    public static EmployeeResponse toDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getPerson() != null ? employee.getPerson().getFirstName() : null)
                .lastName(employee.getPerson() != null ? employee.getPerson().getLastName() : null)
                .phone(employee.getPerson() != null ? employee.getPerson().getPhone() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .positionTitle(employee.getPosition() != null ? employee.getPosition().getTitle() : null)
                .managerFirstName(employee.getManager() != null && employee.getManager().getPerson() != null ? employee.getManager().getPerson().getFirstName() : null)
                .managerLastName(employee.getManager() != null && employee.getManager().getPerson() != null ? employee.getManager().getPerson().getLastName() : null)
                .companyName(employee.getCompany() != null ? employee.getCompany().getName() : null)
                .build();
    }

    public static List<EmployeeResponse> toDTO(List<Employee> employees) {
        if (employees == null) return List.of();
        return employees.stream()
                .map(EmployeeMapper::toDTO)
                .toList();
    }

    // CreateEmployeeRequest -> Employee entity
    public static Employee toEntity(CreateEmployeeRequest dto,
                                    Person person,
                                    Department department,
                                    Position position,
                                    Company company,
                                    Employee manager) {

        return Employee.builder()
                .person(person)
                .department(department)
                .position(position)
                .company(company)
                .manager(manager)
                .registrationNumber(dto.getRegistrationNumber())
                .hireDate(dto.getHireDate())
                .employmentStartDate(dto.getEmploymentStartDate())
                .employmentEndDate(dto.getEmploymentEndDate())
                .isActive(dto.getIsActive())
                .build();
    }

    public static void updateEntity(Employee employee,
                                    UpdateEmployeeRequest dto,
                                    Person person,
                                    Department department,
                                    Position position,
                                    Company company,
                                    Employee manager) {

        if (person != null) employee.setPerson(person);
        if (department != null) employee.setDepartment(department);
        if (position != null) employee.setPosition(position);
        if (company != null) employee.setCompany(company);
        if (manager != null) employee.setManager(manager);

        if (dto.getRegistrationNumber() != null) employee.setRegistrationNumber(dto.getRegistrationNumber());
        if (dto.getHireDate() != null) employee.setHireDate(dto.getHireDate());
        if (dto.getEmploymentStartDate() != null) employee.setEmploymentStartDate(dto.getEmploymentStartDate());
        if (dto.getEmploymentEndDate() != null) employee.setEmploymentEndDate(dto.getEmploymentEndDate());
        if (dto.getIsActive() != null) employee.setIsActive(dto.getIsActive());
    }
}
