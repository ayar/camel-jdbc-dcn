package com.eudemon.core.model.base;

import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("REGION")
@JsonTypeName("REGION")
public class Region extends Nomenclature {
}
