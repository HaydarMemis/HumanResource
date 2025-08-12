package com.neg.technology.human.resource.Employee.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeListResponse {
    private List<EmployeeResponse> employees;
}
