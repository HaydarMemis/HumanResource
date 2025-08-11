package com.neg.technology.human.resource.Exception;

public class LeaveBalanceExceededException extends RuntimeException {
    public LeaveBalanceExceededException(String message) {
        super(message);
    }
}
