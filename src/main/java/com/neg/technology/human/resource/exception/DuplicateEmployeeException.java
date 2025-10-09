package com.neg.technology.human.resource.exception;

public class DuplicateEmployeeException extends RuntimeException {

    public DuplicateEmployeeException() {
        super("Bu çalışan zaten mevcut.");
    }

    // ileride duruma göre artırabiliriz
    public static DuplicateEmployeeException employeeAlreadyExists() {
        return new DuplicateEmployeeException();
    }
}
