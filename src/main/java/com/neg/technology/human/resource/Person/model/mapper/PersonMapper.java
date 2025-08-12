package com.neg.technology.human.resource.Person.model.mapper;

import com.neg.technology.human.resource.Person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.Person.model.response.PersonResponse;
import com.neg.technology.human.resource.Person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.Person.model.entity.Person;

public class PersonMapper {

    public static PersonResponse toDTO(Person person) {
        if (person == null) return null;

        return new PersonResponse(
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                person.getNationalId(),
                person.getBirthDate(),
                person.getGender(),
                person.getEmail(),
                person.getPhone(),
                person.getAddress(),
                person.getMaritalStatus()
        );
    }

    public static Person toEntity(CreatePersonRequest dto) {
        if (dto == null) return null;

        return Person.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .nationalId(dto.getNationalId())
                .birthDate(dto.getBirthDate())
                .gender(dto.getGender())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .maritalStatus(dto.getMaritalStatus())
                .build();
    }

    public static void updateEntity(Person person, UpdatePersonRequest dto) {
        if (person == null || dto == null) return;

        if (dto.getFirstName() != null) person.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) person.setLastName(dto.getLastName());
        if (dto.getNationalId() != null) person.setNationalId(dto.getNationalId());
        if (dto.getBirthDate() != null) person.setBirthDate(dto.getBirthDate());
        if (dto.getGender() != null) person.setGender(dto.getGender());
        if (dto.getEmail() != null) person.setEmail(dto.getEmail());
        if (dto.getPhone() != null) person.setPhone(dto.getPhone());
        if (dto.getAddress() != null) person.setAddress(dto.getAddress());
        if (dto.getMaritalStatus() != null) person.setMaritalStatus(dto.getMaritalStatus());
    }
}
