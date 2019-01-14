package com.eudemon.common.entities.attributes;

import java.util.Date;

import com.eudemon.common.entities.AttributeType;

public class AttributeDate implements AttributeType<Date> {
	public static Class<Date> getType() {
		return Date.class;
	}
}