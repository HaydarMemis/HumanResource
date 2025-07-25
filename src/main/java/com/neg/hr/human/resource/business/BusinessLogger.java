package com.neg.hr.human.resource.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessLogger {

    private static final Logger businessLogger = LoggerFactory.getLogger("com.neg.hr.human.resource.business");

    public static void logEmployeeCreated(Long employeeId, String name) {
        businessLogger.info("Çalışan oluşturuldu: id={}, isim={}", employeeId, name);
    }

    public static void logEmployeeDeleted(Long employeeId) {
        businessLogger.warn("Çalışan silindi: id={}", employeeId);
    }
}