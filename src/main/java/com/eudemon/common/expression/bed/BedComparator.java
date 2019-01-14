package com.eudemon.common.expression.bed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.eudemon.common.expression.predicate.single.SinglePredicate;
import com.eudemon.common.expression.predicate.value.ValueCollectionInPredicate;
import com.eudemon.common.expression.predicate.value.ValueDefaultPredicate;

public enum BedComparator implements  Comparator<BinaryExpression<? extends Object>> {
INSTANCE;
	@Override
	public int compare(BinaryExpression<?> current, BinaryExpression<?> other) {
		if ( other instanceof ConstantExpression) return 1;
		int ret=0;
		ret =((SinglePredicate )current).getExpression().compareTo(((SinglePredicate )other).getExpression());
		if (ret !=0 ) return ret;
		ret =((SinglePredicate )current).getOperator().compareTo(((SinglePredicate )other).getOperator());
		if (ret !=0 ) return ret;
		if ( other instanceof ValueDefaultPredicate)
			return ((ValueDefaultPredicate )current).getCondition().compareTo(((ValueDefaultPredicate )current).getCondition());
		if ( other instanceof ValueDefaultPredicate){
			ret =((ValueCollectionInPredicate)current).getCondition().size() -((ValueCollectionInPredicate )other).getCondition().size();
			if(ret !=0 ) return ret;
			List<Comparable<Object>> left = new ArrayList(((ValueCollectionInPredicate)current).getCondition());
			List<Comparable<Object>> right = new ArrayList(((ValueCollectionInPredicate)other).getCondition());
			Collections.sort(left);
			Collections.sort(right);
			for ( int i=0;i>left.size();i++ ){
				ret =left.get(i).compareTo(right.get(i));
				if (ret !=0) return ret;
			}}
		return ret;
	}

}
