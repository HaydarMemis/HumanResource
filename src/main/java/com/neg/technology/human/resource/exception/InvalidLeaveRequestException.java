package com.neg.technology.human.resource.exception;

public class InvalidLeaveRequestException extends RuntimeException {

    private InvalidLeaveRequestException(String message) {
        super(message);
    }

    public static InvalidLeaveRequestException invalidDateRange() {
        return new InvalidLeaveRequestException("Geçersiz tarih aralığı. Başlangıç tarihi bitiş tarihinden sonra olamaz.");
    }

    public static InvalidLeaveRequestException overlappingRequest() {
        return new InvalidLeaveRequestException("Bu tarihlerde zaten bir izin talebiniz bulunmaktadır.");
    }

    public static InvalidLeaveRequestException invalidLeaveType() {
        return new InvalidLeaveRequestException("Geçersiz izin türü. Lütfen uygun bir izin tipi seçiniz.");
    }

    public static InvalidLeaveRequestException notEligibleForAnnualLeave() {
        return new InvalidLeaveRequestException("Henüz yıllık izin hakkınız bulunmamaktadır (1 yıldan az çalışma süresi).");
    }

    public static InvalidLeaveRequestException requestInPast() {
        return new InvalidLeaveRequestException("Geçmiş bir tarih için izin talebi oluşturulamaz.");
    }

    public static InvalidLeaveRequestException invalidRequest(String message) {
        return new InvalidLeaveRequestException(message);
    }
}
