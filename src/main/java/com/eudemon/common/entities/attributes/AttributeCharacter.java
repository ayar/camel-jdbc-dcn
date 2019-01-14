package com.eudemon.common.entities.attributes;

import com.eudemon.common.entities.AttributeType;

public class AttributeCharacter implements AttributeType<Character> {
	public static Class<Character> getType() {
		return Character.class;
	}
}