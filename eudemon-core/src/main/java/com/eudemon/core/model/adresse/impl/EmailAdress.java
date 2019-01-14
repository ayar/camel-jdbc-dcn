package com.eudemon.core.model.adresse.impl;

import com.eudemon.core.model.adresse.BaseAdress;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("EMAIL")
public class EmailAdress extends BaseAdress {
    String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
