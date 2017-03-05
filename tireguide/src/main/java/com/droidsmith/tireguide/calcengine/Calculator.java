package com.droidsmith.tireguide.calcengine;

import android.text.TextUtils;

/**
 * Created by steve on 12/28/15. Calculates the proper tire pressure for a bicycle tire based on
 * weight and width of tire.
 */
public class Calculator {
	private double loadWeight;
	private String tireWidth;

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

		if (TextUtils.equals("20", tireWidth)) {
			m = 1.363636364f;
			b = -11f;
		} else if (TextUtils.equals("21", tireWidth)) {
			m = 1.181818182f;
			b = 0f;
		} else if (TextUtils.equals("22", tireWidth)) {
			m = 1.169601449f;
			b = -6.260144928f;
		} else if (TextUtils.equals("23", tireWidth)) {
			m = 1.136363636f;
			b = -9.5f;
		} else if (TextUtils.equals("24", tireWidth)) {
			m = 1.053507905f;
			b = -10.26594203f;
		} else if (TextUtils.equals("25", tireWidth)) {
			m = 1f;
			b = -13f;
		} else if (TextUtils.equals("26", tireWidth)) {
			m = 0.9522727273f;
			b = -12.55f;
		} else if (TextUtils.equals("27", tireWidth)) {
			m = 0.886363636f;
			b = -8.5f;
		} else if (TextUtils.equals("28", tireWidth)) {
			m = .8022580702f;
			b = -6.348534297f;
		}

		psi = loadWeight * m + b;
		return psi;
	}
}
