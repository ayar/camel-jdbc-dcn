package com.eudemon.common.expression.bed;


import com.eudemon.common.expression.bed.constant.Contradiction;
import com.eudemon.common.expression.bed.constant.Tautology;
import com.eudemon.common.expression.operation.BinaryOperation;

public class LogicalOperator<E> implements BinaryOperation<BinaryExpression<E>,BinaryExpression<E>>{

					
	
	private final BinaryOperation<BinaryExpression<E>,BinaryExpression<E>> op;
	
	private LogicalOperator(BinaryOperation<BinaryExpression<E>,BinaryExpression<E>> op) {
		this.op = op;
	}


	
	
	static <T> LogicalOperator<T> and() { return new LogicalOperator<T>((a, b) -> { if (a instanceof Contradiction) return a; if (b instanceof Contradiction) return b; return a.compareTo(b)>0?a:b;});}
	static <T> LogicalOperator<T> or() {  return new LogicalOperator<T>((a, b) -> { if (a instanceof Tautology) return a; if (b instanceof Tautology) return b; return a.compareTo(b)>0?a:b;});}


	@Override
	public BinaryExpression<E> evalute( BinaryExpression<E> a, BinaryExpression<E> b) {
		return op.evalute(a, b);
	}



	



}
