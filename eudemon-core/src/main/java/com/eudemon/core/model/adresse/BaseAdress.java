package com.eudemon.core.model.adresse;

import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="ADRESS_TYPE")
public class BaseAdress {
    @Id
    Long id;

List<AdressScheme> adresseSchemes;

String name;
String adresse;

}
