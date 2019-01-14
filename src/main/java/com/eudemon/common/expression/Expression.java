package com.eudemon.common.expression;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;

import com.eudemon.common.expression.bed.BinaryExpression;
import com.eudemon.common.expression.operation.ArithmeticOperator;
import com.eudemon.common.expression.predicate.single.SinglePredicate;
import com.eudemon.common.expression.predicate.value.ValueCollectionInPredicate;
import com.eudemon.common.expression.predicate.value.ValueDefaultPredicate;

public abstract class Expression<T extends Comparable<T> , E> implements Comparable<Expression<?,E>> {
	public abstract String getName();

	public  T getValue(E element) {
		Object current = element;
		for (final String path : getName().split("\\.")) {
			Field f;
			try {
				f = current.getClass().getDeclaredField(path);
				f.setAccessible(true);
				current = f.get(element);
			} catch (final Exception e) {
				throw new RuntimeException("unable to read element from object", e);
			}
		}
		return current == null ? null : (T) current;
	}

	public BinaryExpression<E> isNull() {
		return new SinglePredicate<T, E>(this, ArithmeticOperator.IS_NULL);
	}

	public BinaryExpression<E> isNotNull() throws Exception {
		return isNull().not();
	}

	public BinaryExpression<E> in(T... values) throws Exception {
		return new ValueCollectionInPredicate<T, E>(this, ArithmeticOperator.IN, Arrays.asList(values));
	}

	public BinaryExpression<E> in(Collection< T> collection)  {
		return new ValueCollectionInPredicate<T, E>(this, ArithmeticOperator.IN, collection);
	}

	



	public BinaryExpression<E> notIn(T... values) throws Exception {
		return in(values).not();
	}

	public BinaryExpression<E> notIn(Collection<T> collections) throws Exception {
		return in(collections).not();
	}

	


	public BinaryExpression<E> isEqual(T value) {
		return new ValueDefaultPredicate<T, E>(this, ArithmeticOperator.IS_EQUAL, value);
	}

	


	public BinaryExpression<E> notEqual(T value) {
		return isEqual(value).not();
	}
	
	public BinaryExpression<E> lessThan(T value) {
		return new ValueDefaultPredicate<T, E>(this, ArithmeticOperator.LessThan, value);
		
	}

	public BinaryExpression<E> lessThanOrEqualTo(T value) {
		return new ValueDefaultPredicate<T, E>(this, ArithmeticOperator.LessThanOrEqual, value);
			}
	

	public BinaryExpression<E> greaterThan(T value) {
		return  lessThanOrEqualTo(value).not();
	}

	

	public BinaryExpression<E> greaterThanOrEqualTo(T value) {
		return  lessThan(value).not();
	}

	

	

	

	public BinaryExpression<E> between(T value1, T value2) {
		return greaterThanOrEqualTo(value1).and(lessThanOrEqualTo(value2));
	}
	
	

	public BinaryExpression<E> like(T value) {
		return new ValueDefaultPredicate<T, E>(this, ArithmeticOperator.LIKE, value);
	}

	public BinaryExpression<E> notLike(T x) {
		return like(x).not();
	}
	
	
	public int compareTo(Expression<?,E> o) {
		return this.getName().compareTo(o.getName());
	}
	
	public String toString() {
	return getName();
	}
}
