package com.droidsmith.tireguide.extensions

import android.widget.EditText

fun EditText.getTextOrEmpty(): String = text?.toString().orEmpty()