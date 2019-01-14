package com.eudemon.core.model.holidays;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class CronExpressionConverter implements AttributeConverter<CronTabExpression,String> {
    @Override
    public String convertToDatabaseColumn(CronTabExpression cronTabExpression) {
        return cronTabExpression==null? null:cronTabExpression.getExpression();
    }

    @Override
    public CronTabExpression convertToEntityAttribute(String s) {
        return StringUtils.isBlank(s)?null: new CronTabExpression(s);
    }
}
