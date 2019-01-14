package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeLong implements AttributeType<String> {
	public static Class<String> getType() {
		return String.class;
	}
}