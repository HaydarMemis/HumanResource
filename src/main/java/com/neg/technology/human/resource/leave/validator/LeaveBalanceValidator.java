package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveBalanceRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveBalanceRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class LeaveBalanceValidator {
    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    public LeaveBalanceValidator(EmployeeRepository employeeRepository, LeaveTypeRepository leaveTypeRepository) {
        this.employeeRepository = employeeRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    public Employee validateAndGetEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz çalışan ID: " + employeeId));
    }

    public LeaveType validateAndGetLeaveType(Long leaveTypeId) {
        return leaveTypeRepository.findById(leaveTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Geçersiz izin tipi ID: " + leaveTypeId));
    }

    /**
     * Çalışanın yaşına, cinsiyetine ve kıdemine göre izin hakkı hesaplanmalı.
     * Bu metot, CreateLeaveBalanceRequest ile gelen bilgileri kontrol eder ve
     * ilgili leaveBalanceServiceImpl'de kullanılmak üzere validasyon sağlar.
     */
    public void validateCreateDTO(CreateLeaveBalanceRequest dto) {
        Employee employee = validateAndGetEmployee(dto.getEmployeeId());
        LeaveType leaveType = validateAndGetLeaveType(dto.getLeaveTypeId());

        // Employee entity'sinde getBirthDate(), getStartDate(), getGender() metotları yoksa doğrudan alanlara erişelim
        LocalDate birthDate = null;
        LocalDate startDate = null;
        String gender = null;
        try {
            // getBirthDate() metodu yoksa doğrudan alanı kullan
            birthDate = (LocalDate) Employee.class.getDeclaredField("birthDate").get(employee);
        } catch (Exception e) {
            try {
                birthDate = (LocalDate) employee.getClass().getField("birthDate").get(employee);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Çalışanın doğum tarihi bilgisi eksik veya erişilemiyor.");
            }
        }
        try {
            startDate = (LocalDate) Employee.class.getDeclaredField("startDate").get(employee);
        } catch (Exception e) {
            try {
                startDate = (LocalDate) employee.getClass().getField("startDate").get(employee);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Çalışanın işe başlama tarihi bilgisi eksik veya erişilemiyor.");
            }
        }
        try {
            gender = (String) Employee.class.getDeclaredField("gender").get(employee);
        } catch (Exception e) {
            try {
                gender = (String) employee.getClass().getField("gender").get(employee);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Çalışanın cinsiyet bilgisi eksik veya erişilemiyor.");
            }
        }

        int yas = birthDate != null ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
        int kidem = startDate != null ? Period.between(startDate, LocalDate.now()).getYears() : 0;

        // Burada iş kurallarına göre izin hakkı belirlenebilir.
        // Örneğin:
        // - 50 yaş üstü fazladan izin hakkı
        // - Kadın çalışanlara doğum izni
        // - Kıdeme göre artan izin hakkı
        // Bu kurallar LeaveBalanceServiceImpl'de detaylandırılmalı.

        // Örnek validasyon: (detaylar serviste olmalı)
        if (yas < 18) {
            throw new IllegalArgumentException("18 yaşından küçük çalışanlara izin eklenemez.");
        }
        // Diğer kurallar LeaveBalanceServiceImpl'de uygulanacak.
    }

    /**
     * Güncelleme için de aynı şekilde yaş, cinsiyet ve kıdem kontrolü yapılabilir.
     */
    public void validateUpdateDTO(UpdateLeaveBalanceRequest dto) {
        if (dto.getEmployeeId() != null) {
            Employee employee = validateAndGetEmployee(dto.getEmployeeId());
            LocalDate birthDate = null;
            try {
                birthDate = (LocalDate) Employee.class.getDeclaredField("birthDate").get(employee);
            } catch (Exception e) {
                try {
                    birthDate = (LocalDate) employee.getClass().getField("birthDate").get(employee);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("Çalışanın doğum tarihi bilgisi eksik veya erişilemiyor.");
                }
            }
            int yas = birthDate != null ? Period.between(birthDate, LocalDate.now()).getYears() : 0;
            if (yas < 18) {
                throw new IllegalArgumentException("18 yaşından küçük çalışanlara izin eklenemez.");
            }
        }
        if (dto.getLeaveTypeId() != null) {
            validateAndGetLeaveType(dto.getLeaveTypeId());
        }
    }
}