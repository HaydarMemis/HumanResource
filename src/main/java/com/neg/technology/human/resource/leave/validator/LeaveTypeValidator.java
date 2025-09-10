package com.neg.technology.human.resource.leave.validator;

import com.neg.technology.human.resource.leave.model.request.CreateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.request.UpdateLeaveTypeRequest;
import com.neg.technology.human.resource.leave.model.entity.LeaveType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * LeaveTypeValidator sadece izin tipiyle ilgili temel validasyonları yapar.
 * Tüm iş kuralları (yaş, cinsiyet, kıdem, yıllık izin birikimi, borç izin, vs.)
 * LeaveBalanceServiceImpl ve LeaveRequestServiceImpl'de merkezi olarak yönetilmelidir.
 * Burada sadece alanların boş olup olmadığı, negatif değer kontrolü gibi temel kontroller yapılır.
 */
@Service
public class LeaveTypeValidator {

    private void validatorCommon(String name, Boolean isAnnual, LeaveType.Gender genderRequired, Boolean isUnpaid, Integer borrowableLimit) {
        if (!StringUtils.hasText(name)) {
            throw new IllegalArgumentException("İzin tipi adı boş olamaz");
        }

        if (isAnnual == null) {
            throw new IllegalArgumentException("isAnnual alanı boş olamaz");
        }

        if (genderRequired == null) {
            throw new IllegalArgumentException("genderRequired alanı boş olamaz");
        }

        if (isUnpaid == null) {
            throw new IllegalArgumentException("isUnpaid alanı boş olamaz");
        }

        if (borrowableLimit != null && borrowableLimit < 0) {
            throw new IllegalArgumentException("borrowableLimit negatif olamaz");
        }
    }

    public void validateCreate(CreateLeaveTypeRequest dto) {
        validatorCommon(
                dto.getName(),
                dto.getIsAnnual(),
                dto.getGenderRequired(),
                dto.getIsUnpaid(),
                dto.getBorrowableLimit());
    }

    public void validateUpdate(UpdateLeaveTypeRequest dto) {
        validatorCommon(
                dto.getName(),
                dto.getIsAnnual(),
                dto.getGenderRequired(),
                dto.getIsUnpaid(),
                dto.getBorrowableLimit()
        );
    }
}

/*
Açıklama:
- Yaş, cinsiyet, kıdem, yıllık izin birikimi, borç izin gibi iş kuralları LeaveBalanceServiceImpl ve LeaveRequestServiceImpl'de merkezi olarak yönetilecek.
- Burada sadece temel alan validasyonları var.
- İzin birikimi, yıl geçişi, eski yıllardan kalan izinlerin yeni yıla devri, borç izin gibi kurallar LeaveBalanceServiceImpl'de toplanacak.
- Aynı tarihte izin isteği kontrolü LeaveRequestServiceImpl'de yapılacak.
*/