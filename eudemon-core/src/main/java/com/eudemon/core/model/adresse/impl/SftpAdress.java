package com.eudemon.core.model.adresse.impl;

import com.eudemon.core.model.adresse.BaseAdress;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("SFTP")
public class SftpAdress extends BaseAdress {
    String privatekey;
    String passphrase;
    String username;
    String password;
    String remotelocation;
}
