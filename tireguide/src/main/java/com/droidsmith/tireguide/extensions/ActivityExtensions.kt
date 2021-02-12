package com.droidsmith.tireguide.extensions

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.util.Log

fun Activity.openExternalUrl(url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.e(Intent.ACTION_VIEW, "Intent could not be resolved to open external URL: $url", e)
    }
}

fun Activity?.requireActivity(): Activity =
    this ?: throw IllegalStateException("Activity is null. View may not be attached.")