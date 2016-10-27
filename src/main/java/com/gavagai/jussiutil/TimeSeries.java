package com.gavagai.jussiutil;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TimeSeries {
	private Log logger = LogFactory
			.getLog(TimeSeries.class);

	/**  	Vector<Float> items;

        public TimeSeries(Integer size) {
                items = new Vector<Float>(size);
        }

        public void init(Collection<Float> fs) {
                items = new Vector<Float>(fs.size());
        }

        public static float variance(float[] centroid, Collection<float[]> s) {
		float averageDistanceFromCentroid = 0.0f;
		if (s.size() > 0) {
			for (float[] vec: s) {
				double l = VectorMath.distance(vec,centroid);
				averageDistanceFromCentroid += l*l;
			}
		}			
		return averageDistanceFromCentroid;
	}
	public static float sdev(float[] centroid, Collection<float[]> s) {		
		return (float) Math.sqrt(variance(centroid,s));
	}

	/**
	 * Returns the cosine similarity of the two arrays.
	 *
	 *
	public static float cosineSimilarity(float[] a, float[] b) {

		double dotProduct = 0;
		double aMagnitude = 0;
		double bMagnitude = 0;
		for (int i = 0; i < b.length; i++) {
			double aValue = (double) a[i];
			double bValue = (double) b[i];
			aMagnitude += ( aValue * aValue);
			bMagnitude += ( bValue * bValue);
			dotProduct += ( aValue * bValue);
		}

		double aMagnitudeSqRt = Math.sqrt(aMagnitude);
		double bMagnitudeSqRt = Math.sqrt(bMagnitude);
		return (float) ((aMagnitudeSqRt == 0 || bMagnitudeSqRt == 0) ? 0 : dotProduct
				/ (aMagnitudeSqRt * bMagnitudeSqRt));

	}

	public static float distance(float[] a, float[] b) {
		return 1-Math.abs(cosineSimilarity(a,b));
	}

	/**
	 * Returns the shortest distance between the two points.
	 *
	 *
	public static float distanceSq(float[] a, float[] b) {
		double sumDiff = 0;
		for (int i = 0; i < b.length; i++) {
			sumDiff += (a[i]-b[i]) * (a[i]-b[i]);
		}
		return (float) Math.sqrt(sumDiff);
	}
	 **/

	public static float sum(float[] fs) {
		float s = 0f;
		for (float f:fs) {s += f;}
		return s;
	}
	public static float average(float[] fs) {
		float s = sum(fs);
		return s/fs.length;
	}
	public static float sqerr(float[] fs) {
		float s = average(fs);
		float sqerr = 0f;
		for (float f:fs) {sqerr += (f-s)*(f-s);}
		return sqerr;
	}

	public static float autoCorrelation(float[] fs, int d) {
		float c = 0f;
		float s = 0f;
		float a = average(fs);
		for (int i = d; i < fs.length; i++) {
			float fd = (fs[i]-a);
			s += fd*(fs[i-d]);
		}
		c = s/sqerr(fs);
		return c;
	}
	public static float[] allAutoCorrelations(float[] fs) {
		float[] ss = new float[fs.length];
		for (int d = 1; d < fs.length; d++) {
			ss[d] = autoCorrelation(fs,d);
		}
		return ss;
	}


	public static float pearson(float[] fs1, float[] fs2){
		float result = 0;
		float sum_sq_x = 0;
		float sum_sq_y = 0;
		float sum_coproduct = 0;
		float mean_x = fs1[0];
		float mean_y = fs2[0];
		for (int i=1; i<fs1.length; i++) {
			float sweep = Float.valueOf(i)/(i+1);
			float delta_x = fs1[i]-mean_x;
			float delta_y = fs2[i]-mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / (i + 1);
			mean_y += delta_y / (i + 1);
		}
		float pop_sd_x = (float) Math.sqrt(sum_sq_x/fs1.length);
		float pop_sd_y = (float) Math.sqrt(sum_sq_y/fs1.length);
		float cov_x_y = sum_coproduct / fs1.length;
		result = cov_x_y / (pop_sd_x*pop_sd_y);
		return result;
	}


	public static float spearman(int[] a, int[] b) {
		float d2 = 0;
		if (a.length != b.length) {throw new IndexOutOfBoundsException("Non-equal length of arrays under comparison.");} else {
			for (int i = 0; i < a.length; i++) {
				d2 += (a[i]-b[i])*(a[i]-b[i]);
			}
			float rs = 1-6*d2/(a.length*(a.length*a.length-1));
			//  if (abs($rs)*sqrt($n) > 1.6449) {$s = "*";} else {$s = " "};
			return rs;
		}		
	}
	private static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator =  new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k1).compareTo(map.get(k2));
				if (compare == 0) return 1;
				else return compare;
			}
		};
		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	public static int[] ranks(float[] fs) {
		Map<Integer,Float> map = new TreeMap<Integer,Float>();
		for (int i = 0; i < fs.length; i++) {
			map.put(i, fs[i]);
		}
		Map<Integer,Float> nm = sortByValues(map);
		int[] a1 = new int[nm.keySet().size()]; int a1i = 0;
		for (Integer one: nm.keySet()) {
			a1[a1i] = (int)one;
			a1i++;
		}
		return a1;
	}

	public static void main(String[] args) {
		Properties environment = new Properties();
		String configfilename;
		//		if (args.length < 1) {
		//			configfilename = "timeseries.config";
		//		} else {
		//			configfilename = args[0];
		//		}
		//		try {	
		//			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		//			environment.load(classLoader.getResourceAsStream(configfilename));
		//		} catch (FileNotFoundException e1) {
		//			System.err.println(e1+": "+configfilename);
		//		} catch (IOException e1) {
		//			System.err.println(e1+": "+configfilename);
		//		} catch (NullPointerException e1) {
		//			System.err.println(e1+": "+configfilename);
		//		}
		TimeSeries ts = new TimeSeries();
		int[] indices = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
		HashMap<Integer,Vector<Float>> catchemall = new HashMap<Integer,Vector<Float>>();
		for (int i:indices) {
			catchemall.put(i, new Vector<Float>());
		}
		int individuals = 0;
		try {
			String sourcefilename = "/home/jussi/rahapara/2014.opinion/s.csv"; //sourceDirectory+"/"+environment.getProperty("sourcefilename","/bigdata/research/replab/replab-data/replab2012-labeled-with-language.txt");
			String separator = environment.getProperty("fieldseparator","\t");
			Scanner fileScanner;
			fileScanner = new Scanner(new BufferedInputStream(new FileInputStream(sourcefilename)));
			//			FileWriter outfile = new FileWriter(outfilename);
			while (fileScanner.hasNextLine()) {
				individuals++;
				String fileLine = fileScanner.nextLine();
				String[] bits = fileLine.split(",");
				for (int field:indices) {
					try {
						catchemall.get(field).add(Float.parseFloat(bits[field]));
					} catch (NumberFormatException e) {
						catchemall.get(field).add(0f);
					} catch (ArrayIndexOutOfBoundsException e) {
						catchemall.get(field).add(0f);
					}
				}
			}	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i:indices) {
			float[] fs = new float[individuals];
			int j = 0;
			for (Float f:catchemall.get(i)) {fs[j] = f.floatValue(); j++;}
			float[] a = allAutoCorrelations(fs);
			for (int d = 0; d < a.length; d++) {
				if (a[d] > 0.5) {System.out.println(i+" "+d+" "+a[d]);}
			}
		}
		for (int i:indices) {
			float[] fsi = new float[individuals];
			int ii = 0;
			for (Float f:catchemall.get(i)) {fsi[ii] = f.floatValue(); ii++;}
			int[] ai = ranks(fsi);
			System.out.print(i+": ");
			for (int j:indices) {
				int jj = 0;
				float[] fsj = new float[individuals];
				for (Float f:catchemall.get(j)) {fsj[jj] = f.floatValue(); jj++;}
				int[] aj = ranks(fsj);
				float x = spearman(ai,aj);
				if (x > 0.5) {System.out.print(x+"\t");} else {System.out.print(".\t");}
			}
			System.out.println();
		}
		for (int i:indices) {
			float[] fsi = new float[individuals];
			int ii = 0;
			for (Float f:catchemall.get(i)) {fsi[ii] = f.floatValue(); ii++;}
			System.out.print(i+": ");
			for (int j:indices) {
				int jj = 0;
				float[] fsj = new float[individuals];
				for (Float f:catchemall.get(j)) {fsj[jj] = f.floatValue(); jj++;}
				float x = pearson(fsi,fsj);
				if (x > 0.7) {System.out.print(x+"\t");} else {System.out.print(".\t");}
			}
			System.out.println();
		}

		//		TreeMap<Integer,Float> map = new TreeMap<Integer,Float>();
		//		map.put(1, 9.81f);
		//		map.put(2, 3.14f);
		//		map.put(3, 2.88f);
		//		map.put(4,3.14f);
		//		map.put(5, 2.88f);
		//		
		//		Map<Integer,Float> nm = sortByValues(map);
		//		System.out.println(nm.keySet());
		//		System.out.println(map.keySet());
		//		int[] a1 = new int[nm.keySet().size()]; int a1i = 0;
		//		for (Integer one: nm.keySet()) {
		//			a1[a1i] = (int)one;
		//			a1i++;
		//		}
		//		int[] a2 = new int[map.keySet().size()]; int a2i = 0;
		//		for (Integer two: map.keySet()) {
		//			a2[a2i] = (int)two;
		//			a2i++;
		//		}
		//		System.out.println(spearman(a1,a2));



	}



	//http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java

}

