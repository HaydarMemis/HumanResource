package com.neg.hr.human.resource.exception;

public class PositionNotFoundException extends RuntimeException {
    public PositionNotFoundException(Long id) {
        super("Position not found with id: " + id);
    }
}
