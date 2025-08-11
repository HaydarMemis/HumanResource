package com.neg.technology.human.resource.dto.utilities;

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