package com.neg.technology.human.resource.EmployeeProject.entity;

import com.neg.technology.human.resource.Employee.model.entity.Employee;
import com.neg.technology.human.resource.entity.AuditableEntity;
import com.neg.technology.human.resource.entity.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employee_project")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeProject extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
