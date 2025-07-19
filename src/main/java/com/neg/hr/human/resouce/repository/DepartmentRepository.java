package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department,Long> {
}
