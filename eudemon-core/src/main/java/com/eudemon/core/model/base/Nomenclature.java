package com.eudemon.core.model.base;


import org.codehaus.jackson.annotate.JsonTypeInfo;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "scheme")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="scheme")
@Table(name = "CORE_NOMENCLATURE", uniqueConstraints = @UniqueConstraint(columnNames={"scheme", "code"}))
public class Nomenclature implements Serializable {

    private static final long serialVersionUID = 1L;

    public Nomenclature(){

    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "scheme", nullable = false,updatable = false)
    private String scheme;

    @NotNull
    @Column(name = "code", nullable = false,updatable = false)
    private String code;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public Nomenclature scheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getCode() {
        return code;
    }

    public Nomenclature code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public Nomenclature name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public Nomenclature status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Nomenclature nomenclature = (Nomenclature) o;
        if (nomenclature.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), nomenclature.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Nomenclature{" +
            "id=" + getId() +
            ", scheme='" + getScheme() + "'" +
            ", code='" + getCode() + "'" +
            ", name='" + getName() + "'" +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
