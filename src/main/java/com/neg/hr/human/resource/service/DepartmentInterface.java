package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentInterface {
    Optional<Department> findByName(String name);

    List<Department> findByLocation(String location);

    List<Department> findByLocationContainingIgnoreCase(String keyword);

    boolean existsByName(String name);

    public Department save(Department department);

    public Optional<Department> findById(Long id);

    public List<Department> findAll();

    void deleteById(Long id);

    public Department update(Long id, Department department);
}
