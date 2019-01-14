package com.eudemon.core.model.contact;


import com.eudemon.core.model.base.Nomenclature;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("CONTACTS")
@JsonTypeName("CONTACTS")
public class ContactRole extends Nomenclature {
}
