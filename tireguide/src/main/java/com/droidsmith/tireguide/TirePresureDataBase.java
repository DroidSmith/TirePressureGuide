package com.droidsmith.tireguide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Defines a Database to store bike profiles.
 */
public class TirePresureDataBase extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "bikeprofile.db";
	private static final int DATABASE_VERSION = 1;

	private SQLiteDatabase database;

	interface Tables {
		String PROFILES = "profiles";
	}

	public TirePresureDataBase(Context context) {
		super(context, DATABASE_NAME, null,	DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + Tables.PROFILES + " (" + BaseColumns._ID + " INTEGER PRIMARY " +
				"KEY AUTOINCREMENT," + "profile_name TEXT, body_weight NUMBER, bike_weight " +
				"NUMBER, front_tire_width NUMBER, rear_tire_width NUMBER, " +
				"rider_type TEXT, front_load_percent " +
				"NUMBER, rear_load_percent NUMBER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Tables.PROFILES);
		onCreate(db);
	}

	/**
	 * Opens database.
	 */
	public void open() {
		database = getWritableDatabase();
	}

	/**
	 * Closes database.
	 */
	public void close() {
		database.close();
	}

	/**
	 * Adds a profile record.
	 * @param contentValues The row content values to add.
	 * @return The row id of the new profile record.
	 */
	public long addProfile(ContentValues contentValues) {
		return database.insert(Tables.PROFILES, null, contentValues);
	}

	/**
	 * Deletes a profile record.
	 * @param rowId The row id of the record to delete.
	 * @return Whether the profile is deleted or not.
	 */
	public boolean deleteProfile(long rowId) {
		return database.delete(Tables.PROFILES, BaseColumns._ID + "=" + rowId, null) > 0;
	}

	/**
	 * Fetch a profile record cursor from the database.
	 * @param rowId The row id of the record to fetch.
	 * @return A profile record cursor from the database.
	 */
	public Cursor fetchProfile(long rowId) {
		return database.query(true, Tables.PROFILES, null, BaseColumns._ID + "=" + rowId, null,
				null, null, null, null);
	}

	/**
	 * Fetch a profile record cursor containing all profiles from the database.
	 * @return A profile record cursor containing all profiles from the database.
	 */
	public Cursor fetchAllProfiles() {
		return database.query(true, Tables.PROFILES, null, null, null,
				null, null, null, null);
	}

	/**
	 * Updates the profile by the given row id.
	 * @param rowId The row id of the database record.
	 * @param contentValues The new data values to update.
	 * @return Whether the profile was updated or not.
	 */
	public boolean updateProfile(long rowId, ContentValues contentValues) {
		return database.update(Tables.PROFILES, contentValues, BaseColumns._ID + "=" + rowId,
				null) > 0;
	}
}
