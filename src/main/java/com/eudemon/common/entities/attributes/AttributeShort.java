package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeShort implements AttributeType<Short> {
	public static Class<Short> getType() {
		return Short.class;
	}
}