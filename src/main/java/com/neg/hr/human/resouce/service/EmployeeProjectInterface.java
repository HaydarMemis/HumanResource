package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.EmployeeProject;

import java.util.List;
public interface EmployeeProjectInterface {

    public EmployeeProject save(EmployeeProject employeeProject);

    public EmployeeProject findById(Long id);

    public List<EmployeeProject> findAll();

    public void delete(Long id);

    public EmployeeProject update(Long id, EmployeeProject  employeeProject);
}
