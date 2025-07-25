package com.neg.hr.human.resource.repository;

import com.neg.hr.human.resource.entity.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, Long> {

    // Çalışanın projeye atanmış kaydı var mı?
    boolean existsByEmployee_Id(Long employeeId);

    // Projeye atanmış çalışan kaydı var mı?
    boolean existsByProject_Id(Long projectId);

    // Belirli bir çalışan ve proje kaydı var mı?
    boolean existsByEmployee_IdAndProject_Id(Long employeeId, Long projectId);

    // Çalışana ait kayıtları sil
    void deleteByEmployee_Id(Long employeeId);

    // Projeye ait kayıtları sil
    void deleteByProject_Id(Long projectId);

    // Çalışana ait tüm kayıtları getir
    List<EmployeeProject> findByEmployeeId(Long employeeId);

    // Projeye ait tüm kayıtları getir
    List<EmployeeProject> findByProjectId(Long projectId);
}
