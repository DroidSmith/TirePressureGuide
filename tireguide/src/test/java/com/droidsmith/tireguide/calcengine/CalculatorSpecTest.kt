package com.droidsmith.tireguide.calcengine

import io.kotlintest.matchers.plusOrMinus
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import io.kotlintest.properties.headers
import io.kotlintest.properties.row
import io.kotlintest.properties.table
import io.kotlintest.specs.ShouldSpec

class CalculatorSpecTest : ShouldSpec() {
	init {
		val calculator = Calculator()
		"supported tire widths" {
			should("return correct psi for 20mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "20", -11),
									 row(8.0, "20", 0),
									 row(50.0, "20", 57),
									 row(100.0, "20", 125),
									 row(63.9, "20", 76),
									 row(77.4, "20", 95),
									 row(84.15, "20", 104),
									 row(85.2, "20", 105),
									 row(91.8, "20", 114),
									 row(102.6, "20", 129),
									 row(103.2, "20", 130),
									 row(105.3, "20", 133),
									 row(112.2, "20", 142),
									 row(122.4, "20", 156),
									 row(136.9, "20", 176),
									 row(140.4, "20", 180))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 21mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "21", 0),
									 row(50.0, "21", 59),
									 row(100.0, "21", 118),
									 row(63.9, "21", 76),
									 row(77.4, "21", 91),
									 row(84.15, "21", 99),
									 row(85.2, "21", 101),
									 row(91.8, "21", 108),
									 row(102.6, "21", 121),
									 row(103.2, "21", 122),
									 row(105.3, "21", 124),
									 row(112.2, "21", 133),
									 row(122.4, "21", 145),
									 row(136.9, "21", 162),
									 row(140.4, "21", 166))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 22mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "22", -6),
									 row(5.0, "22", 0),
									 row(50.0, "22", 52),
									 row(100.0, "22", 111),
									 row(63.9, "22", 68),
									 row(77.4, "22", 84),
									 row(84.15, "22", 92),
									 row(85.2, "22", 93),
									 row(91.8, "22", 101),
									 row(102.6, "22", 114),
									 row(103.2, "22", 114),
									 row(105.3, "22", 117),
									 row(112.2, "22", 125),
									 row(122.4, "22", 137),
									 row(136.9, "22", 154),
									 row(140.4, "22", 158))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 23mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "23", -9),
									 row(8.0, "23", 0),
									 row(50.0, "23", 47),
									 row(100.0, "23", 104),
									 row(63.9, "23", 63),
									 row(77.4, "23", 78),
									 row(84.15, "23", 86),
									 row(85.2, "23", 87),
									 row(91.8, "23", 95),
									 row(102.6, "23", 107),
									 row(103.2, "23", 108),
									 row(105.3, "23", 110),
									 row(112.2, "23", 118),
									 row(122.4, "23", 130),
									 row(136.9, "23", 146),
									 row(140.4, "23", 150))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 24mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "24", -10),
									 row(10.0, "24", 0),
									 row(50.0, "24", 42),
									 row(100.0, "24", 95),
									 row(63.9, "24", 57),
									 row(77.4, "24", 71),
									 row(84.15, "24", 78),
									 row(85.2, "24", 79),
									 row(91.8, "24", 86),
									 row(102.6, "24", 98),
									 row(103.2, "24", 98),
									 row(105.3, "24", 101),
									 row(112.2, "24", 108),
									 row(122.4, "24", 119),
									 row(136.9, "24", 134),
									 row(140.4, "24", 138))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 25mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "25", -13),
									 row(13.0, "25", 0),
									 row(50.0, "25", 37),
									 row(100.0, "25", 87),
									 row(63.9, "25", 51),
									 row(77.4, "25", 64),
									 row(84.15, "25", 71),
									 row(85.2, "25", 72),
									 row(91.8, "25", 79),
									 row(102.6, "25", 90),
									 row(103.2, "25", 90),
									 row(105.3, "25", 92),
									 row(112.2, "25", 99),
									 row(122.4, "25", 109),
									 row(136.9, "25", 124),
									 row(140.4, "25", 127))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 26mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "26", -13),
									 row(13.0, "26", 0),
									 row(50.0, "26", 35),
									 row(100.0, "26", 83),
									 row(63.9, "26", 48),
									 row(77.4, "26", 61),
									 row(84.15, "26", 68),
									 row(85.2, "26", 69),
									 row(91.8, "26", 75),
									 row(102.6, "26", 85),
									 row(103.2, "26", 86),
									 row(105.3, "26", 88),
									 row(112.2, "26", 94),
									 row(122.4, "26", 104),
									 row(136.9, "26", 118),
									 row(140.4, "26", 121))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 27mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "27", -8),
									 row(10.0, "27", 0),
									 row(50.0, "27", 36),
									 row(100.0, "27", 80),
									 row(63.9, "27", 48),
									 row(77.4, "27", 60),
									 row(84.15, "27", 66),
									 row(85.2, "27", 67),
									 row(91.8, "27", 73),
									 row(102.6, "27", 82),
									 row(103.2, "27", 83),
									 row(105.3, "27", 85),
									 row(112.2, "27", 91),
									 row(122.4, "27", 100),
									 row(136.9, "27", 113),
									 row(140.4, "27", 116))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}

			should("return correct psi for 28mm tire") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "28", -6),
									 row(8.0, "28", 0),
									 row(50.0, "28", 34),
									 row(100.0, "28", 74),
									 row(63.9, "28", 45),
									 row(77.4, "28", 56),
									 row(84.15, "28", 61),
									 row(85.2, "28", 62),
									 row(91.8, "28", 67),
									 row(102.6, "28", 76),
									 row(103.2, "28", 76),
									 row(105.3, "28", 78),
									 row(112.2, "28", 84),
									 row(122.4, "28", 92),
									 row(136.9, "28", 103),
									 row(140.4, "28", 106))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}
		}
		"unsupported tire widths" {
			should("retun 0 psi") {
				val psiTable = table(headers("loadWeight", " tireWidth", "psi"),
									 row(0.0, "", 0),
									 row(50.0, "", 0),
									 row(100.0, "", 0),
									 row(0.0, "29", 0),
									 row(50.0, "29", 0),
									 row(100.0, "29", 0))
				forAll(psiTable) { loadWeight, tireWidth, psi ->
					calculator.psi(loadWeight,
								   tireWidth) shouldBe (Math.round(psi.toDouble()).toDouble() plusOrMinus 0.5)
				}
			}
		}
	}
}
