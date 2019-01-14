package com.eudemon.common.entities.attributes;

import java.util.Map;

import com.eudemon.common.entities.AttributeType;

public class AttributeMap implements AttributeType<Map> {
	public static Class<Map> getType() {
		return Map.class;
	}
}