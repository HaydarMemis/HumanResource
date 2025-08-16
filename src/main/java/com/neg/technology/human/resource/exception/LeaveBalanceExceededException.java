package com.neg.technology.human.resource.exception;

public class LeaveBalanceExceededException extends RuntimeException {
    public LeaveBalanceExceededException(String message) {
        super(message);
    }
}
