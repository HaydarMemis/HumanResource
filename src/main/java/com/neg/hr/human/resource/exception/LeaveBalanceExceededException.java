package com.neg.hr.human.resource.exception;

public class LeaveBalanceExceededException extends RuntimeException {
    public LeaveBalanceExceededException(String message) {
        super(message);
    }
}
