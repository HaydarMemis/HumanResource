package com.neg.technology.human.resource.Department.model.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentResponseList {
    private List<DepartmentResponse> departments;
}
