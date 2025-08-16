package com.neg.technology.human.resource.employee.model.response;

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
