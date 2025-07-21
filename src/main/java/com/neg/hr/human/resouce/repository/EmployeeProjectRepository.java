package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {

    List<EmployeeProject> findByEmployeeId(Long employeeId);

    List<EmployeeProject> findByProjectId(Long projectId);

    boolean existsByEmployeeIdAndProjectId(Long employeeId, Long projectId);

    void deleteByEmployeeId(Long employeeId);

    void deleteByProjectId(Long projectId);
}
