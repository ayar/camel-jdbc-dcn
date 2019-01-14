package com.eudemon.core.model.agreement;

import com.eudemon.core.model.base.Nomenclature;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("PRODUCTS")
@JsonTypeName("PRODUCTS")
public class Product extends Nomenclature {
}
