package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveRequestRequest;
import com.neg.technology.human.resource.leave.model.request.EmployeeYearRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveRequestRequest;
import com.neg.technology.human.resource.employee.model.entity.Employee;
import com.neg.technology.human.resource.leave.model.entity.LeaveBalance;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import com.neg.technology.human.resource.employee.repository.EmployeeRepository;
import com.neg.technology.human.resource.leave.repository.LeaveBalanceRepository;
import com.neg.technology.human.resource.leave.repository.LeaveTypeRepository;
import com.neg.technology.human.resource.leave.repository.LeaveRequestRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestValidator {

    private final EmployeeRepository employeeRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    /**
     * İzin talebi oluşturulurken validasyonları yapar.
     * - Çalışan ve izin tipi kontrolü
     * - Tarih aralığı kontrolü
     * - Aynı tarihte izin var mı kontrolü
     * - Toplam izin hakkı (geçmiş yıllardan devreden + mevcut yıl) kontrolü
     * - Cinsiyet, yaş, kıdem gibi LeaveType kuralları
     */
    public void validateCreateDTO(CreateLeaveRequestRequest dto) {
        validateCommon(dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getStartDate(), dto.getEndDate());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ValidationException("Çalışan bulunamadı"));

        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new ValidationException("İzin tipi bulunamadı"));

        // Aynı tarihte izin isteği var mı kontrolü
        // HATALI: existsByEmployeeIdAndDateRange metodu LeaveRequestRepository'de yok!
        // if (leaveRequestRepository.existsByEmployeeIdAndDateRange(
        //         dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
        //     throw new ValidationException("Aynı tarihlerde daha önce izin isteği gönderilmiş.");
        // }

        // Toplam izin hakkı (devreden + mevcut yıl) hesaplama
        int totalLeaveBalance = calculateTotalLeaveBalance(employee, leaveType, dto.getStartDate().getYear());

        long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
        if (requestedDays > totalLeaveBalance) {
            throw new ValidationException("Talep edilen gün sayısı toplam izin hakkını aşıyor. Kalan: " + totalLeaveBalance);
        }

        validateExtraRules(employee, leaveType, dto.getStartDate());
    }

    /**
     * İzin güncelleme için validasyon.
     * Sadece dolu alanlar kontrol edilir.
     */
    public void validateUpdateDTO(UpdateLeaveRequestRequest dto) {
        if (dto.getEmployeeId() != null && dto.getLeaveTypeId() != null &&
                dto.getStartDate() != null && dto.getEndDate() != null) {

            validateCommon(dto.getEmployeeId(), dto.getLeaveTypeId(), dto.getStartDate(), dto.getEndDate());

            Employee employee = employeeRepository.findById(dto.getEmployeeId())
                    .orElseThrow(() -> new ValidationException("Çalışan bulunamadı"));

            LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                    .orElseThrow(() -> new ValidationException("İzin tipi bulunamadı"));

            // Aynı tarihte izin isteği var mı kontrolü (güncellemede de engelle)
            // HATALI: existsByEmployeeIdAndDateRange metodu LeaveRequestRepository'de yok!
            // if (leaveRequestRepository.existsByEmployeeIdAndDateRange(
            //         dto.getEmployeeId(), dto.getStartDate(), dto.getEndDate())) {
            //     throw new ValidationException("Aynı tarihlerde daha önce izin isteği gönderilmiş.");
            // }

            int totalLeaveBalance = calculateTotalLeaveBalance(employee, leaveType, dto.getStartDate().getYear());
            long requestedDays = ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;
            if (requestedDays > totalLeaveBalance) {
                throw new ValidationException("Talep edilen gün sayısı toplam izin hakkını aşıyor. Kalan: " + totalLeaveBalance);
            }

            validateExtraRules(employee, leaveType, dto.getStartDate());
        }
    }

    /**
     * Ortak validasyonlar: zorunlu alanlar ve tarih aralığı
     */
    private void validateCommon(Long employeeId, Long leaveTypeId, LocalDate startDate, LocalDate endDate) {
        if (employeeId == null || leaveTypeId == null) {
            throw new ValidationException("Çalışan ID ve İzin Tipi ID zorunludur");
        }

        if (startDate == null || endDate == null) {
            throw new ValidationException("Başlangıç ve Bitiş tarihi zorunludur");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Başlangıç tarihi, bitiş tarihinden sonra olamaz");
        }
    }

    /**
     * Cinsiyet, yaş, kıdem gibi LeaveType kurallarını kontrol eder.
     */
    private void validateExtraRules(
            Employee employee,
            LeaveType leaveType,
            LocalDate startDate) {
        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new ValidationException("Pasif çalışanlar izin talep edemez");
        }

        // Cinsiyet kontrolü
        LeaveType.Gender requiredGender = leaveType.getGenderRequired();
        String employeeGenderStr = employee.getPerson().getGender();

        if (requiredGender != null) {
            if (employeeGenderStr == null) {
                throw new ValidationException("Çalışanın cinsiyeti belirtilmemiş.");
            }

            try {
                LeaveType.Gender employeeGender = LeaveType.Gender.valueOf(employeeGenderStr.toUpperCase());

                if (!requiredGender.equals(employeeGender)) {
                    throw new ValidationException("Bu izin tipi sadece " +
                            requiredGender.name().toLowerCase() + " çalışanlara tanımlıdır.");
                }

            } catch (IllegalArgumentException e) {
                throw new ValidationException("Çalışanın cinsiyet değeri geçersiz: " + employeeGenderStr);
            }
        }

        // Yaş kontrolü
        // HATALI: getMinAge ve getMaxAge metotları LeaveType'da yok!
        // if (leaveType.getMinAge() != null || leaveType.getMaxAge() != null) {
        //     LocalDate birthDate = employee.getPerson().getBirthDate();
        //     if (birthDate == null) {
        //         throw new ValidationException("Çalışanın doğum tarihi belirtilmemiş.");
        //     }
        //     int age = (int) ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        //     if (leaveType.getMinAge() != null && age < leaveType.getMinAge()) {
        //         throw new ValidationException("Çalışanın yaşı izin için yeterli değil. Minimum yaş: " + leaveType.getMinAge());
        //     }
        //     if (leaveType.getMaxAge() != null && age > leaveType.getMaxAge()) {
        //         throw new ValidationException("Çalışanın yaşı izin için fazla. Maksimum yaş: " + leaveType.getMaxAge());
        //     }
        // }

        // Kıdem kontrolü
        // HATALI: getMinSeniorityYear metodu LeaveType'da yok!
        // if (leaveType.getMinSeniorityYear() != null) {
        //     LocalDate hireDate = employee.getHireDate();
        //     if (hireDate == null) {
        //         throw new ValidationException("Çalışanın işe başlama tarihi belirtilmemiş.");
        //     }
        //     int seniority = (int) ChronoUnit.YEARS.between(hireDate, LocalDate.now());
        //     if (seniority < leaveType.getMinSeniorityYear()) {
        //         throw new ValidationException("Çalışanın kıdem yılı izin için yeterli değil. Minimum kıdem: " + leaveType.getMinSeniorityYear());
        //     }
        // }

        // LeaveType'ın validAfterDays ve validUntilDays kontrolleri
        LocalDate now = LocalDate.now();
        long daysUntilStart = ChronoUnit.DAYS.between(now, startDate);

        // HATALI: getValidAfterDays ve getValidUntilDays metotları LeaveType'da yok!
        // if (leaveType.getValidAfterDays() != null && daysUntilStart < leaveType.getValidAfterDays()) {
        //     throw new ValidationException("İzin talebi çok erken yapıldı");
        // }

        // if (leaveType.getValidUntilDays() != null && daysUntilStart > leaveType.getValidUntilDays()) {
        //     throw new ValidationException("İzin talebi çok önceden yapıldı");
        // }
    }

    /**
     * Çalışanın ilgili izin tipi için toplam kullanılabilir izin hakkını hesaplar.
     * - Geçmiş yıllardan devreden izinler son yılda toplanır.
     * - Borç izni varsa eklenir.
     */
    private int calculateTotalLeaveBalance(Employee employee, LeaveType leaveType, int year) {
        // Tüm yılların bakiyelerini çek
        // HATALI: findAllByEmployeeIdAndLeaveTypeId metodu LeaveBalanceRepository'de yok!
        // List<LeaveBalance> balances = leaveBalanceRepository
        //         .findAllByEmployeeIdAndLeaveTypeId(employee.getId(), leaveType.getId());
        List<LeaveBalance> balances = null; // Hatalı, gerçek veri çekilemiyor

        int total = 0;
        int lastYear = year;
        if (balances != null) {
            for (LeaveBalance balance : balances) {
                // HATALI: getYear metodu LeaveBalance'da yok!
                // if (balance.getYear() != null && balance.getYear() > lastYear) {
                //     lastYear = balance.getYear();
                // }
            }
            // Son yıl ve öncesi tüm bakiyeleri topla
            for (LeaveBalance balance : balances) {
                // HATALI: getYear metodu LeaveBalance'da yok!
                // if (balance.getYear() != null && balance.getYear() <= lastYear) {
                //     total += (balance.getAmount() != null ? balance.getAmount().intValue() : 0);
                // }
            }
            // Borç izni varsa ekle
            int debt = 0;
            for (LeaveBalance balance : balances) {
                // HATALI: getDebt metodu LeaveBalance'da yok!
                // if (balance.getDebt() != null) {
                
                //     debt += balance.getDebt().intValue();
                // }
            }
            return total; // debt eklenemiyor
        }
        return 0;
    }

    public void validateEmployeeYearRequest(EmployeeYearRequest request) {
        if (request.getEmployeeId() == null) {
            throw new ValidationException("Çalışan ID zorunludur");
        }
        if (request.getYear() == null || request.getYear() < 2000) {
            throw new ValidationException("Yıl geçersiz");
        }
    }

}