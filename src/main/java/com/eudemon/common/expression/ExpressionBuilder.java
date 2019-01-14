package com.eudemon.common.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionBuilder {

	
	private ExpressionBuilder(){
		
	}
	public  static <T extends Comparable<T>,E> Expression<T, E> make(Class<E> entity, String name, Class<T> type) {
		return  new Expression<T, E>() {
			@Override
			public String getName() {
				return name;
			}
		};
	
	}
}