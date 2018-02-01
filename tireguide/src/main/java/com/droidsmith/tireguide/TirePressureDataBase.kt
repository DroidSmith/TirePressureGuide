package com.droidsmith.tireguide

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Defines a Database to store bike profiles.
 */
internal class TirePressureDataBase(context: Context)
	: SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

	/**
	 * Fetch a profile record cursor containing all profiles from the database.
	 * @return A profile record cursor containing all profiles from the database.
	 */
	val profiles: Cursor
		get() = writableDatabase.query(Tables.profiles,
				arrayOf(
						BaseColumns._ID,
						ProfileColumns.PROFILE_NAME,
						ProfileColumns.RIDER_TYPE,
						ProfileColumns.BODY_WEIGHT,
						ProfileColumns.BIKE_WEIGHT,
						ProfileColumns.FRONT_TIRE_WIDTH,
						ProfileColumns.REAR_TIRE_WIDTH,
						ProfileColumns.FRONT_LOAD_PERCENT,
						ProfileColumns.REAR_LOAD_PERCENT
				),
				null,
				null,
				null,
				null,
				null
		)

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
	fun addProfile(profileName: String,
				   riderType: String,
				   bodyWeight: Double,
				   bikeWeight: Double,
				   frontTireWidth: String,
				   rearTireWidth: String,
				   frontLoadPercent: Double,
				   rearLoadPercent: Double
	): Long {
		val values = ContentValues(8)
		values.put(ProfileColumns.PROFILE_NAME, profileName)
		values.put(ProfileColumns.RIDER_TYPE, riderType)
		values.put(ProfileColumns.BODY_WEIGHT, bodyWeight)
		values.put(ProfileColumns.BIKE_WEIGHT, bikeWeight)
		values.put(ProfileColumns.FRONT_TIRE_WIDTH, frontTireWidth)
		values.put(ProfileColumns.REAR_TIRE_WIDTH, rearTireWidth)
		values.put(ProfileColumns.FRONT_LOAD_PERCENT, frontLoadPercent)
		values.put(ProfileColumns.REAR_LOAD_PERCENT, rearLoadPercent)
		// try to update existing record instead of blindly adding a new record
		return if (updateProfile(profileName, values)) 0
		else writableDatabase.insert(Tables.profiles, ProfileColumns.PROFILE_NAME, values)

	}

	/**
	 * Updates the profile by the given profile name.
	 * @param profileName The profile name of the database record.
	 * @param contentValues The new data values to update.
	 * @return Whether the profile was updated or not.
	 */
	private fun updateProfile(profileName: String, contentValues: ContentValues): Boolean {
		return writableDatabase.update(
				Tables.profiles,
				contentValues,
				ProfileColumns.PROFILE_NAME + " = ?",
				arrayOf(profileName)
		) > 0
	}

	/**
	 * Deletes a profile record.
	 * @param rowId The row id of the record to delete.
	 * @return Whether the profile is deleted or not.
	 */
	fun deleteProfile(rowId: Long): Boolean {
		return writableDatabase.delete(
				Tables.profiles,
				BaseColumns._ID + " = ?",
				arrayOf(rowId.toString())
		) > 0
	}

	/**
	 * Fetch a profile record cursor from the database.
	 * @param rowId The row id of the record to fetch.
	 * @return A profile record cursor from the database.
	 */
	fun getProfile(rowId: Long): Cursor {
		return writableDatabase.query(
				true,
				Tables.profiles,
				null,
				BaseColumns._ID + " = ?",
				arrayOf(rowId.toString()),
				null,
				null,
				null,
				null
		)
	}

	internal interface Tables {
		companion object {
			val profiles = "profiles"
		}
	}

	override fun onCreate(db: SQLiteDatabase) {
		db.execSQL(StringBuilder().apply {
			append("CREATE TABLE ${Tables.profiles} (")
			append("${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,")
			append("${ProfileColumns.PROFILE_NAME} TEXT,")
			append("${ProfileColumns.RIDER_TYPE} TEXT,")
			append("${ProfileColumns.BODY_WEIGHT} NUMBER,")
			append("${ProfileColumns.BIKE_WEIGHT} NUMBER,")
			append("${ProfileColumns.FRONT_TIRE_WIDTH} NUMBER,")
			append("${ProfileColumns.REAR_TIRE_WIDTH} NUMBER,")
			append("${ProfileColumns.FRONT_LOAD_PERCENT} NUMBER,")
			append("${ProfileColumns.REAR_LOAD_PERCENT} NUMBER)")
		}.toString())
	}

	override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
		db.execSQL("DROP TABLE IF EXISTS ${Tables.profiles}")
		onCreate(db)
	}

	companion object {
		private val DATABASE_VERSION = 1
	}
}
