package com.eudemon.core.model.holidays;

import com.eudemon.core.model.base.Nomenclature;
import org.codehaus.jackson.annotate.JsonTypeName;

import javax.persistence.DiscriminatorValue;

@DiscriminatorValue("HOLIDAYS")
@JsonTypeName("HOLIDAYS")
public class HolidayCalendarType  extends Nomenclature{
}
