package com.droidsmith.tireguide

/**
 * Profile database record columns.
 */
interface ProfileColumns {
    companion object {
        val PROFILE_NAME = "profile_name"
        val RIDER_TYPE = "rider_type"
        val BODY_WEIGHT = "body_weight"
        val BIKE_WEIGHT = "bike_weight"
        val FRONT_TIRE_WIDTH = "front_tire_width"
        val REAR_TIRE_WIDTH = "rear_tire_width"
        val FRONT_LOAD_PERCENT = "front_load_percent"
        val REAR_LOAD_PERCENT = "rear_load_percent"
    }
}
