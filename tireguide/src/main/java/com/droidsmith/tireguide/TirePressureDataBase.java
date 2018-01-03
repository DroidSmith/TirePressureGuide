package com.droidsmith.tireguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import static com.droidsmith.tireguide.Constants.DATABASE_NAME;

/**
 * Defines a Database to store bike profiles.
 */
class TirePressureDataBase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;

	TirePressureDataBase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * Adds a profile to the profiles table.
	 * @param profileName The name of the profile.
	 * @param riderType The rider type.
	 * @param bodyWeight The rider weight.
	 * @param bikeWeight The bike and gear weight
	 * @param frontTireWidth Front tire width (e.g. 20mm to 28mm)
	 * @param rearTireWidth Rear tire width (e.g. 20mm o 28mm)
	 * @param frontLoadPercent Percent of rider weight on the front wheel.
	 * @param rearLoadPercent Percent of the rider weight on the rear wheel.
	 */
	long addProfile(String profileName,
					String riderType,
					double bodyWeight,
					double bikeWeight,
					String frontTireWidth,
					String rearTireWidth,
					double frontLoadPercent,
					double rearLoadPercent) {
		ContentValues values = new ContentValues(8);
		values.put(ProfileColumns.PROFILE_NAME, profileName);
		values.put(ProfileColumns.RIDER_TYPE, riderType);
		values.put(ProfileColumns.BODY_WEIGHT, bodyWeight);
		values.put(ProfileColumns.BIKE_WEIGHT, bikeWeight);
		values.put(ProfileColumns.FRONT_TIRE_WIDTH, frontTireWidth);
		values.put(ProfileColumns.REAR_TIRE_WIDTH, rearTireWidth);
		values.put(ProfileColumns.FRONT_LOAD_PERCENT, frontLoadPercent);
		values.put(ProfileColumns.REAR_LOAD_PERCENT, rearLoadPercent);
		// try to update existing record instead of blindly adding a new record
		if (updateProfile(profileName, values)) {
			return 0;
		}

		return getWritableDatabase().insert(Tables.PROFILES, ProfileColumns.PROFILE_NAME, values);

	}

	/**
	 * Updates the profile by the given profile name.
	 * @param profileName The profile name of the database record.
	 * @param contentValues The new data values to update.
	 * @return Whether the profile was updated or not.
	 */
	private boolean updateProfile(String profileName, ContentValues contentValues) {
		return getWritableDatabase().update(Tables.PROFILES,
											contentValues,
											ProfileColumns.PROFILE_NAME + " = ?",
											new String[] {profileName}) > 0;
	}

	/**
	 * Deletes a profile record.
	 * @param rowId The row id of the record to delete.
	 * @return Whether the profile is deleted or not.
	 */
	public boolean deleteProfile(long rowId) {
		return getWritableDatabase().delete(Tables.PROFILES,
											BaseColumns._ID + " = ?",
											new String[] {String.valueOf(rowId)}) > 0;
	}

	/**
	 * Fetch a profile record cursor containing all profiles from the database.
	 * @return A profile record cursor containing all profiles from the database.
	 */
	Cursor getProfiles() {
		return getWritableDatabase().query(Tables.PROFILES,
										   new String[] {BaseColumns._ID,
														 ProfileColumns.PROFILE_NAME,
														 ProfileColumns.RIDER_TYPE,
														 ProfileColumns.BODY_WEIGHT,
														 ProfileColumns.BIKE_WEIGHT,
														 ProfileColumns.FRONT_TIRE_WIDTH,
														 ProfileColumns.REAR_TIRE_WIDTH,
														 ProfileColumns.FRONT_LOAD_PERCENT,
														 ProfileColumns.REAR_LOAD_PERCENT},
										   null,
										   null,
										   null,
										   null,
										   null);
	}

	/**
	 * Fetch a profile record cursor from the database.
	 * @param rowId The row id of the record to fetch.
	 * @return A profile record cursor from the database.
	 */
	public Cursor getProfile(long rowId) {
		return getWritableDatabase().query(true,
										   Tables.PROFILES,
										   null,
										   BaseColumns._ID + "=" + rowId,
										   null,
										   null,
										   null,
										   null,
										   null);
	}

	interface Tables {
		String PROFILES = "profiles";
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "
						   + Tables.PROFILES
						   + " ("
						   + BaseColumns._ID
						   + " INTEGER PRIMARY "
						   + "KEY AUTOINCREMENT,"
						   + ProfileColumns.PROFILE_NAME
						   + " TEXT,"
						   + ProfileColumns.RIDER_TYPE
						   + " TEXT,"
						   + ProfileColumns.BODY_WEIGHT
						   + " NUMBER,"
						   + ProfileColumns.BIKE_WEIGHT
						   + " NUMBER,"
						   + ProfileColumns.FRONT_TIRE_WIDTH
						   + " NUMBER,"
						   + ProfileColumns.REAR_TIRE_WIDTH
						   + " NUMBER,"
						   + ProfileColumns.FRONT_LOAD_PERCENT
						   + " NUMBER,"
						   + ProfileColumns.REAR_LOAD_PERCENT
						   + " NUMBER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.PROFILES);
		onCreate(db);
	}
}
