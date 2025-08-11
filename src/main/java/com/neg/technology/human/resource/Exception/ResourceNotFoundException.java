package com.neg.technology.human.resource.Exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(resourceName + " with identifier '" + identifier + "' not found.");
    }
}
