package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectInterface {
    Project save(Project project);

    Optional<Project> findById(Long id);

    Optional<Project> findByName(String name);

    List<Project> findAll();

    boolean existsByName(String name);

    void deleteById(Long id);

    Project update(Long id, Project project);
}
