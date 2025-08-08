package com.neg.hr.human.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NameRequestDTO {

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    // Standard getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Optional: toString() for better logging
    @Override
    public String toString() {
        return "NameRequestDTO{name='" + name + "'}";
    }
}