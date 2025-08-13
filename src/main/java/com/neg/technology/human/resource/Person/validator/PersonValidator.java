package com.neg.technology.human.resource.Person.validator;

import com.neg.technology.human.resource.Person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.Person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.Person.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PersonValidator {

    private final PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    public void validateCreate(CreatePersonRequest dto) {
        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new IllegalArgumentException("First name must not be empty");
        }

        // Email var ise ve zaten kayıtlı ise hata fırlat
        if (dto.getEmail() != null && personService.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        // NationalId var ise ve zaten kayıtlı ise hata fırlat
        if (dto.getNationalId() != null && personService.existsByNationalId(dto.getNationalId())) {
            throw new IllegalArgumentException("National ID already exists");
        }
    }

    public void validateUpdate(UpdatePersonRequest dto, Long personId) {
        if (!StringUtils.hasText(dto.getFirstName())) {
            throw new IllegalArgumentException("First name must not be empty");
        }

        // Email varsa ve aynı kişi değilse hata fırlat
        if (dto.getEmail() != null) {
            personService.findByEmailIgnoreCase(dto.getEmail())
                    .ifPresent(existingPerson -> {
                        if (!existingPerson.getId().equals(personId)) {
                            throw new IllegalArgumentException("Email already exists");
                        }
                    });
        }

        // NationalId varsa ve aynı kişi değilse hata fırlat
        if (dto.getNationalId() != null) {
            personService.findByNationalId(dto.getNationalId())
                    .ifPresent(existingPerson -> {
                        if (!existingPerson.getId().equals(personId)) {
                            throw new IllegalArgumentException("National ID already exists");
                        }
                    });
        }
    }
}
