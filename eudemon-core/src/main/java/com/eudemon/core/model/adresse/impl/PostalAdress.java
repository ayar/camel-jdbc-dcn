package com.eudemon.core.model.adresse.impl;

import com.eudemon.core.model.adresse.BaseAdress;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("POSTAL")
public class PostalAdress extends BaseAdress {
    String adresse2;
    String adresse3;
    String zipcode;
    String city;

    public String getAdresse2() {
        return adresse2;
    }

    public void setAdresse2(String adresse2) {
        this.adresse2 = adresse2;
    }

    public String getAdresse3() {
        return adresse3;
    }

    public void setAdresse3(String adresse3) {
        this.adresse3 = adresse3;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
