package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Department;
import com.neg.hr.human.resource.repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService implements DepartmentInterface {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public Optional<Department> findByName(String name) {
        return departmentRepository.findByName(name);
    }

    @Override
    public List<Department> findByLocation(String location) {
        return departmentRepository.findByLocation(location);
    }

    @Override
    public List<Department> findByLocationContainingIgnoreCase(String keyword) {
        return departmentRepository.findByLocationContainingIgnoreCase(keyword);
    }

    @Override
    public boolean existsByName(String name) {
        return departmentRepository.existsByName(name);
    }

    @Override
    public Department save(Department department) {
        return departmentRepository.save(department);
    }

    @Override
    public Optional<Department> findById(Long id) {
        return departmentRepository.findById(id);
    }

    @Override
    public List<Department> findAll() {
        return departmentRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        departmentRepository.deleteById(id);
    }

    @Override
    public Department update(Long id, Department department) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Department not found with id " + id));
        existing.setName(department.getName());
        existing.setLocation(department.getLocation());
        return departmentRepository.save(existing);
    }
}
