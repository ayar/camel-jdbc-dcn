package com.eudemon.common.entities.attributes;

import java.util.List;

import com.eudemon.common.entities.AttributeType;

public class AttributeList implements AttributeType<List> {
	public static Class<List> getType() {
		return List.class;
	}
}