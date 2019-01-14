package com.eudemon.common.expression.operation;

import java.util.Collection;
import java.util.regex.Pattern;

public enum ArithmeticOperator implements BinaryOperation<Object,Boolean>{
	//order of this enum is very important op1<op
					 IS_NULL(0,(a, b) -> a == null)
					 ,IS_EMPTY(1,(a, b) -> ((Collection<Object>) a).isEmpty())
					,IS_EQUAL(2,(a, b) -> a.equals(b))
					,LIKE(3,(a, b) -> Pattern.compile((String) a).matcher((CharSequence) b).matches())
					,IN(4,(a, b) -> ((Collection<Object>) b).contains(a))
					,LessThan(5, (a, b) -> ((Comparable)a).compareTo(b) > 0)
					,LessThanOrEqual(6,(a, b) -> ((Comparable)a).compareTo(b) >= 0)
					;
	
	
	private final BinaryOperation<Object,Boolean> op;
	public Integer order;

	private ArithmeticOperator(Integer order, BinaryOperation<Object,Boolean> op) {
		this.op = op;
		this.order = order;
	}



	



	@Override
	public Boolean evalute( Object a, Object b) {
		// TODO Auto-generated method stub
		return op.evalute(a, b);
	}



}
