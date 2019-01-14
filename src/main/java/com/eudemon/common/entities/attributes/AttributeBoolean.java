package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeBoolean implements AttributeType<Boolean> {
	public static Class<Boolean> getType() {
		return Boolean.class;
	}
}