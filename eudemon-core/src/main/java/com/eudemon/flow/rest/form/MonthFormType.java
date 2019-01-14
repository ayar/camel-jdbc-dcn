package com.eudemon.flow.rest.form;

import com.eudemon.flow.engine.form.AbstractFormType;


public class MonthFormType extends AbstractFormType {

    private static final long serialVersionUID = 1L;

    public static final String TYPE_NAME = "month";

    @Override
    public String getName() {
        return TYPE_NAME;
    }

    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
        Integer month = Integer.valueOf(propertyValue);
        return month;
    }

    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        if (modelValue == null) {
            return null;
        }
        return modelValue.toString();
    }
}
