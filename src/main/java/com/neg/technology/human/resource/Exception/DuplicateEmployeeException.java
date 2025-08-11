package com.neg.technology.human.resource.Exception;

public class DuplicateEmployeeException extends RuntimeException {
    public DuplicateEmployeeException(String nationalId) {
        super("An employee with national ID " + nationalId + " already exists.");
    }
}

