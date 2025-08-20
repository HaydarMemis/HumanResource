package com.neg.technology.human.resource.person.validator;

import com.neg.technology.human.resource.person.model.request.CreatePersonRequest;
import com.neg.technology.human.resource.person.model.request.UpdatePersonRequest;
import com.neg.technology.human.resource.person.service.PersonService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class PersonValidator {

    private final PersonService personService;

    public PersonValidator(PersonService personService) {
        this.personService = personService;
    }

    public Mono<Void> validateCreate(CreatePersonRequest dto) {
        return Mono.fromCallable(() -> {
                    if (!StringUtils.hasText(dto.getFirstName())) {
                        throw new IllegalArgumentException("First name must not be empty");
                    }
                    return dto;
                })
                .then(Mono.defer(() -> {
                    if (dto.getEmail() != null) {
                        return personService.existsByEmail(dto.getEmail())
                                .flatMap(exists -> exists ?
                                        Mono.error(new IllegalArgumentException("Email already exists")) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                }))
                .then(Mono.defer(() -> {
                    if (dto.getNationalId() != null) {
                        return personService.existsByNationalId(dto.getNationalId())
                                .flatMap(exists -> exists ?
                                        Mono.error(new IllegalArgumentException("National ID already exists")) :
                                        Mono.empty());
                    }
                    return Mono.empty();
                }))
                .then();
    }
    public Mono<Void> validateUpdate(UpdatePersonRequest dto, Long personId) {
        return validateFirstName(dto)
                .then(validateEmailUniqueness(dto, personId))
                .then(validateNationalIdUniqueness(dto, personId));
    }

    private Mono<Void> validateFirstName(UpdatePersonRequest dto) {
        return Mono.fromCallable(() -> {
            if (!StringUtils.hasText(dto.getFirstName())) {
                throw new IllegalArgumentException("First name must not be empty");
            }
            return null;
        });
    }
    private Mono<Void> validateEmailUniqueness(UpdatePersonRequest dto, Long personId) {
        if (dto.getEmail() == null) {
            return Mono.empty().then();
        }

        return personService.findByEmailIgnoreCase(dto.getEmail())
                .flatMap(existingPerson -> {
                    if (!existingPerson.getId().equals(personId)) {
                        return Mono.<Void>error(new IllegalArgumentException("Email already exists"));
                    }
                    return Mono.<Void>empty();
                })
                .onErrorResume(this::handleNotFoundError);
    }

    private Mono<Void> validateNationalIdUniqueness(UpdatePersonRequest dto, Long personId) {
        if (dto.getNationalId() == null) {
            return Mono.empty().then();
        }

        return personService.findByNationalId(dto.getNationalId())
                .flatMap(existingPerson -> {
                    if (!existingPerson.getId().equals(personId)) {
                        return Mono.<Void>error(new IllegalArgumentException("National ID already exists"));
                    }
                    return Mono.<Void>empty();
                })
                .onErrorResume(this::handleNotFoundError);
    }

    private Mono<Void> handleNotFoundError(Throwable e) {
        if (e.getMessage() != null && e.getMessage().contains("not found")) {
            return Mono.empty().then();
        }
        return Mono.<Void>error(e);
    }
}