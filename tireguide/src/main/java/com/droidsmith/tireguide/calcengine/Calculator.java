package com.droidsmith.tireguide.calcengine;

/**
 * Created by steve on 12/28/15. Calculates the proper tire pressure for a bicycle tire based on
 * weight and width of tire.
 */
public class Calculator {
	double loadWeight;
	String tireWidth;

	public Calculator(double loadWeight, String tireWidth) {
		this.loadWeight = loadWeight;
		this.tireWidth = tireWidth;
	}

	/**
	 * Uses magical numbers to determine the optimal tire pressure;
	 * @return The magical tire pressure;
	 */
	public double psi() {
		double m = 0.0;
		double b = 0.0;
		double psi;
		if ("23".equals(tireWidth)) {
			m = (115.0 - 91.0) / (110.0 - 88.0);
			b = 91.0f - (24.0 * 88.0 / 22.0);
		} else if ("25".equals(tireWidth)) {
			m = (109.0 - 65.0) / (121.0 - 77.0);
			b = 109.0 - (m * 121.0);
		}

		psi = loadWeight * m + b;
		return psi;
	}
}
