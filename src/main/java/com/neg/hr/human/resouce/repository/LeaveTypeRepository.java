package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<EmployeeProject,Long> {
}
