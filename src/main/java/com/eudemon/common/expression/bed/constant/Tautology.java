package com.eudemon.common.expression.bed.constant;

import com.eudemon.common.expression.bed.BinaryExpression;
import com.eudemon.common.expression.bed.ConstantExpression;

public class Tautology<E> extends ConstantExpression<E> {


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
	
	@Override
	public int compareTo(BinaryExpression<E> obj) {
		if ( obj  instanceof Tautology) return 0;
		if (obj  instanceof  Contradiction) return 1;
		return -1;
	}

	@Override
	public BinaryExpression<E> copy() {
		// TODO Auto-generated method stub
		return new Tautology<E>();
	}

	@Override
	public boolean test(E element) {
		return true;
	}
	@Override
	public BinaryExpression<E> getRoot() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public String toString() {
		return "true";
	}



}
