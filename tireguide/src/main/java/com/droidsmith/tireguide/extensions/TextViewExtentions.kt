package com.droidsmith.tireguide.extensions

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView

fun TextView.addOnTextChangedBehavior(function: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            function(s.toString())
        }
    })
}