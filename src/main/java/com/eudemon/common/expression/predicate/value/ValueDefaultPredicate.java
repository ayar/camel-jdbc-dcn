package com.eudemon.common.expression.predicate.value;

import com.eudemon.common.expression.Expression;
import com.eudemon.common.expression.bed.BedComparator;
import com.eudemon.common.expression.bed.BinaryExpression;
import com.eudemon.common.expression.operation.ArithmeticOperator;
import com.eudemon.common.expression.predicate.single.SinglePredicate;

public class ValueDefaultPredicate<T extends Comparable<T> , E>  extends SinglePredicate<T,E> {
	T condition;

	public T getCondition() {
		return condition;
	}

	public ValueDefaultPredicate(Expression<? extends T, E> expression, ArithmeticOperator operator, T value) {
		super(expression,operator);
		this.condition = value;
	}

	@Override
	public boolean test(E element) {
		boolean result =this.getOperator().evalute(this.condition, getExpression().getValue(element));	
		return result&&getHight().test(element) || (!result)&&getLow().test(element);

	}

	
	@Override
	public BinaryExpression<E> copy() {
		BinaryExpression<E> a = new ValueDefaultPredicate<T , E>(getExpression(),getOperator(),this.condition);
		a.setHight(getHight()); //getLow().copy()
		a.setLow(getLow());//getHight().copy()
		return a;
	}

	@Override
	public String toString() {
		return  expression + " " + operator + " " + condition + "--true-->" + getHight().toString() +"," +
				expression + " " + operator + " " + condition + "--false-->" + getLow().toString();
				
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((condition == null) ? 0 : condition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof ValueDefaultPredicate))
			return false;
		if (!super.equals(obj))
			return false;
		ValueDefaultPredicate other = (ValueDefaultPredicate) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		return true;
	}



	
}