package com.droidsmith.tireguide;

import android.provider.BaseColumns;

/**
 * Profile database record columns.
 */
public interface ProfileColumns {
	String PROFILE_NAME = "profile_name";
	String RIDER_TYPE = "rider_type";
	String BODY_WEIGHT = "body_weight";
	String BIKE_WEIGHT = "bike_weight";
	String FRONT_TIRE_WIDTH = "front_tire_width";
	String REAR_TIRE_WIDTH = "rear_tire_width";
	String FRONT_LOAD_PERCENT = "front_load_percent";
	String REAR_LOAD_PERCENT = "rear_load_percent";

	public static class Profiles implements BaseColumns, ProfileColumns {
		public static int _ID = 0;
		public static int PROFILE_NAME = 1;
		public static int RIDER_TYPE = 2;
		public static int BODY_WEIGHT = 3;
		public static int BIKE_WEIGHT = 4;
		public static int FRONT_TIRE_WIDTH = 5;
		public static int REAR_TIRE_WIDTH = 6;
		public static int FRONT_LOAD_PERCENT = 7;
		public static int REAR_LOAD_PERCENT = 8;
	}
}
