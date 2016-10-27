package com.gavagai.jussiutil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * For collecting data and performing a 2xN Chi Square Test
 * @author jimmy
 *
 */
public class PearsonsChiSquareTest {
	
	Map<String, Double> expected = new HashMap<String, Double>();
	Map<String, Double> observed = new HashMap<String, Double>();
	
	public PearsonsChiSquareTest() {
		
	}
	
	
	public void setExpected(String column, double value) {
		expected.put(column, value);
	}
	
	public Set<String> getColumns() {
		return observed.keySet();
	}
	
	public double getExpected(String column) {
		return expected.get(column) == null ? 0 : expected.get(column);
	}
	
	public void setObserved(String column, double value) {
		observed.put(column, value);
	}
	
	public double getObserved(String column) {
		return observed.get(column) == null ? 0 : observed.get(column);
	}
	
	public double getTestStatistic() {
		if (!expected.keySet().equals(observed.keySet())) {
			throw new RuntimeException("Expected and observed columns differ in content \n Observed: " + observed.keySet() + "\n Expected: " + expected.keySet());
		}
	
		double statistic = 0;
		for (String column : getColumns()) {
			statistic += Math.pow(getObserved(column) - getExpected(column), 2)/getExpected(column);
		}
		
		return statistic;
	}
}
