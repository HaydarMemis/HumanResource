package com.neg.technology.human.resource.exception;

public class LeaveBalanceExceededException extends RuntimeException {

    // Özel constructor — sadece sınıf içinden erişilir
    private LeaveBalanceExceededException(String message) {
        super(message);
    }

    public static LeaveBalanceExceededException insufficientBalance(double available, double requested) {
        return new LeaveBalanceExceededException(
            "Yetersiz izin bakiyesi. Kalan: " + available + " gün, İstenen: " + requested + " gün."
        );
    }

    public static LeaveBalanceExceededException negativeAllowed(double allowedNegative) {
        return new LeaveBalanceExceededException(
            "Yetersiz izin bakiyesi. Maksimum negatif izin hakkı: " + allowedNegative + " gün."
        );
    }

    public static LeaveBalanceExceededException annualLeaveExceeded() {
        return new LeaveBalanceExceededException(
            "Yıllık izin hakkınız dolmuştur. Lütfen yeni dönem başlangıcını bekleyiniz."
        );
    }

    public static LeaveBalanceExceededException unpaidLeaveRequired() {
        return new LeaveBalanceExceededException(
            "Yetersiz izin bakiyesi. Ücretsiz izin talebi oluşturmayı deneyebilirsiniz."
        );
    }

    public static LeaveBalanceExceededException custom(String message) {
        return new LeaveBalanceExceededException(message);
    }
}
