package com.gavagai.jussiutil;
import java.io.*;

public class OrdMotOrd  {
	private int df;
	public int getDegreesOfFreedom() {return df;};
	private int i = 3;
	private int j = 3;

	// false = in case size is 2, 2 and 1 are separated
	// true = in case size is 2, 2 and 1 are folded together
	private boolean fold = false;
	private long matris[][];
	private float ideal[][];
	private long kol[];
	private long rad[];
	private long summa;

	public OrdMotOrd(int i0, int j0) {
		this.i = i0;
		this.j = j0;
		this.df = (i-1)*(j-1);
		this.matris = new long[i][j];
		this.setIdeal(new float[i][j]);
		this.kol = new long[i];
		this.rad = new long[j];
	}
	// om vi anv�nder i eller j = 2 istf 3 beh�ver vi
	// inte separera rad resp kol 1 fr�n 2 och d� beh�ver
	// vi allts� inte nT(A) och n1(A): d� r�cker df(A) 
	// som m�tt. dvs om fold=true; annars �r sl�r vi ihop
	// n0 med n1.
	//    private void getMatrix(String A, String B) {
	//	
	//	summa = c.getN();
	//	
	//	rad[2] = c.getNT(A);
	//	rad[1] = c.getN1(A);
	//	rad[0] = summa - rad[0] - rad[1];
	//
	//	kol[2] = c.getNT(B);
	//	kol[1] = c.getN1(B);
	//	kol[0] = summa - kol[0] - kol[1];
	//
	//	matris[2][2] = c.getNTT(A,B);
	//	matris[2][1] = c.getNT1(A,B);
	//	matris[2][0] = rad[2] - matris[2][2] - matris[2][1];
	//
	//	matris[1][2] = c.getN1T(A,B);
	//	matris[1][1] = c.getN11(A,B);
	//	matris[1][0] = rad[1] - matris[1][2] - matris[1][1];
	//
	//	matris[0][2] = kol[2] - matris[2][2] - matris[1][2];
	//	matris[0][1] = kol[1] - matris[2][1] - matris[1][1];
	//	matris[0][0] = rad[0] - matris[0][2] - matris[0][1];
	//
	//    }

//	private void foldMatrix() {
//		if (fold) {
//			if (i == 1) {
//				rad[0] += rad[1];
//				rad[1] = rad[2];
//				matris[0][0] += matris[1][0];
//				matris[0][1] += matris[1][1];
//				matris[0][2] += matris[1][2];
//				matris[1][0] = matris[2][0];
//				matris[1][1] = matris[2][1];
//				matris[1][2] = matris[2][2];
//			}
//			if (j == 1) {
//				kol[0] += kol[1];
//				kol[1] = kol[2];
//				matris[0][0] += matris[0][1];
//				matris[1][0] += matris[1][1];
//				matris[2][0] += matris[2][1];
//				matris[0][1] =  matris[0][2];
//				matris[1][1] =  matris[1][2];
//				matris[2][1] =  matris[2][2];
//			}
//		} else {
//			if (i == 1) {
//				rad[1] += rad[2];
//				matris[1][0] += matris[2][0];
//				matris[1][1] += matris[2][1];
//				matris[1][2] += matris[2][2];
//			}
//			if (j == 1) {
//				kol[1] += kol[2];
//				matris[0][1] +=  matris[0][2];
//				matris[1][1] +=  matris[1][2];
//				matris[2][1] +=  matris[2][2];
//			}
//		}
//	}

	public void checkMatrix() {
		for(int ii= 0; ii < matris.length; ii++) {
			for(int jj= 0; jj < matris[ii].length; jj++) {
				if (matris[ii][jj] < 5) {
					System.err.println("Under fem i khi2-cell!");
				}
			}
		}
	}

	public double khi2() {
		double khi2 = 0;
		for(int ii= 0; ii < i; ii++) {
			for(int jj= 0; jj < j; jj++) {
				getIdeal()[ii][jj] = kol[jj]*rad[ii]/summa;
				double delta = matris[ii][jj] - getIdeal()[ii][jj];
				if (getIdeal()[ii][jj] > 0) khi2 += (delta*delta)/getIdeal()[ii][jj];
			}
		}
		return khi2;
	}

