package com.eudemon.common.entities;

public interface AttributeType<T> {
	static Class<?> getType() {
		return Object.class;
	}
}