/**

foreach $column (@columns) {
    $this_rank = 0;
    # $individual will in turn contain the index of a cell to be studied in $column.
    # each cycle, it will be put in $previous_index and the cell contents in $previous_score
    foreach $individual (sort {$score{$column,$a} <=> $score{$column,$b}} keys %individuals) {
        # Are the values equal? 
        # YES: Stack the individual in @stack of $duplicates and wait for a new value to show up.
        #      Well, except if we are at the first element, of course. 
	if ($this_rank > 0 && $score{$column,$individual} == $score{$column,$previous}) {
	    print STDERR "$column $individual	->	st	$score{$column,$individual} $score{$column,$previous}  d $duplicates EQ!\n" if $debug;
	    $stack[$duplicates] = $previous;
	    $duplicates++;
        # Are the values equal? 
	# NO: Pop the stack, and put the previous item into %rank.
	} else {
            # unroll the stack if necessary
	    if ($duplicates) { 
		$stack[$duplicates] = $previous;
		$duplicates++;
		# then calculate the average for the stack. 
		$rank_average = $this_rank - 1 - $#stack/2;
		# foreach stacked duplicate value, put in the average rank.
		$d = $duplicates;
		foreach $double (@stack) {
		    print STDERR "$column $double	->	$rank_average	$score{$column,$double} UNSTACK\n" if $debug;
		    $rank{$column,$double} = $rank_average;
		};
		# empty the stack.
		$duplicates = 0;
		@stack = ();
	    } else {
		# if no stack, put the previous rank into %rank
		print STDERR "$column $previous	->	$previous_rank	$score{$column,$previous} INPUT!\n" if $debug;
		$rank{$column,$previous} = $previous_rank;
	    };
	};
	# Put the current position and rank into memory for next iteration.
	$previous = $individual;
	$previous_rank = $this_rank;
	$this_rank++;	
    };

    # after the last element, are there any left on stack? (was the last element a duplicate?)
    if ($duplicates) {
	$stack[$duplicates] = $previous;
	$duplicates++;
	# then calculate the average for the stack. 
	$rank_average = $this_rank - 1 - $#stack/2;
	# foreach stacked duplicate value, put in the average rank.
	$d = $duplicates;
	foreach $double (@stack) {
	    print STDERR "$column $double	->	$rank_average	$score{$column,$double} UNSTACK\n" if $debug;
	    $rank{$column,$double} = $rank_average;
	};
	# empty the stack.
	$duplicates = 0;
	@stack = ();
    } else {
	# if no stack, put the previous rank into %rank
	print STDERR "$column $previous	->	$previous_rank	$score{$column,$previous} INPUT!\n" if $debug;
	$rank{$column,$previous} = $previous_rank;
    };
};

 */

