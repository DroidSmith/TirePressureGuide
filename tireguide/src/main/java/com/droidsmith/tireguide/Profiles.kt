package com.droidsmith.tireguide

import android.provider.BaseColumns

class Profiles : BaseColumns, ProfileColumns {
    companion object {
        @JvmField
        var _ID = 0

        @JvmField
        var PROFILE_NAME = 1

        @JvmField
        var RIDER_TYPE = 2

        @JvmField
        var BODY_WEIGHT = 3

        @JvmField
        var BIKE_WEIGHT = 4

        @JvmField
        var FRONT_TIRE_WIDTH = 5

        @JvmField
        var REAR_TIRE_WIDTH = 6

        @JvmField
        var FRONT_LOAD_PERCENT = 7

        @JvmField
        var REAR_LOAD_PERCENT = 8
    }
}
