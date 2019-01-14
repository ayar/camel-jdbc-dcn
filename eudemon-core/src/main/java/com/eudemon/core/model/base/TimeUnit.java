package com.eudemon.core.model.base;

import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("TIME_UNITS")
@JsonTypeName("TIME_UNITS")
public class TimeUnit extends Nomenclature {
}
