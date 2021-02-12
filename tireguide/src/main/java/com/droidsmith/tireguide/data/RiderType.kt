package com.droidsmith.tireguide.data

import android.content.Context
import androidx.annotation.StringRes
import com.droidsmith.tireguide.R

enum class RiderType constructor(@StringRes val displayName: Int, val serviceName: String) {
    CASUAL(R.string.type_casual, "Casual"),
    SPORT(R.string.type_sport, "Sport"),
    RACER(R.string.type_racer, "Racer");

    companion object {
        fun getTypeFromDisplayName(context: Context, displayName: String?): RiderType? =
            values().firstOrNull() { context.getString(it.displayName).equals(displayName, ignoreCase = true) }

        fun getTypeFromServiceName(serviceName: String?): RiderType? =
            values().firstOrNull() { it.serviceName.equals(serviceName, ignoreCase = true) }

        fun getRiderTypesAsStrings(context: Context): Array<String> =
            values().map { context.getString(it.displayName) }.toTypedArray()
    }
}