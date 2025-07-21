package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Employee;
import java.util.List;

public interface EmployeeService {
    Employee save(Employee employee);
    Employee findById(Long id);
    List<Employee> findAll();
    void delete(Long id);
    Employee update(Long id, Employee employee);
}
