package com.droidsmith.tireguide.calcengine;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class CalculatorTest {

	private final double loadWeight;
	private final String tireWidth;
	private final double psi;
	private Calculator calculator;

	public CalculatorTest(double loadWeight, String tireWidth, double psi) {
		this.loadWeight = loadWeight;
		this.tireWidth = tireWidth;
		this.psi = psi;
	}

	@Parameterized.Parameters(name = "{index}: Test with loadWeight={0}, tireWidth={1}, result: {2}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {{0.0, "21", 0.0},
											 {91.8, "26", 75},
											 {122.4, "26", 104},
											 {77.4, "26", 61},
											 {103.2, "26", 86},
											 {63.9, "26", 48},
											 {85.2, "26", 69},
											 {105.3, "26", 88},
											 {140.4, "26", 121},
											 {84.15, "25", 71},
											 {112.2, "25", 99},
											 {102.6, "28", 76},
											 {136.9, "28", 103},});
	}

	@Before
	public void setUP() {
		calculator = new Calculator();
	}

	@Test
	public void psiWithZeroValuesReturnsZero() {
		assertTrue(arePsiEqual(psi, calculator.psi(loadWeight, tireWidth)));
	}

	private boolean arePsiEqual(double expectedPsi, double testPsi) {
		return Math.round(expectedPsi) == testPsi;
	}
}
