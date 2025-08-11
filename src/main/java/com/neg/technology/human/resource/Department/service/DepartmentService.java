package com.neg.technology.human.resource.Department.service;

import com.neg.technology.human.resource.Department.model.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {
    Optional<Department> findByName(String name);

    List<Department> findByLocation(String location);

    List<Department> findByLocationContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    Department save(Department department);

    Optional<Department> findById(Long id);

    List<Department> findAll();

    void deleteById(Long id);

    Department update(Long id, Department department);
    boolean existsById(Long id);
}
