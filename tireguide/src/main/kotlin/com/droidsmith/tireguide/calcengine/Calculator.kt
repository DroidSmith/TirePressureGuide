package com.droidsmith.tireguide.calcengine

import android.text.TextUtils

/**
 * Calculates the proper tire pressure for a bicycle tire based on weight and width of tire.
 */
class Calculator(private val loadWeight: Double, private val tireWidth: String) {

    /**
     * Uses magical numbers to determine the optimal tire pressure;
     * @return The magical tire pressure;
     */
    fun psi(): Double {
        var m = 0.0
        var b = 0.0
        val psi: Double

        if (TextUtils.equals("20", tireWidth)) {
            m = 1.363636364
            b = -11.0
        } else if (TextUtils.equals("21", tireWidth)) {
            m = 1.181818182
            b = 0.0
        } else if (TextUtils.equals("22", tireWidth)) {
            m = 1.169601449
            b = -6.260144928
        } else if (TextUtils.equals("23", tireWidth)) {
            m = 1.136363636
            b = -9.5
        } else if (TextUtils.equals("24", tireWidth)) {
            m = 1.053507905
            b = -10.26594203
        } else if (TextUtils.equals("25", tireWidth)) {
            m = 1.0
            b = -13.0
        } else if (TextUtils.equals("26", tireWidth)) {
            m = 0.9522727273
            b = -12.55
        } else if (TextUtils.equals("27", tireWidth)) {
            m = 0.886363636
            b = -8.5
        } else if (TextUtils.equals("28", tireWidth)) {
            m = .8022580702
            b = -6.348534297
        }

        psi = loadWeight * m + b
        return psi
    }
}
