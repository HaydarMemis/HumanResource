package com.neg.technology.human.resource.employee_project.repository;

import com.neg.technology.human.resource.employee_project.model.entity.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {

    boolean existsByEmployee_Id(Long employeeId);

    boolean existsByProject_Id(Long projectId);

    boolean existsByEmployee_IdAndProject_Id(Long employeeId, Long projectId);

    void deleteByEmployee_Id(Long employeeId);

    void deleteByProject_Id(Long projectId);

    List<EmployeeProject> findByEmployeeId(Long employeeId);

    List<EmployeeProject> findByProjectId(Long projectId);
}
