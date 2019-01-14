package com.eudemon.common.entities;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.eudemon.common.entities.attributes.*;

public class EntityFactory {
	public static Map<Class, Class<? extends AttributeType>> supportedType = new LinkedHashMap<Class, Class<? extends AttributeType>>();
	public static Map<Class, Class<?>> premitivesType = new LinkedHashMap<Class, Class<?>>();
	static {
		EntityFactory.supportedType.put(Boolean.class, AttributeBoolean.class);
		EntityFactory.supportedType.put(Byte.class, AttributeByte.class);
		EntityFactory.supportedType.put(Character.class, AttributeCharacter.class);
		EntityFactory.supportedType.put(Date.class, AttributeDate.class);
		EntityFactory.supportedType.put(Double.class, AttributeDouble.class);
		EntityFactory.supportedType.put(Entity.class, AttributeEntity.class);
		EntityFactory.supportedType.put(Float.class, AttributeFloat.class);
		EntityFactory.supportedType.put(Integer.class, AttributeInteger.class);
		EntityFactory.supportedType.put(List.class, AttributeList.class);
		EntityFactory.supportedType.put(Long.class, AttributeLong.class);
		EntityFactory.supportedType.put(Short.class, AttributeShort.class);
		EntityFactory.supportedType.put(String.class, AttributeString.class);
		EntityFactory.supportedType.put(Map.class, AttributeMap.class);
		
		EntityFactory.premitivesType.put(boolean.class, Boolean.class);
		EntityFactory.premitivesType.put(char.class, Character.class);
		EntityFactory.premitivesType.put(byte.class, Byte.class);
		EntityFactory.premitivesType.put(short.class, Short.class);
		EntityFactory.premitivesType.put(int.class, Integer.class);
		EntityFactory.premitivesType.put(long.class, Long.class);
		EntityFactory.premitivesType.put(float.class, Float.class);
		EntityFactory.premitivesType.put(double.class, Double.class);
		
	}

	public static Entity from(Object obj) throws Exception {
		if (obj != null && !supportedType.containsKey(obj.getClass())) {
			final Entity e = new Entity(obj.getClass().getPackage().getName(), obj.getClass().getSimpleName());
			for (final Field f : obj.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				Class<? extends AttributeType> att = supportedType
						.get(f.getType().isPrimitive() ? premitivesType.get(f.getType()) : f.getType());
				if (att == null) {
					att = supportedType.get(f.getType().getSuperclass());
				}
				if (att == null && !f.getType().isEnum()) {
					att = AttributeEntity.class;
				}
				if (att != null) {
					final Attribute a = new Attribute(f.getName(), att);
					if (att == AttributeEntity.class) {
						e.setAttribute(a, from(f.get(obj)));
					} else {
						if (att == AttributeList.class && f.get(obj) != null) {
							final List l = (List) f.get(obj);
							final List nl = new ArrayList<>();
							l.forEach(ee -> {
								if (ee == null || (ee != null && ((supportedType.containsKey(ee.getClass())
										|| premitivesType.containsKey(ee.getClass()))))) {
									nl.add(ee);
								} else {
									try {
										nl.add(from(ee));
									} catch (final Exception e1) {
										throw new RuntimeException("unable to  read the object");
									}
								}
							});
							e.setAttribute(a, nl);
						} else {
							e.setAttribute(a, f.get(obj));
						}
					}
				}
			}
			return e;
		}
		return null;
	}
}