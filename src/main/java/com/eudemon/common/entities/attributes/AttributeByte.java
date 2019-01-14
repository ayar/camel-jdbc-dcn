package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeByte implements AttributeType<Byte> {
	public static Class<Byte> getType() {
		return Byte.class;
	}
}