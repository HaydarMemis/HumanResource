package com.neg.technology.human.resource.department.repository;

import com.neg.technology.human.resource.department.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    List<Department> findByLocation(String location);

    List<Department> findByLocationContainingIgnoreCase(String keyword);

    boolean existsByName(String name);
}
