package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeInteger implements AttributeType<Integer> {
	public static Class<Integer> getType() {
		return Integer.class;
	}
}