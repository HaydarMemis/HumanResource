package com.neg.technology.human.resource.Person.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonResponseList {
    private List<PersonResponse> persons;
}
