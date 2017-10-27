package tomikaa.greeremote.device;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by tomikaa on 2017. 10. 27..
 */

public class DeviceDatabase extends SQLiteOpenHelper {
    private static final String LOG_TAG = "DeviceDatabase";

    private static final int DATABASE_VERSION = 1;

    private static final String KEY_DEV_ID = "dev_id";
    private static final String KEY_DEV_NAME = "dev_name";
    private static final String KEY_DEV_AES_KEY = "dev_key";

    private static final int IDX_DEV_ID = 0;
    private static final int IDX_DEV_NAME = 1;
    private static final int IDX_DEV_AES_KEY = 2;

    private static final String DEVICE_STORAGE_TABLE_NAME = "devices";
    private static final String DEVICE_STORAGE_TABLE_CREATE =
            "CREATE TABLE " + DEVICE_STORAGE_TABLE_NAME + " (" +
            KEY_DEV_ID + " VARCHAR(20), " +
            KEY_DEV_NAME + " TEXT, " +
            KEY_DEV_AES_KEY + " VARCHAR(20), " +
            "PRIMARY KEY (" + KEY_DEV_ID + "));";

    private final ArrayList<DeviceDescriptor> mDeviceDescriptors = new ArrayList<>();

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use to open or create the database
     */
    public DeviceDatabase(Context context) {
        super(context, DEVICE_STORAGE_TABLE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DEVICE_STORAGE_TABLE_CREATE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void loadDevices() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(DEVICE_STORAGE_TABLE_NAME,
                new String[]{ KEY_DEV_ID, KEY_DEV_NAME, KEY_DEV_AES_KEY},
                null, null, null, null, null);

        mDeviceDescriptors.clear();

        if (c.getCount() > 0) {
            do {
                DeviceDescriptor d = new DeviceDescriptor(
                    c.getString(IDX_DEV_ID),
                    c.getString(IDX_DEV_NAME),
                    c.getString(IDX_DEV_AES_KEY)
                );

                mDeviceDescriptors.add(d);

                c.moveToNext();
            } while (!c.isLast());
        }
    }

    public void saveDevice(String id, String name, String key) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DEV_ID, id);
        values.put(KEY_DEV_NAME, name);
        values.put(KEY_DEV_AES_KEY, key);

        try {
            db.insertOrThrow(DEVICE_STORAGE_TABLE_NAME, null, values);
            mDeviceDescriptors.add(new DeviceDescriptor(id, name, key));
        } catch (SQLiteConstraintException e) {
            if (e.getMessage().contains("UNIQUE")) {
                Log.i(LOG_TAG, "DeviceDescriptor already saved: " + id);
            } else {
                Log.e(LOG_TAG, String.format("Failed to save device (%s). Error: %s", id, e.getMessage()));
            }
        }
    }

    public void removeDevice(String id) {
        for (DeviceDescriptor d: mDeviceDescriptors) {
            if (d.id.equals(id))
            {
                mDeviceDescriptors.remove(d);
                break;
            }
        }

        SQLiteDatabase db = getWritableDatabase();
        db.delete(DEVICE_STORAGE_TABLE_NAME, KEY_DEV_ID + "=?", new String[]{ id });
    }
}
