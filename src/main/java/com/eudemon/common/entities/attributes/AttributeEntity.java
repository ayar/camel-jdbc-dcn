package com.eudemon.common.entities.attributes;


import com.eudemon.common.entities.AttributeType;
import com.eudemon.common.entities.Entity;

public class AttributeEntity implements AttributeType<Entity> {
	public static Class<Entity> getType() {
		return Entity.class;
	}
}

