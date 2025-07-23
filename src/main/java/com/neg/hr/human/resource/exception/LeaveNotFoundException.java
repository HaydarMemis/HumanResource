package com.neg.hr.human.resource.exception;

public class LeaveNotFoundException extends RuntimeException {
    public LeaveNotFoundException(String message) {
        super(message);
    }
}
