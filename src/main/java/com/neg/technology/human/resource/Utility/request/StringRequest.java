package com.neg.technology.human.resource.Utility.request;

import jakarta.validation.constraints.NotBlank;

public class StringRequest {
    @NotBlank
    private String value;

    public StringRequest() {}

    public StringRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
