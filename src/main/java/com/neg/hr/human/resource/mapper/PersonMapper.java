package com.neg.hr.human.resource.mapper;

import com.neg.hr.human.resource.dto.*;
import com.neg.hr.human.resource.entity.Person;

public class PersonMapper {

    public static PersonDTO toDTO(Person person) {
        if (person == null) return null;

        return new PersonDTO(
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

    public static Person toEntity(CreatePersonDTO dto) {
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

    public static void updateEntity(Person person, UpdatePersonDTO dto) {
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
