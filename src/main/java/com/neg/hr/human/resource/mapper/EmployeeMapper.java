package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.hr.human.resource.dto.entity.EmployeeEntityDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.hr.human.resource.entity.*;

public class EmployeeMapper {
    public static EmployeeEntityDTO toDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeEntityDTO.builder()
                .id(employee.getId())
                .firstName(employee.getPerson().getFirstName())
                .lastName(employee.getPerson().getLastName())
                .phone(employee.getPerson().getPhone())
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .positionTitle(employee.getPosition() != null ? employee.getPosition().getTitle() : null)
                .managerFirstName(employee.getManager() != null ? employee.getManager().getPerson().getFirstName() : null)
                .managerLastName(employee.getManager() != null ? employee.getManager().getPerson().getLastName() : null)
                .companyName(employee.getCompany() != null ? employee.getCompany().getName() : null)
                .build();
    }

    public static Employee toEntity(CreateEmployeeRequestDTO dto,
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
                                    UpdateEmployeeRequestDTO dto,
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
