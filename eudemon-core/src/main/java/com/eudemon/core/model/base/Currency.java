package com.eudemon.core.model.base;

import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("CURRENCY")
@JsonTypeName("CURRENCY")
public class Currency extends Nomenclature {
}
