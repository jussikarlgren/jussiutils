package com.gavagai.jussiutil;

import java.util.Hashtable;

public class HashtableI extends Hashtable<Object,Integer> {
	public int increment(Object o) {
		if (this.containsKey(o)) {
			Integer n = (Integer) get(o);
			put(o,n+1);
			return n+1;
		} else {
			put(o,1);
			return 1;
		}
	}

}
