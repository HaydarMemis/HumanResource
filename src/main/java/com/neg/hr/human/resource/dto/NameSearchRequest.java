package com.neg.hr.human.resource.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NameSearchRequest {
    private String firstName;
    private String lastName;
}