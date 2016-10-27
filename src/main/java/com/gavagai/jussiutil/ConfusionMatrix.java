package com.gavagai.jussiutil;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class ConfusionMatrix {
	HashMap<Object, HashMap<Object, Integer>> matrix;
	private int items = 0;

	public ConfusionMatrix(Collection<Object> labels) {
		init(labels);
	}

	public ConfusionMatrix(Integer size) {
		matrix = new HashMap<Object, HashMap<Object, Integer>>(size);
	}

	public ConfusionMatrix(Object[] values) {
		matrix = new HashMap<Object, HashMap<Object, Integer>>(values.length);
		HashMap<Object, Integer> init = new HashMap<Object, Integer>(values.length);
		for (Object k : values) {
			init.put(k.toString(), 0);
		}
		for (Object k : values) {
			matrix.put(k.toString(), (HashMap<Object, Integer>) init.clone());
		}
	}

	public void init(Collection<Object> labels) {
		matrix = new HashMap<Object, HashMap<Object, Integer>>(labels.size());
		HashMap<Object, Integer> init = new HashMap<Object, Integer>(labels.size());
		for (Object k : labels) {
			init.put(k, 0);
		}
		for (Object k : labels) {
			matrix.put(k, (HashMap<Object, Integer>) init.clone());
		}
	}
	public String toString() {
		float corr = 0f;
		float uncorr = 0f;
		String s = "\n\t";
		for (Object k : matrix.keySet()) {
			if (k != null && k.toString().length() > 6) { //printf                                    
				s = s + k.toString().substring(0,6) + "\t";
			} else {
				s = s + k + "\t";
			}
		}
		s = s + "\n";
		for (Object k : matrix.keySet()) {
			if (k != null && k.toString().length() > 6) { // printf                                   
				s = s + k.toString().substring(0,6) + "\t";
			} else {
				s = s + k + "\t";
			}
			for (Object l : matrix.keySet()) {
				s = s + confusion(k, l) + "\t";
				if (l != null && l.equals(k)) {
					corr += confusion(k,l);
				} else {
					uncorr += confusion(k,l);
				}
			}
			s = s + "\t|\t";
			s = s + getNumberOfLabeledItems(k);
			s = s + "\n";
		}
		s = s + "\t";
		for (Object k : matrix.keySet()) {
			s = s + "------"+"\t";
		}
		s = s + "\n";
		s = s + "\t";
		for (Object k : matrix.keySet()) {
			s = s + getNumberOfPredictedItems(k)+"\t";
		}
		//            if (uncorr+corr > 0) {
		//                float acc = corr/(corr+uncorr)*100;
		//                s = s + acc+"%\t";
		//            }
		s = s + "\n";
		return s;
	}
	public float getAccuracy() {
		float corr = 0f;
		float uncorr = 0f;
		float acc = 0f;
		for (Object k : matrix.keySet()) {
			for (Object l : matrix.keySet()) {
				if (l != null && l.equals(k)) {
					corr += confusion(k,l);
				} else {
					uncorr += confusion(k,l);
				}
			}
		}
		if (uncorr+corr > 0) {
			acc = corr/(corr+uncorr)*100;
		}
		return acc;
	}

	public float getMacroAveragePrecision() {
		return getMacroAveragePrecision(matrix.keySet());
	}
	public float getMacroAveragePrecision(Set<Object> predictions) {
		float acc = 0f;
		for (Object k : predictions) {
			acc += getPrecision(k);
		}
		acc = predictions.size() > 0?acc/predictions.size():0f;
		return acc;
	}
	public float getMacroAverageRecall() {
		return getMacroAverageRecall(matrix.keySet());
	}
	public float getMacroAverageRecall(Set<Object> labels) {
		float acc = 0f;
		for (Object k : labels) {
			acc += getRecall(k);
		}
		acc = labels.size() > 0?acc/labels.size():0f;
		return acc;
	}
	public float getMicroAveragePrecision() {
		return getMicroAveragePrecision(matrix.keySet());
	}
	public float getMicroAveragePrecision(Set<Object> predictions) {
		float corr = 0f;
		float uncorr = 0f;
		float acc = 0f;
		for (Object prediction : predictions) {
			for (Object label : matrix.keySet()) {
				if (label != null && label.equals(prediction)) {
					corr += confusion(label,prediction);
				} else {
					uncorr += confusion(label,prediction);
				}
			}		
		}
		if (uncorr+corr > 0) {
			acc = corr/(corr+uncorr)*100;
		}
		return acc;
	}
	public float getMicroAverageRecall() {
		return getMicroAverageRecall(matrix.keySet());
	}
	public float getMicroAverageRecall(Set<Object> labels) {
		float corr = 0f;
		float uncorr = 0f;
		float acc = 0f;
		for (Object label : labels) {
			for (Object prediction : matrix.keySet()) {
				if (label != null && label.equals(prediction)) {
					corr += confusion(label,prediction);
				} else {
					uncorr += confusion(label,prediction);
				}
			}		
		}
		if (uncorr+corr > 0) {
			acc = corr/(corr+uncorr)*100;
		}		return acc;
	}
	public float getPrecision(Object prediction) {
		float corr = 0f;
		float uncorr = 0f;
		float acc = 0f;
		for (Object l : matrix.keySet()) {
			if (l != null && l.equals(prediction)) {
				corr += confusion(l,prediction);
			} else {
				uncorr += confusion(l,prediction);
			}
		}		
		if (uncorr+corr > 0) {
			acc = corr/(corr+uncorr)*100;
		}
		return acc;
	}
	public float getRecall(Object label) {
		float corr = 0f;
		float uncorr = 0f;
		float acc = 0f;
		for (Object l : matrix.keySet()) {
			if (l != null && l.equals(label)) {
				corr += confusion(label,l);
			} else {
				uncorr += confusion(label,l);
			}
		}		
		if (uncorr+corr > 0) {
			acc = corr/(corr+uncorr)*100;
		}
		return acc;		
	}

	public int confusion(Object label, Object prediction) {
		try {
			return matrix.get(label).get(prediction);
		} catch (NullPointerException e) {
			return 0;
		}
	}

	public void addIn(ConfusionMatrix c) {
		for (Object ck: c.matrix.keySet()) {
			HashMap<Object,Integer> cm = c.matrix.get(ck);
			for (Object ckm: cm.keySet()) {
				increment(ck,ckm,cm.get(ckm));
			}
		}
	}

	public void increment(Object label, Object prediction) {
		increment( label,  prediction, 1);
	}

	public void increment(Object label, Object prediction, int c) {
		HashMap<Object, Integer> now;
		items += c;
		if (matrix.containsKey(label)) {
			now = matrix.get(label);
		} else {
			now = new HashMap<Object, Integer>();
		}
		if (now.containsKey(prediction)) {
			now.put(prediction, c + now.get(prediction));
		} else {
			now.put(prediction, c);
		}
		matrix.put(label, now);
		if (!matrix.containsKey(prediction)) {
			now = new HashMap<Object, Integer>();
			matrix.put(prediction, now);
		}
	}
	public int getItems() {
		return items;
	}
	public int getNumberOfPredictedItems(Object prediction) {
		int i = 0;		
		for (Object label : matrix.keySet()) {
			if (label != null && prediction != null) {
				i += confusion(label,prediction);
			}
		}		
		return i;
	}
	public int getNumberOfLabeledItems(Object label) {
		int i = 0;		
		for (Object prediction : matrix.keySet()) {
			if (label != null && prediction != null) {
				i += confusion(label,prediction);
			}
		}		
		return i;
	}
}

