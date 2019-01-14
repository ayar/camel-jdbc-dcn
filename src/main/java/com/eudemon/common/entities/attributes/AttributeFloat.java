package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeFloat implements AttributeType<Float> {
	public static Class<Float> getType() {
		return Float.class;
	}
}