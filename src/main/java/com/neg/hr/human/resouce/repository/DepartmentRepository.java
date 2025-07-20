package com.neg.hr.human.resouce.repository;

import com.neg.hr.human.resouce.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByName(String name);

    List<Department> findByLocation(String location);

    List<Department> findByLocationContainingIgnoreCase(String keyword);

    //  İsim benzersiz mi? (yeni oluştururken kontrol amaçlı)
    boolean existsByName(String name);
}
