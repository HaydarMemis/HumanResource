package com.neg.technology.human.resource.dto.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartmentEntityDTO {
    private Long id;
    private String name;
    private String location;
}
