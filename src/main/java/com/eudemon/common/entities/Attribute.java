package com.eudemon.common.entities;


import java.lang.reflect.InvocationTargetException;

public class Attribute {
	String _name;
	Class<? extends AttributeType> _type;

	public Attribute(String name, Class<? extends AttributeType> type) {
		this._name = name;
		this._type = type;
	}

	public String getName() {
		return _name;
	}

	public Class<?> getType() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		return (Class<?>) this._type.getMethod("getType").invoke(null);
	}

	@Override
	public String toString() {
		return "Attribute [_name=" + _name + ", _type=" + _type + "]";
	}
}