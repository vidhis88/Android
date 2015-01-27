package com.mileagetracker.dao;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vshah2 on 10/27/14.
 */

public class MileageTrackerDB {
	private static final String TAG = "MileageTrackerDB";

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "mileage_tracker_schema";

	//The columns we'll include in the Vehicle Info table
	private static final String VEHICLE_MAKE_KEY = "make";
	private static final String VEHICLE_MODEL_KEY = "model";
	private static final String VEHICLE_YEAR_KEY = "year";
	private static final String VEHICLE_NAME_KEY = "name";

	private static final String VEHICLE_INFO_TABLE = "vehicle_info";

	private static final String VEHICLE_INFO_TABLE_CREATE =
			"CREATE TABLE " + VEHICLE_INFO_TABLE + " (" +
					VEHICLE_MAKE_KEY + " VARCHAR(20), " +
					VEHICLE_MODEL_KEY + " VARCHAR(30), " +
					VEHICLE_YEAR_KEY + " INT, " +
					VEHICLE_NAME_KEY + " VARCHAR(30) NOT NULL)";

	//The columns we'll include in the Mileage Tracker table
	private static final String ODOMETER_READING_KEY = "odometer_reading";
	private static final String GALLONS_FILLED_KEY = "gallons_filled";
	private static final String AMOUNT_KEY = "amount";
	private static final String VEHICLE_ID_KEY = "vehicle_id";
	private static final String FUEL_BRAND_KEY = "fuel_brand";
	private static final String MPG_KEY = "mpg";
	private static final String ENTRY_TS_KEY = "entry_timestamp";

	private static final String MILEAGE_DETAILS_TABLE = "mileage_record";

	private static final String MILEAGE_DETAILS_TABLE_CREATE =
			"CREATE TABLE " + MILEAGE_DETAILS_TABLE + " (" +
					ODOMETER_READING_KEY + " UNSIGNED INT, " +
					GALLONS_FILLED_KEY + " FLOAT, " +
					AMOUNT_KEY + " FLOAT, " +
					VEHICLE_ID_KEY + " INT REFERENCES " + VEHICLE_INFO_TABLE + "(id), " +
					FUEL_BRAND_KEY + " VARCHAR(20), " +
					MPG_KEY + " FLOAT, " +
					ENTRY_TS_KEY + " TIMESTAMP DEFAULT (STRFTIME('%s', 'NOW')))";

	private SQLiteDatabase db;
	private static MileageTrackerDB dbInstance;
	private final TrackerOpenHelper mDatabaseOpenHelper;
	private static final HashMap<String, String> mColumnMap = buildColumnMap();

	/**
	 * Constructor
	 *
	 * @param context The Context within which to work, used to create the DB
	 */
	public MileageTrackerDB(Context context) {
		mDatabaseOpenHelper = new TrackerOpenHelper(context);
		db = mDatabaseOpenHelper.getWritableDatabase();
	}

	public static MileageTrackerDB getInstance(Context context) {
		if (dbInstance == null) {
			dbInstance = new MileageTrackerDB(context);
		}

		return dbInstance;
	}

