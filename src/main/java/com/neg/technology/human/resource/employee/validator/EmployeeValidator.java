package com.neg.technology.human.resource.employee.validator;

import com.neg.technology.human.resource.company.repository.CompanyRepository;
import com.neg.technology.human.resource.department.repository.DepartmentRepository;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.person.repository.PersonRepository;
import com.neg.technology.human.resource.company.repository.PositionRepository;
import com.neg.technology.human.resource.employee.model.request.CreateEmployeeRequest;
import com.neg.technology.human.resource.employee.model.request.UpdateEmployeeRequest;
import org.springframework.stereotype.Service;

@Service
public class EmployeeValidator {
    private final PersonRepository personRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;

    public EmployeeValidator(PersonRepository personRepository,
                             DepartmentRepository departmentRepository,
                             PositionRepository positionRepository,
                             CompanyRepository companyRepository,
                             EmployeeRepository employeeRepository) {
        this.personRepository = personRepository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.companyRepository = companyRepository;
    }

    public void validateCreateDTO(CreateEmployeeRequest dto) {
        validateCommon(
                dto.getPersonId(),
                dto.getDepartmentId(),
                dto.getPositionId(),
                dto.getCompanyId(),
                dto.getManagerId());

        if(dto.getHireDate() == null)
            throw new IllegalArgumentException("Hire date is required");

        if(dto.getEmploymentStartDate() == null)
            throw new IllegalArgumentException("Employment start date is required");

        if(dto.getIsActive() == null)
            throw new IllegalArgumentException("Active status is required");

        if(dto.getEmploymentEndDate() != null &&
        dto.getEmploymentEndDate().isBefore(dto.getEmploymentStartDate()))
            throw new IllegalArgumentException("Employment end date cannot be before start date");
    }

    public void validateUpdateDTO(UpdateEmployeeRequest dto) {
        validateCommon(dto.getPersonId(), dto.getDepartmentId(), dto.getPositionId(), dto.getCompanyId(), dto.getManagerId());

        if(dto.getEmploymentStartDate() != null && dto.getEmploymentEndDate() != null &&
        dto.getEmploymentEndDate().isBefore(dto.getEmploymentStartDate()))
            throw new IllegalArgumentException("Employment end date cannot be before start date");
    }

    private void validateCommon( Long personId, Long departmentId, Long positionId,
                                 Long companyId, Long managerId) {
        if (personId != null && personRepository.findById(personId).isEmpty()) {
            throw new IllegalArgumentException("Invalid person id");
        }

        if (departmentId != null && departmentRepository.findById(departmentId).isEmpty()) {
            throw new IllegalArgumentException("Invalid department id");
        }

        if (positionId != null && positionRepository.findById(positionId).isEmpty()) {
            throw new IllegalArgumentException("Invalid position id");
        }

        if (companyId != null && companyRepository.findById(companyId).isEmpty()) {
            throw new IllegalArgumentException("Invalid company id");
        }

        if (managerId != null && employeeRepository.findById(managerId).isEmpty()) {
            throw new IllegalArgumentException("Invalid manager id");
        }
    }
}