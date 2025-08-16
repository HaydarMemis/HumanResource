package com.neg.technology.human.resource.utility.module.entity.request;

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