	/**
	 * Builds a map for all columns that may be requested, which will be given to the
	 * SQLiteQueryBuilder. This is a good way to define aliases for column names, but must include
	 * all columns, even if the value is the key. This allows the ContentProvider to request
	 * columns w/o the need to know real column names and create the alias itself.
	 */
	private static HashMap<String, String> buildColumnMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		//			map.put(KEY_WORD, KEY_WORD);
		//			map.put(KEY_DEFINITION, KEY_DEFINITION);
		map.put(BaseColumns._ID, "rowid AS " + BaseColumns._ID);
		map.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, "rowid AS " +
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
		map.put(SearchManager.SUGGEST_COLUMN_SHORTCUT_ID, "rowid AS " +
				SearchManager.SUGGEST_COLUMN_SHORTCUT_ID);
		return map;
	}

	/**
	 * Performs a database query.
	 *
	 * @param selection     The selection clause
	 * @param selectionArgs Selection arguments for "?" components in the selection
	 * @param columns       The columns to return
	 * @return A Cursor over all rows matching the query
	 */
	private Cursor query(String selection, String[] selectionArgs, String[] columns, String orderBy) {
	    /* The SQLiteBuilder provides a map for all possible columns requested to
	     * actual columns in the database, creating a simple column alias mechanism
         * by which the ContentProvider does not need to know the real column names
         */
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(MILEAGE_DETAILS_TABLE);
		//		builder.setProjectionMap(mColumnMap);

		Cursor cursor = builder.query(mDatabaseOpenHelper.getReadableDatabase(),
				columns, selection, selectionArgs, null, null, orderBy);

		if (cursor == null) {
			return null;
		} else if (!cursor.moveToFirst()) {
			cursor.close();
			return null;
		}
		return cursor;
	}

	public MileageRecord getLastRecord() {
		MileageRecord record = null;
		Cursor c = query(null, null, null, ENTRY_TS_KEY + " DESC");

		if (c != null && c.moveToFirst()) {
			record = new MileageRecord();
			record.setOdometerReading(c.getInt(0));
			record.setGallonsFilled(c.getFloat(1));
			record.setAmount(c.getFloat(2));
			record.setFuelBrand(c.getString(4));
			record.setMpg(c.getFloat(5));
			record.setEntryTimeMillis(c.getLong(6));

			c.close();
		}

		return record;
	}

	public int getNumRecords() {
		int count = 0;

		Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + MILEAGE_DETAILS_TABLE, null);
		if (c != null && c.moveToFirst()) {
			count = c.getInt(0);
		}
		c.close();
		return count;
	}

	public float getAvgMpg() {
		float avgMpg = 0.0f;

		Cursor c = db.rawQuery("SELECT AVG(" + MPG_KEY + ") FROM " + MILEAGE_DETAILS_TABLE, null);
		if (c != null && c.moveToFirst()) {
			avgMpg = c.getFloat(0);
		}
		c.close();

		return avgMpg;
	}

	public int getNumVehicles() {
		int count = 0;

		Cursor c = db.rawQuery("SELECT COUNT(*) FROM " + VEHICLE_INFO_TABLE, null);
		if (c != null && c.moveToFirst()) {
			count = c.getInt(0);
		}
		c.close();
		return count;
	}

	public float getVehicleInitialMPG(long vehicleId) {
		float initialMPG = 0.0f;

		Cursor c = db.query(MILEAGE_DETAILS_TABLE, new String[]{MPG_KEY, "MIN(" + ENTRY_TS_KEY + ")"}, VEHICLE_ID_KEY + "=?", new String[]{String.valueOf(vehicleId)}, null, null, null);
		if (c != null && c.moveToFirst()) {
			initialMPG = c.getFloat(0);
		}
		c.close();
		return initialMPG;
	}

	public String getVehicleNameById(long vehicleId) {
		Cursor c = db.query(VEHICLE_INFO_TABLE, new String[]{VEHICLE_NAME_KEY}, "rowid=?", new String[]{String.valueOf(vehicleId)}, null, null, null);
		if (c != null && c.moveToFirst()) {
			return c.getString(0);
		}

		return null;
	}

	public List<MileageRecord> getAllRecordsByVehicleId(long vehicleId) {
		List<MileageRecord> records = new ArrayList<MileageRecord>();

		String[] columns = new String[]{MPG_KEY, AMOUNT_KEY, FUEL_BRAND_KEY, ENTRY_TS_KEY};
		Cursor c = db.query(MILEAGE_DETAILS_TABLE, columns, VEHICLE_ID_KEY + "=?", new String[]{String.valueOf(vehicleId)}, null, null, ENTRY_TS_KEY + " DESC");
		if (c != null && c.moveToFirst()) {
			do {
				MileageRecord record = new MileageRecord();
				record.setMpg(c.getFloat(0));
				record.setAmount(c.getFloat(1));
				record.setFuelBrand(c.getString(2));

				long timeInMillis = c.getLong(3)* 1000;
				record.setEntryTimeMillis(timeInMillis);

				records.add(record);
			} while (c.moveToNext());
		}

		return records;
	}

	/**
	 * Add a new vehicle's info to the db
	 *
	 * @return rowId or -1 if failed
	 */
	public long addVehicleInfo(VehicleDTO vehicle) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(VEHICLE_MAKE_KEY, vehicle.getMake());
		initialValues.put(VEHICLE_MODEL_KEY, vehicle.getModel());
		initialValues.put(VEHICLE_YEAR_KEY, vehicle.getYear());
		initialValues.put(VEHICLE_NAME_KEY, vehicle.getName());

		return db.insert(VEHICLE_INFO_TABLE, null, initialValues);
	}

	/**
	 * Add a mileage record to the db
	 *
	 * @return rowId or -1 if failed
	 */
	public long addRecord(MileageRecord record) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(ODOMETER_READING_KEY, record.getOdometerReading());
		initialValues.put(GALLONS_FILLED_KEY, record.getGallonsFilled());
		initialValues.put(AMOUNT_KEY, record.getAmount());
		initialValues.put(VEHICLE_ID_KEY, record.getVehicleId());
		initialValues.put(FUEL_BRAND_KEY, record.getFuelBrand());
		initialValues.put(MPG_KEY, record.getMpg());

		return db.insert(MILEAGE_DETAILS_TABLE, null, initialValues);
	}

	/**
	 * This creates/opens the database.
	 */
	private class TrackerOpenHelper extends SQLiteOpenHelper {

		public TrackerOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(MILEAGE_DETAILS_TABLE_CREATE);
			db.execSQL(VEHICLE_INFO_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS " + MILEAGE_DETAILS_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + VEHICLE_INFO_TABLE);

			onCreate(db);
		}
	}
}
