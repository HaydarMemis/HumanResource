package com.neg.technology.human.resource.company.validator;

import com.neg.technology.human.resource.company.model.request.CreateProjectRequest;
import com.neg.technology.human.resource.company.model.request.UpdateProjectRequest;
import com.neg.technology.human.resource.company.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public ProjectValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Mono<Void> validateCreate(CreateProjectRequest dto) {
        return Mono.fromCallable(() -> {
                    if (!StringUtils.hasText(dto.getName())) {
                        throw new IllegalArgumentException("Project name must not be empty");
                    }
                    return dto;
                })
                .then(Mono.defer(() -> {
                    boolean exists = projectRepository.existsByName(dto.getName());
                    if (exists) {
                        return Mono.error(new IllegalArgumentException("Project name already exists"));
                    }
                    return Mono.empty();
                }))
                .then();
    }

    public Mono<Void> validateUpdate(UpdateProjectRequest dto, Long id) {
        return Mono.fromCallable(() -> {
                    if (!StringUtils.hasText(dto.getName())) {
                        throw new IllegalArgumentException("Project name must not be empty");
                    }
                    return dto;
                })
                .then(Mono.defer(() -> {
                    return projectRepository.findByName(dto.getName())
                            .map(existing -> {
                                if (!existing.getId().equals(id)) {
                                    throw new IllegalArgumentException("Project name already exists");
                                }
                                return Mono.<Void>empty();
                            })
                            .orElse(Mono.empty());
                }))
                .then();
    }
}