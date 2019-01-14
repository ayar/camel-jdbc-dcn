package com.eudemon.common.expression.predicate.single;

import com.eudemon.common.expression.Expression;
import com.eudemon.common.expression.bed.BedComparator;
import com.eudemon.common.expression.bed.BinaryExpression;
import com.eudemon.common.expression.bed.constant.Contradiction;
import com.eudemon.common.expression.bed.constant.Tautology;
import com.eudemon.common.expression.operation.ArithmeticOperator;

public class SinglePredicate<T , E> extends BinaryExpression<E> {
	protected Expression<? extends T, E> expression;
	protected ArithmeticOperator operator;

	public Expression<? extends T, E> getExpression() {
		return expression;
	}

	public ArithmeticOperator getOperator() {
		return operator;
	}

	public SinglePredicate(final Expression<? extends T, E> expression, ArithmeticOperator operator) {
		this.expression = expression;
		this.operator = operator;
		setHight(new Tautology<E>());
		setLow(new Contradiction<E>());
	}

	@Override
	public boolean test(E element) {
		boolean result =this.operator.evalute(expression.getValue(element), null);	
		return result&&getHight().test(element) || (!result)&&getLow().test(element);
	}

	@Override
	public int compareTo(BinaryExpression<E> o) {
		// TODO Auto-generated method stub
		return BedComparator.INSTANCE.compare(this, o);
	}

	@Override
	public BinaryExpression<E> copy() {
		BinaryExpression<E> a = new SinglePredicate<T , E>(this.expression,this.operator);
		a.setHight(getHight()); //getLow().copy()
		a.setLow(getLow());//getHight().copy()
		return a;
	}

	@Override
	public BinaryExpression<E> getRoot() {
		// TODO Auto-generated method stub
		return this;
	}


		@Override
		public String toString() {
			return  expression + " " + operator + " "  + "--true-->" + getHight().toString() + "," +
					expression + " " + operator + " "  + "--false-->" + getLow().toString();
					
	
	}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((expression == null) ? 0 : expression.hashCode());
			result = prime * result + ((operator == null) ? 0 : operator.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof SinglePredicate))
				return false;
			SinglePredicate other = (SinglePredicate) obj;
			if (expression == null) {
				if (other.expression != null)
					return false;
			} else if (!expression.equals(other.expression))
				return false;
			if (operator != other.operator)
				return false;
			return true;
		}



	
}