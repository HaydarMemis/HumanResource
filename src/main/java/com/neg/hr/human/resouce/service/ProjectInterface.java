package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Project;

import java.util.List;

public interface ProjectInterface {

    public Project save(Project project);

    public Project findById(Long id);

    public List<Project> findAll();

    public void delete(Long id);

    public Project update(Long id, Project project);
}
