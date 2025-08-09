package com.neg.hr.human.resource.service;

import com.neg.hr.human.resource.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    Project save(Project project);

    Optional<Project> findById(Long id);

    Optional<Project> findByName(String name);

    List<Project> findAll();

    boolean existsByName(String name);

    void deleteById(Long id);

    Project update(Long id, Project project);

    boolean existsById(Long id);
}
