package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Employee;
import com.neg.hr.human.resouce.entity.LeaveRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class LeavePolicyService {

    /**
     * Çalışanın işteki kıdemini yıl olarak hesaplar.
     */
    public int calculateSeniorityYears(Employee employee) {
        if (employee.getEmploymentStartDate() == null) return 0;
        LocalDate startDate = employee.getEmploymentStartDate().toLocalDate();
        LocalDate today = LocalDate.now();
        return Period.between(startDate, today).getYears();
    }

    /**
     * Çalışanın kıdemini ay olarak hesaplar.
     */
    public int calculateSeniorityMonths(Employee employee) {
        if (employee.getEmploymentStartDate() == null) return 0;
        LocalDate startDate = employee.getEmploymentStartDate().toLocalDate();
        LocalDate today = LocalDate.now();
        Period period = Period.between(startDate, today);
        return period.getYears() * 12 + period.getMonths();
    }

    /**
     * Çalışanın yaşını yıl olarak hesaplar.
     */
    public int calculateAge(Employee employee) {
        if (employee.getPerson() == null || employee.getPerson().getBirthDate() == null) return 0;
        LocalDate birthDate = employee.getPerson().getBirthDate();
        LocalDate today = LocalDate.now();
        return Period.between(birthDate, today).getYears();
    }

    /**
     * Yıllık izin hakkını hesaplar.
     */
    public int calculateAnnualLeaveDays(Employee employee) {
        int seniority = calculateSeniorityYears(employee);
        int age = calculateAge(employee);

        if (seniority < 1) return 0; // 1 yıldan az çalışan yıllık izin hakkı yok, borç alabilir
        if (seniority >= 1 && seniority < 5) return 14;
        if (seniority >= 5 && seniority < 15) return 20;
        if (seniority >= 15) return 26;

        return 0;
    }

    /**
     * Yaşa bağlı izin artışı (50 yaş üstü çalışanlara 1 yıl dolduğunda 20 gün ekstra izin)
     */
    public int calculateAgeBasedLeaveBonus(Employee employee) {
        int age = calculateAge(employee);
        int seniority = calculateSeniorityYears(employee);
        if (age >= 50 && seniority >= 1) {
            return 20;
        }
        return 0;
    }

    /**
     * Borç izin limiti kontrolü.
     * - İşe yeni başlayan personel, ilk 3 ay sonunda maksimum 5 gün borçlanabilir.
     * - Borç izin toplamı 10 günü geçemez.
     * - Borç izin sadece yöneticinin onayıyla alınabilir (onay kontrolü burada yok, ayrı yerde yapılmalı).
     */
    public boolean canBorrowLeave(Employee employee, int requestedDays, int currentBorrowedDays) {
        int seniorityMonths = calculateSeniorityMonths(employee);

        if (seniorityMonths < 3) {
            // Henüz borç izin kullanamaz
            return false;
        }

        int maxBorrowable = 10; // Toplam borç izin limiti
        if ((currentBorrowedDays + requestedDays) > maxBorrowable) {
            return false;
        }

        // Yöneticinin onayı kontrolü servisin başka bir yerinde yapılmalı.

        return true;
    }

    /**
     * Hamilelik izni hesaplama
     * - Kadın için toplam 16 hafta (112 gün): 8 hafta doğum öncesi, 8 hafta doğum sonrası.
     * - Çoğul gebelikte +2 hafta (14 gün) eklenir.
     * - Erkek çalışan için babalık izni 5 gündür (burada sadece kadın için hesaplanıyor).
     */
    public int calculateMaternityLeaveDays(Employee employee, boolean multiplePregnancy) {
        if (employee.getPerson() == null) return 0;

        String gender = employee.getPerson().getGender();
        if (gender == null) return 0;

        if (gender.equalsIgnoreCase("F")) {
            int baseDays = 112;
            if (multiplePregnancy) baseDays += 14;
            return baseDays;
        } else if (gender.equalsIgnoreCase("M")) {
            // Erkek çalışan için babalık izni 5 gün
            return 5;
        }

        return 0;
    }

    /**
     * Doğum günü izni (1 gün ücretli izin)
     * - Personelin doğum gününde 1 gün izin verilir.
     * - Yönetim değerlendirmesine göre (burada sadece tarih kontrolü yapılıyor).
     */
    public boolean isBirthdayLeaveEligible(Employee employee, LocalDate date) {
        if (employee.getPerson() == null || employee.getPerson().getBirthDate() == null) return false;
        LocalDate birthDate = employee.getPerson().getBirthDate();

        return birthDate.getMonth() == date.getMonth() &&
                birthDate.getDayOfMonth() == date.getDayOfMonth();
    }

    /**
     * Resmi ve dini bayram tatili kontrolü.
     * - Burada sadece tarih karşılaştırması yapılır.
     * - Bayram tatilleri çalışılırsa mesai ücreti eklenir (bu hesap servisin dışında).
     */
    public boolean isHoliday(LocalDate date) {
        // Örnek: Sabit bayram tarihleri burada tutulabilir veya dışarıdan alınabilir.
        // Bu metod gerçek sistemde bir veri kaynağına bağlanmalı.
        // Örnek hardcoded tarih (yalnızca örnek, güncellenmeli):
        LocalDate newYear = LocalDate.of(date.getYear(), 1, 1);
        LocalDate republicDay = LocalDate.of(date.getYear(), 10, 29);
        // Dini bayramlar değişken olduğu için ayrıca işlenmeli.

        return date.equals(newYear) || date.equals(republicDay);
    }

    /**
     * Ölüm izni gün sayısı
     * - Anne, baba, eş, çocuk, kardeş için 3 gün
     * - Diğer akrabalar için 1 gün (opsiyonel)
     */
    public int calculateBereavementLeaveDays(String relation) {
        switch (relation.toLowerCase()) {
            case "anne":
            case "baba":
            case "eş":
            case "cocuk":
            case "kardes":
                return 3;
            default:
                return 1; // Diğer akrabalar
        }
    }

    /**
     * Evlilik izni
     * - Resmi evlilik belgesi gerekir.
     * - 3 gün ücretli izin.
     * - Sadece ilk evlilik için geçerlidir (opsiyonel).
     */
    public int calculateMarriageLeaveDays(boolean isFirstMarriage, boolean hasMarriageCertificate) {
        if (!hasMarriageCertificate) return 0;
        if (!isFirstMarriage) return 0;
        return 3;
    }

    /**
     * Askerlik izni
     * - Ücretsiz izin olarak verilir.
     * - Burada sadece izin süresi belirtilebilir, detaylar dışarıda tutulabilir.
     */
    public int calculateMilitaryLeaveDays() {
        // Askerlik süresi sistemden veya dışarıdan belirlenmeli
        return 30; // Örnek 30 gün
    }

    /**
     * Kullanıcının kendi izin isteklerini görüntüleyebilmesi için filtreleme yapılabilir.
     * Burada iş mantığı olarak kullanıcı bazlı sorgu yapılmalı.
     * (Repository ve Controller seviyesinde uygulanmalı)
     */

    /**
     * Kullanıcının izin bakiyesini görüntüleme mantığı (kalan, kullanılan, borçlu izinler)
     * Burada hesaplama yapılabilir, örnek:
     */
    public int calculateRemainingLeave(int totalGranted, int used, int borrowed) {
        return totalGranted - used + borrowed;
    }

}
