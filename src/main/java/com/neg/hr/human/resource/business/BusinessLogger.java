package com.neg.hr.human.resource.business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusinessLogger {

    private static final Logger logger = LoggerFactory.getLogger("com.neg.hr.human.resource.business");

    // Genel loglama metotları — entity tipi, id ve isim bilgisi ile
    public static void logCreated(Class<?> clazz, Long id, String name) {
        logger.info("{} created - ID: {}, Name: {}", clazz.getSimpleName(), id, name);
    }

    public static void logDeleted(Class<?> clazz, Long id) {
        logger.warn("{} deleted - ID: {}", clazz.getSimpleName(), id);
    }

    public static void logUpdated(Class<?> clazz, Long id, String name) {
        logger.info("{} updated - ID: {}, Name: {}", clazz.getSimpleName(), id, name);
    }

    public static void logEmployeeCreated(Long id, String fullName) {
        logger.info("Employee created - ID: {}, Name: {}", id, fullName);
    }

    public static void logEmployeeDeleted(Long id) {
        logger.warn("Employee deleted - ID: {}", id);
    }

    public static void logEmployeeUpdated(Long id, String fullName) {
        logger.info("Employee updated - ID: {}, Name: {}", id, fullName);
    }
}
