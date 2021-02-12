package com.droidsmith.tireguide.extensions

import android.view.View
import android.widget.AdapterView
import android.widget.Spinner

/**
Conveinence method for setting on item selected behavior when we do not care about nothing selected behavior.
 */
fun Spinner.setOnItemSelectedBehavior(funtion: (AdapterView<*>?, View?, Int, Long) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            funtion(parent, view, position, id)
        }
    }
}