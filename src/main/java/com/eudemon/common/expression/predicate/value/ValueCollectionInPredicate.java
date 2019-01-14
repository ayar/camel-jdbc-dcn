package com.eudemon.common.expression.predicate.value;

import java.util.Collection;

import org.apache.commons.collections4.CollectionUtils;

import com.eudemon.common.expression.Expression;
import com.eudemon.common.expression.bed.BedComparator;
import com.eudemon.common.expression.bed.BinaryExpression;
import com.eudemon.common.expression.operation.ArithmeticOperator;
import com.eudemon.common.expression.predicate.single.SinglePredicate;


public class ValueCollectionInPredicate<T extends Comparable<T>  , E>  extends SinglePredicate<T,E> {
	Collection<T> condition;

	public Collection<T> getCondition() {
		return condition;
	}

	public ValueCollectionInPredicate( Expression<? extends T, E> expression, ArithmeticOperator operator,
			Collection<T> collection) {
		super(expression,operator);
		this.condition = collection;
	} 

	@Override
	public boolean test(E element) {
		boolean result =getOperator().evalute(getExpression().getValue(element), condition);
		return result&&getHight().test(element) || (!result)&&getLow().test(element);

	}





	@Override
	public int compareTo(BinaryExpression<E> o) {
		return BedComparator.INSTANCE.compare(this, o);
	}


	@Override
	public BinaryExpression<E> copy() {
		BinaryExpression<E> a = new ValueCollectionInPredicate<T , E>(getExpression(),getOperator(),this.condition);
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
	
		if (!(obj instanceof ValueCollectionInPredicate))
			return false;
		if (!super.equals(obj))
			return false;
		ValueCollectionInPredicate other = (ValueCollectionInPredicate) obj;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if ( !CollectionUtils.isEqualCollection(condition, other.condition))
			return false;
		return true;
	}

}