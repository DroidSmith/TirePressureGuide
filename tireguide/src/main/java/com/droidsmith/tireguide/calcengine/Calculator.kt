package com.droidsmith.tireguide.calcengine

import java.util.Objects

/**
 * Calculates the proper tire pressure for a bicycle tire based on weight and width of tire.
 */
class Calculator {

	/**
	 * Uses magical numbers to determine the optimal tire pressure.
	 * @param loadWeight The weight in pounds exerted on the wheel.
	 * @param tireWidth The width of the tire.
	 * @return The magical tire pressure using formula y=mx+b.
	 */
	fun psi(loadWeight: Double, tireWidth: String): Double {
		var m = 0.0
		var b = 0.0
		val psi: Double

		when {
			Objects.equals(tireWidth, "20") -> {
				m = 1.363636364
				b = -11.0
			}
			Objects.equals(tireWidth, "21") -> {
				m = 1.181818182
				b = 0.0
			}
			Objects.equals(tireWidth, "22") -> {
				m = 1.169601449
				b = -6.260144928
			}
			Objects.equals(tireWidth, "23") -> {
				m = 1.136363636
				b = -9.5
			}
			Objects.equals(tireWidth, "24") -> {
				m = 1.053507905
				b = -10.26594203
			}
			Objects.equals(tireWidth, "25") -> {
				m = 1.0
				b = -13.0
			}
			Objects.equals(tireWidth, "26") -> {
				m = 0.9522727273
				b = -12.55
			}
			Objects.equals(tireWidth, "27") -> {
				m = 0.886363636
				b = -8.5
			}
			Objects.equals(tireWidth, "28") -> {
				m = .8022580702
				b = -6.348534297
			}
		// Find the slope of y=mx+b
		}

		psi = loadWeight * m + b // Find the slope of y=mx+b
		return Math.round(psi).toDouble()
	}
}
