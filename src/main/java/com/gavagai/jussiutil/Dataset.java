package com.gavagai.jussiutil;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Dataset<T> extends Vector<T> {
	private static final long serialVersionUID = 1L; // default

	static private int numberOfHeaders = 2;

	private Dataset<T> testset;
	private Dataset<T> trainingset;
	private int numberOfFeatures;
	private String[] featureNames;

	private Log logger = LogFactory.getLog(Dataset.class);

	public Dataset(String datafile) {
		readFile(datafile);
	}

	public Dataset() {
	}

	public class Individual {
		private String id;
		private boolean target;
		private double[] features;
		private int targets = 0;

		Individual(double[] nf) {
			this("noname", nf, false);
		}
		public Individual(String n,double[] nf,boolean t) {
			this.id = n;
			this.target = t;
			this.features = nf;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public boolean isTarget() {
			return target;
		}
		public void setTarget(boolean target) {
			this.target = target;
		}
		public double[] getFeatures() {
			return features;
		}
		public void setFeatures(double[] features) {
			this.features = features;
		}
		public double getFeature(int j) {
			return features[j];
		}
	}
	/* 
	 * readFile() reads a file on format: 	lineno \t id \t (key:val)+
	 */
	public void readFile(String fileName) {
		try {
			File dataFile = new File(fileName);
			Scanner fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(dataFile)));
			while (fileScanner.hasNextLine()) {
				String fileLine = fileScanner.nextLine();
				String bits[] = fileLine.split("\t");
				numberOfFeatures = bits.length - numberOfHeaders;
				featureNames = new String[numberOfFeatures];
				if (bits.length >= numberOfFeatures+numberOfHeaders) {
					double[] featureArray = new double[numberOfFeatures];
					double pos = 0;
					double neg = 0;
					for (int i = 0; i < numberOfFeatures; i++) {
						String features = bits[i+2];
						String[] feat = features.split(":"); //jsonparser deluxe
						featureArray[i] = Double.parseDouble(feat[1]);
						featureNames[i] = feat[0];
						if (feat[0].equalsIgnoreCase("negative")) {
							neg = Double.parseDouble(feat[1]);
							featureArray[i] = 0.0;
						}
						if (feat[0].equalsIgnoreCase("positive")) {
							pos = Double.parseDouble(feat[1]);
							featureArray[i] = 0.0;
						}					
					}
					Individual i = new Individual(featureArray);
					i.setId(bits[1]);
					if (neg < 0.7*pos) {i.setTarget(true);} else {i.setTarget(false);}
					add((T) i);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void partition(double portion) {
		trainingset = new Dataset();
		trainingset.addAll(this);
		trainingset.setNumberOfFeatures(numberOfFeatures);
		testset = new Dataset();
		testset.setLegend(featureNames);
		testset.setNumberOfFeatures(numberOfFeatures);
		trainingset.setLegend(featureNames);
		long testsetsize = Math.round(this.size()*portion);
		while (testset.size() < testsetsize) { 
			testset.add(trainingset.drawRandomIndividual());
		}
	}

	public T drawRandomIndividual() {
		return remove((int) Math.floor(Math.random()*size()));
	}

	public T showRandomIndividual() {
		return get((int) Math.floor(Math.random()*size())); 
	}

	public static int getNumberOfHeaders() {
		return numberOfHeaders;
	}

	public static void setNumberOfHeaders(int numberOfHeaders) {
		Dataset.numberOfHeaders = numberOfHeaders;
	}

	public Dataset<T> getTestset() {
		return testset;
	}

	public Dataset<T> getTrainingset() {
		return trainingset;
	}

	public int getNumberOfFeatures() {
		return numberOfFeatures;
	}

	public void setNumberOfFeatures(int numberOfFeatures) {
		this.numberOfFeatures = numberOfFeatures;
	}

	public String[] getLegend() {
		return featureNames;
	}

	public void setLegend(String[] legend) {
		this.featureNames = legend;
	}

	public int getNumberOfTargets() {
		int n = 0;
		for (int i = 0; i < size(); i++) {
			if (((Individual)elementAt(i)).isTarget()) {n++;}
		}
		return n;
		}
}
