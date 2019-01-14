package com.eudemon.core.model.contact;

import com.eudemon.core.model.adresse.AdressScheme;
import com.eudemon.core.model.adresse.BaseAdress;
import com.eudemon.core.model.base.AbstractAuditingEntity;
import com.eudemon.core.model.base.Group;
import com.eudemon.core.model.base.Status;
import com.eudemon.core.model.organisation.Organisaton;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "CORE_CONTACTS")
public class Contact extends AbstractAuditingEntity {

    List<ContactRole> roles;
    String name;
    String description;
    Organisaton owner;
    Map<AdressScheme,Group<?extends BaseAdress>> adresses;
    Status status;

    public List<ContactRole> getRoles() {
        return roles;
    }

    public void setRoles(List<ContactRole> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Organisaton getOwner() {
        return owner;
    }

    public void setOwner(Organisaton owner) {
        this.owner = owner;
    }

    public Map<AdressScheme, Group<? extends BaseAdress>> getAdresses() {
        return adresses;
    }

    public void setAdresses(Map<AdressScheme, Group<? extends BaseAdress>> adresses) {
        this.adresses = adresses;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
