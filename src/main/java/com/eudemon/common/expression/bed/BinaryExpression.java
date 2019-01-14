package com.eudemon.common.expression.bed;


public abstract class BinaryExpression<E> implements Comparable<BinaryExpression<E>>  {
	
	public  BinaryExpression<E> low;
	public  BinaryExpression<E> hight;
	
	protected LogicalOperator <E> AND= LogicalOperator.<E>and();
	protected LogicalOperator <E> OR= LogicalOperator.<E>or();
	
	public BinaryExpression<E> getLow() {
		return low;
	}
	public void setLow(BinaryExpression<E> low) {
		this.low = low;
	}
	@Override
	public String toString() {
		return  getRoot().toString() +"--true-->" +getHight().toString() +"\n" + getRoot().toString() +"--false-->" +getLow().toString() ;
		
	}
	public BinaryExpression<E> getHight() {
		return hight;
	}
	public void setHight(BinaryExpression<E> hight) {
		this.hight = hight;
	}
    public abstract BinaryExpression<E> getRoot() ;
	

	public abstract BinaryExpression<E> copy();
	public abstract boolean test(E element);

	public BinaryExpression<E> make(BinaryExpression<E> i, BinaryExpression<E> l, BinaryExpression<E> h) {
		if (l.equals(h)) {
			return l;
		} 
		BinaryExpression<E> ret = i.copy();
		ret.setLow(l);
		ret.setHight(h);
		return ret;
	}
	
	public BinaryExpression<E> apply(LogicalOperator<E> operation,BinaryExpression<E> a,BinaryExpression<E> b){
		if( (a instanceof ConstantExpression) || (b instanceof ConstantExpression)) {
			BinaryExpression<E> ret=  operation.evalute(a ,b);
			return ret;
		}
		else if ( a.getRoot().equals(b.getRoot())){
			BinaryExpression<E> ret= make(a,apply(operation, a.getLow(), b.getLow()),apply(operation, a.getHight(), b.getHight()));
		return ret;
		}else {
			BinaryExpression<E> top= a.compareTo(b)>0 ?a:b;
			BinaryExpression<E> litle =a.compareTo(b)>0 ?b:a;
			BinaryExpression<E> ret= make(top,apply(operation,litle,top.getLow()),apply(operation,litle,top.getHight()));
			return ret;
		}
	}
	
	public BinaryExpression<E> not(){
		BinaryExpression<E>  a= this.copy();
		a.setHight(getLow());
		a.setLow(getHight());
		return a;
	}
	
	public  BinaryExpression<E> and(BinaryExpression<E> b){
		return apply(AND, this.copy(), b);
	}
	public  BinaryExpression<E> or(BinaryExpression<E> b){
		return apply(OR, this.copy(), b);
	}

}
