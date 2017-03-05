package com.droidsmith.tireguide;

import android.provider.BaseColumns;

/**
 * Profile database record columns.
 */
interface ProfileColumns {
	String PROFILE_NAME = "profile_name";
	String RIDER_TYPE = "rider_type";
	String BODY_WEIGHT = "body_weight";
	String BIKE_WEIGHT = "bike_weight";
	String FRONT_TIRE_WIDTH = "front_tire_width";
	String REAR_TIRE_WIDTH = "rear_tire_width";
	String FRONT_LOAD_PERCENT = "front_load_percent";
	String REAR_LOAD_PERCENT = "rear_load_percent";

	class Profiles implements BaseColumns, ProfileColumns {
		static int _ID = 0;
		static int PROFILE_NAME = 1;
		static int RIDER_TYPE = 2;
		static int BODY_WEIGHT = 3;
		static int BIKE_WEIGHT = 4;
		static int FRONT_TIRE_WIDTH = 5;
		static int REAR_TIRE_WIDTH = 6;
		static int FRONT_LOAD_PERCENT = 7;
		static int REAR_LOAD_PERCENT = 8;
	}
}
