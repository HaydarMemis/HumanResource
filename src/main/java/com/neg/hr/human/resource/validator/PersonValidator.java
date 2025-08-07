package com.neg.hr.human.resource.validator;

import com.neg.hr.human.resource.dto.create.CreatePersonDTO;
import com.neg.hr.human.resource.dto.update.UpdatePersonDTO;
import com.neg.hr.human.resource.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonValidator {

    private final PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    public void validateCreate(CreatePersonDTO dto) {
        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new IllegalArgumentException("First name must not be empty");
        }
        if (dto.getEmail() != null && personService.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (dto.getNationalId() != null && personService.existsByNationalId(dto.getNationalId())) {
            throw new IllegalArgumentException("National ID already exists");
        }
        // Add any additional validation rules here
    }

    public void validateUpdate(UpdatePersonDTO dto, Long personId) {
        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new IllegalArgumentException("First name must not be empty");
        }
        if (dto.getEmail() != null) {
            personService.findByEmailIgnoreCase(dto.getEmail()).ifPresent(existingPerson -> {
                if (!existingPerson.getId().equals(personId)) {
                    throw new IllegalArgumentException("Email already exists");
                }
            });
        }
        if (dto.getNationalId() != null) {
            personService.findByNationalId(dto.getNationalId()).ifPresent(existingPerson -> {
                if (!existingPerson.getId().equals(personId)) {
                    throw new IllegalArgumentException("National ID already exists");
                }
            });
        }
    }
}
