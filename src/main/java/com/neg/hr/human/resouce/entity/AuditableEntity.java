package com.neg.hr.human.resouce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity {

    @Column(name = "deleted", length = 1)
    protected String deleted = "N";
}
