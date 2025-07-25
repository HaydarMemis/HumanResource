package com.neg.hr.human.resource.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " with identifier '" + identifier + "' not found.");
    }
}
