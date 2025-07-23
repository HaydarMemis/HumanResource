package com.neg.hr.human.resource.repository;

import com.neg.hr.human.resource.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByPersonId(Long personId);

    List<Employee> findByManagerId(Long managerId);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByPositionId(Long positionId);

    List<Employee> findByCompanyId(Long companyId);

    List<Employee> findByIsActiveTrue();

    List<Employee> findByIsActiveFalse();

    List<Employee> findByHireDateBefore(java.time.LocalDateTime date);

    List<Employee> findByEmploymentEndDateBefore(java.time.LocalDateTime date);

    List<Employee> findByPersonIdIn(List<Long> personIds);

    boolean existsByManagerId(Long managerId);
}
