package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.EmployeeDTO;
import com.neg.hr.human.resource.entity.Employee;

public class EmployeeMapper {
    public static EmployeeDTO toDTO(Employee employee) {
        if (employee == null) return null;

        return EmployeeDTO.builder()
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
}
