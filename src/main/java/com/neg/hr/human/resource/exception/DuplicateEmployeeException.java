package com.neg.hr.human.resource.exception;

public class DuplicateEmployeeException extends RuntimeException {
    public DuplicateEmployeeException(String nationalId) {
        super("An employee with national ID " + nationalId + " already exists.");
    }
}