	public void insertMatrix(int a, int b, int c, int d) {
		matris[0][0] = a;
		matris[0][1] = b;
		matris[1][0] = c;
		matris[1][1] = d;
		kol[0] = a + b;
		kol[1] = c + d;
		rad[0] = a + c;
		rad[1] = b + d;
		summa = a + b + c + d;
	}
    //  00 interestingObservationsOfThisCase  01 unInterestingObservationsOfThisCase  r0 allObservationsOfThisCase 
	//  10 interestingObservationsWoutThisCase 11 unInterestingObservationsWoutThisCase r1 allObservationsWoutThisCase
	//  k0 numberOfPotentiallyInterestingCandidates k1 allOtherObservations summa numberOfObservations
	public void inferMatrix(int interestingObservationsOfThisCase, int allObservationsOfThisCase, int numberOfPotentiallyInterestingCandidates, int numberOfObservations) {
		matris[0][0] = interestingObservationsOfThisCase;
		rad[0] = allObservationsOfThisCase;
		kol[0] = numberOfPotentiallyInterestingCandidates;
		summa = numberOfObservations;
		matris[0][1] = allObservationsOfThisCase - interestingObservationsOfThisCase;
		matris[1][0] = numberOfPotentiallyInterestingCandidates - interestingObservationsOfThisCase;
		rad[1] = numberOfObservations - allObservationsOfThisCase;
		matris[1][1] = rad[1] - matris[1][0];
		kol[1] = matris[0][1] + matris[1][1];
	}
	//    public double process(String A, String B) {
	//	getMatrix(A,B);
	//	foldMatrix();
	//	checkMatrix();
	//	double x2 = khi2();
	//	return x2;
	//    }

	public static double evaluateKhi2(double khi2, int df) {
		double r = 0;
		if (df == 4) {
			if (khi2 > 18.5) {r = 99.9;}
			else if (khi2 > 14.9) {r = 99.5;}
			else if (khi2 > 13.3) {r = 99;}
			else if (khi2 > 11.1) {r = 97.5;}
			else if (khi2 > 9.49) {r = 95;}
			else if (khi2 > 7.78) {r = 90;}
			else if (khi2 > 3.36) {r = 50;};
		} else if (df == 3) {
			if (khi2 > 16.3) {r = 99.9;}
			else if (khi2 > 12.8) {r = 99.5;}
			else if (khi2 > 11.3) {r = 99;}
			else if (khi2 > 9.35) {r = 97.5;}
			else if (khi2 > 7.81) {r = 95;}
			else if (khi2 > 6.25) {r = 90;}
			else if (khi2 > 2.37) {r = 50;};
		} else if (df == 2) {
			if (khi2 > 13.8) {r = 99.9;}
			else if (khi2 > 10.6) {r = 99.5;}
			else if (khi2 > 9.21) {r = 99;}
			else if (khi2 > 7.38) {r = 97.5;}
			else if (khi2 > 5.99) {r = 95;}
			else if (khi2 > 4.61) {r = 90;}
			else if (khi2 > 1.39) {r = 50;};
		} else if (df == 1) {
			if (khi2 > 10.8) {r = 99.9;}
			else if (khi2 > 7.88) {r = 99.5;}
			else if (khi2 > 6.63) {r = 99;}
			else if (khi2 > 5.02) {r = 97.5;}
			else if (khi2 > 3.84) {r = 95;}
			else if (khi2 > 2.71) {r = 90;}
			else if (khi2 > 0.46) {r = 50;};
		}
		return r;
	}
	
	public static void main(String[] a) {
		OrdMotOrd oo = new OrdMotOrd(2,2);
		oo.inferMatrix(189,562,45741,150667);
		System.out.println(oo.khi2());
		System.out.println(oo.matris[0][0]+"\t"+oo.matris[0][1]+"\t"+oo.rad[0]);
		System.out.println(oo.matris[1][0]+"\t"+oo.matris[1][1]+"\t"+oo.rad[1]);		
		System.out.println(oo.kol[0]+"\t"+oo.kol[1]+"\t"+oo.summa);
		System.out.println(oo.getIdeal()[0][0]+"\t"+oo.getIdeal()[0][1]+"\t"+oo.rad[0]);
		System.out.println(oo.getIdeal()[1][0]+"\t"+oo.getIdeal()[1][1]+"\t"+oo.rad[1]);		
		System.out.println(oo.kol[0]+"\t"+oo.kol[1]+"\t"+oo.summa);
	}

	public float[][] getIdeal() {
		return ideal;
	}

	public void setIdeal(float ideal[][]) {
		this.ideal = ideal;
	}
}






