package com.neg.technology.human.resource.exception;

public class ResourceNotFoundException extends RuntimeException {

    // Genel mesaj constructor'ı
    private ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException employeeNotFound() {
        return new ResourceNotFoundException("Çalışan bulunamadı. Lütfen ID veya bilgilerinizin doğruluğunu kontrol edin.");
    }

    public static ResourceNotFoundException leaveRequestNotFound() {
        return new ResourceNotFoundException("İzin talebi bulunamadı. Lütfen talep ID’sini veya tarih bilgilerini kontrol edin.");
    }

    public static ResourceNotFoundException leaveTypeNotFound() {
        return new ResourceNotFoundException("İzin türü bulunamadı. Lütfen izin türü adını kontrol edin.");
    }

    public static ResourceNotFoundException projectNotFound() {
        return new ResourceNotFoundException("Proje bulunamadı. Lütfen proje ID veya adını kontrol edin.");
    }

    public static ResourceNotFoundException employeeProjectNotFound() {
        return new ResourceNotFoundException("Çalışan-Proje kaydı bulunamadı. Lütfen ilgili çalışan veya proje bilgilerinin doğruluğunu kontrol edin.");
    }

    public static ResourceNotFoundException leaveBalanceNotFound() {
        return new ResourceNotFoundException("İzin bakiyesi bulunamadı. Lütfen çalışan bilgilerini ve izin türünü kontrol edin.");
    }

    public static ResourceNotFoundException approverEmployeeNotFound() {
        return new ResourceNotFoundException("Onaylayıcı çalışan bulunamadı. Lütfen onaylayıcı bilgilerini kontrol edin.");
    }

    public static ResourceNotFoundException leaveTypeNameNotFound() {
        return new ResourceNotFoundException("Belirtilen isimde izin türü bulunamadı. Lütfen izin türü adını kontrol edin.");
    }

    public static ResourceNotFoundException departmentNotFound() {
        return new ResourceNotFoundException("Departman bulunamadı. Lütfen departman ID veya adını kontrol edin.");
    }

    public static ResourceNotFoundException departmentNameNotFound() {
        return new ResourceNotFoundException("Belirtilen isimde departman bulunamadı. Lütfen departman adını kontrol edin.");
    }

    public static ResourceNotFoundException projectNameNotFound() {
        return new ResourceNotFoundException("Belirtilen isimde proje bulunamadı. Lütfen proje adını kontrol edin.");
    }

    public static ResourceNotFoundException positionNotFound() {
        return new ResourceNotFoundException("Pozisyon bulunamadı. Lütfen pozisyon ID veya adını kontrol edin.");
    }

    public static ResourceNotFoundException positionTitleNotFound() {
        return new ResourceNotFoundException("Belirtilen unvan ile pozisyon bulunamadı. Lütfen pozisyon unvanını kontrol edin.");
    }

    public static ResourceNotFoundException companyNotFound() {
        return new ResourceNotFoundException("Şirket bulunamadı. Lütfen şirket ID veya adını kontrol edin.");
    }

    public static ResourceNotFoundException companyNameNotFound() {
        return new ResourceNotFoundException("Belirtilen isimde şirket bulunamadı. Lütfen şirket adını kontrol edin.");
    }

    public static ResourceNotFoundException personNotFound() {
        return new ResourceNotFoundException("Kişi bulunamadı. Lütfen ID, T.C. kimlik veya e-posta bilgilerini kontrol edin.");
    }
}
