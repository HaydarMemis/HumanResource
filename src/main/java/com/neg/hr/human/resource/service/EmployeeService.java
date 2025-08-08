package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.hr.human.resource.dto.create.CreateEmployeeRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.hr.human.resource.dto.update.UpdateEmployeeRequestDTO;
import com.neg.hr.human.resource.entity.Employee;s

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    Employee createEmployee(CreateEmployeeRequestDTO dto);
    Employee updateEmployee(UpdateEmployeeRequestDTO dto);
    boolean existsById(Long id);

    Optional<Employee> findByPersonId(Long personId);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByPositionId(Long positionId);

    List<Employee> findByCompanyId(Long companyId);

    List<Employee> findByIsActiveTrue();

    List<Employee> findByIsActiveFalse();

    List<Employee> findByHireDateBefore(LocalDateTime date);

    List<Employee> findByEmploymentEndDateBefore(LocalDateTime date);

    List<Employee> findByPersonIdIn(List<Long> personIds);

    boolean existsByManagerId(Long managerId);

    Employee save(Employee employee);

    void deleteById(Long id);

    List<Employee> findAll();

    Optional<Employee> findById(Long id);

    Employee update(Long id, Employee employee);
}
