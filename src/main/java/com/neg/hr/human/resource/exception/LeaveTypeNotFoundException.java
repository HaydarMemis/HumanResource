package com.neg.hr.human.resource.exception;

public class LeaveTypeNotFoundException extends RuntimeException {
    public LeaveTypeNotFoundException(Long id) {
        super("Leave type not found with id: " + id);
    }
}
