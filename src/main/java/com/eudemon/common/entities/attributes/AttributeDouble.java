package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeDouble implements AttributeType<Double> {
	public static Class<Double> getType() {
		return Double.class;
	}
}