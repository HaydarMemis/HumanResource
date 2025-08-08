package com.neg.hr.human.resource.service.impl;

import com.neg.hr.human.resource.entity.Department;
import com.neg.hr.human.resource.exception.ResourceNotFoundException;
import com.neg.hr.human.resource.repository.DepartmentRepository;
import com.neg.hr.human.resource.business.BusinessLogger;
import com.neg.hr.human.resource.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
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
        Department saved = departmentRepository.save(department);
        BusinessLogger.logCreated(Department.class, saved.getId(), saved.getName());
        return saved;
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
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", id);
        }
        departmentRepository.deleteById(id);
        BusinessLogger.logDeleted(Department.class, id);
    }

    @Override
    public Department update(Long id, Department department) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", id));

        existing.setName(department.getName());
        existing.setLocation(department.getLocation());

        Department updated = departmentRepository.save(existing);
        BusinessLogger.logUpdated(Department.class, updated.getId(), updated.getName());
        return updated;
    }

    @Override
    public boolean existsById(Long id) {
        return departmentRepository.existsById(id);
    }
}
