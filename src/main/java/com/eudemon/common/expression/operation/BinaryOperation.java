package com.eudemon.common.expression.operation;

@FunctionalInterface
public interface BinaryOperation<T,E> {
	E evalute(T a, T b);
}
