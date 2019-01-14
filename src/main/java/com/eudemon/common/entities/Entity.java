package com.eudemon.common.entities;

import java.util.LinkedHashMap;
import java.util.Map;

public class Entity {
	String _package;
	String _name;
	Map<String, Attribute> _fields = new LinkedHashMap<String, Attribute>();
	Map<String, Object> _values = new LinkedHashMap<String, Object>();

	public Entity(String _package, String _name) {
		super();
		this._package = _package;
		this._name = _name;
	}

	public <T> void setAttribute(String id, T value) throws Exception {
		final Attribute field = this._fields.get(id);
		if (field == null)
			throw new Exception("unsuported field name " + id);
		this._values.put(field.getName(), value);
	}

	public <T> void setAttribute(Attribute field, T value) throws Exception {
		final Attribute _field = this._fields.get(field.getName());
		if (value!=null && !field.getType().isInstance(value)) {
			throw new Exception(
					"the specified Object is not assignment-compatible with the object represented by the Field :" +field.getName() );
		}
		if (_field != null && !_field.equals(field))
			throw new Exception("unsuported field name " + field.getName());
		if (_field == null)
			this._fields.put(field.getName(), field);
		this._values.put(field.getName(), value);
	}

	@SuppressWarnings("unchecked")
	public Object getAttributeValue(Attribute field) {
		return this._values.get(field.getName());
	}

	@SuppressWarnings("unchecked")
	public Object getAttributeValue(String id) {
		return this._values.get(id);
	}

	@Override
	public String toString() {
		return _package + "." + _name + _values;
	}
}