package com.eudemon.core.model.adresse;


import com.eudemon.core.model.base.Nomenclature;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("ADRESS")
@JsonTypeName("ADRESS")
public class AdressScheme extends Nomenclature {
}
