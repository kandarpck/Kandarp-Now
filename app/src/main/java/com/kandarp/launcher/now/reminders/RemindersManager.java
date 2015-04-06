package com.kandarp.launcher.now.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class RemindersManager extends SQLiteOpenHelper {

    public static final String TABLE_REMINDERS = "reminders";
    public static final String DATE_AND_TIME = "date_and_time";
    public static final String EXPIRED = "expired";
    public static final String NAME = "name";
    public static final String LOCATION = "location";
    public static final String NOTES = "notes";

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_CREATE = "CREATE TABLE " +
            TABLE_REMINDERS + "(" + DATE_AND_TIME + " TEXT PRIMARY KEY NOT NULL, " +
            EXPIRED + " INTEGER DEFAULT 0, " + NAME + " TEXT NOT NULL, " +
            LOCATION + " TEXT, " + NOTES + " TEXT);";


    public RemindersManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i(getClass().getName(), "Creating table " + TABLE_REMINDERS + ".");
        sqLiteDatabase.execSQL(DATABASE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(getClass().getName(), "Upgrading database from version " + i + " to version " + i1 + ".");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(sqLiteDatabase);
    }

    public void checkAndSetExpired() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_REMINDERS + " SET " + EXPIRED + "=1 WHERE " + DATE_AND_TIME +
                "< CURRENT_TIMESTAMP;");
    }

    public void addReminder(HashMap<String, String> values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(DATE_AND_TIME, values.get(DATE_AND_TIME));
        content.put(NAME, values.get(NAME));
        content.put(LOCATION, values.get(LOCATION));
        content.put(NOTES, values.get(NOTES));

        db.insert(TABLE_REMINDERS, null, content);
        Log.i(getClass().getName(), "Successfully added " + values.get(NAME) + " to " + TABLE_REMINDERS + ".");
    }

    public void deleteReminder(Date dateAndTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REMINDERS + " WHERE " + DATE_AND_TIME + "='" + dateFormat.format(dateAndTime) + "';");
        Log.i(getClass().getName(), "Deleted record with timestamp " + dateFormat.format(dateAndTime) + " from " + TABLE_REMINDERS + ".");
    }

    public void deleteExpiredReminders() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REMINDERS + " WHERE " + EXPIRED + "=1");
        Log.i(getClass().getName(), "Cleared all expired records from " + TABLE_REMINDERS + ".");
    }

    public void deleteAllReminders() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REMINDERS);
        Log.i(getClass().getName(), "Cleared all records from " + TABLE_REMINDERS + ".");
    }

    public ArrayList<HashMap<String, String>> getUpcomingReminders() {
        ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REMINDERS +
                " WHERE " + EXPIRED + "=0 ORDER BY DATETIME(" + DATE_AND_TIME + ")", null);

        if (cursor.moveToNext()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(DATE_AND_TIME, cursor.getString(cursor.getColumnIndex(DATE_AND_TIME)));
                map.put(EXPIRED, cursor.getString(cursor.getColumnIndex(EXPIRED)));
                map.put(NAME, cursor.getString(cursor.getColumnIndex(NAME)));
                map.put(LOCATION, cursor.getString(cursor.getColumnIndex(LOCATION)));
                map.put(NOTES, cursor.getString(cursor.getColumnIndex(NOTES)));
                records.add(map);
            } while (cursor.moveToNext());
        }
        return records;
    }

    public ArrayList<HashMap<String, String>> getExpiredReminders() {
        ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REMINDERS +
                " WHERE " + EXPIRED + "=1 ORDER BY DATETIME(" + DATE_AND_TIME + ")", null);

        if (cursor.moveToNext()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(DATE_AND_TIME, cursor.getString(cursor.getColumnIndex(DATE_AND_TIME)));
                map.put(EXPIRED, cursor.getString(cursor.getColumnIndex(EXPIRED)));
                map.put(NAME, cursor.getString(cursor.getColumnIndex(NAME)));
                map.put(LOCATION, cursor.getString(cursor.getColumnIndex(LOCATION)));
                map.put(NOTES, cursor.getString(cursor.getColumnIndex(NOTES)));
                records.add(map);
            } while (cursor.moveToNext());
        }
        return records;
    }

    public HashMap<String, String> getReminder(Date dateAndTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        HashMap<String, String> record = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_REMINDERS +
                " WHERE " + DATE_AND_TIME + "='" + dateFormat.format(dateAndTime) + "'", null);

        if (cursor.moveToNext()) {
            do {
                record.put(DATE_AND_TIME, cursor.getString(cursor.getColumnIndex(DATE_AND_TIME)));
                record.put(EXPIRED, cursor.getString(cursor.getColumnIndex(EXPIRED)));
                record.put(NAME, cursor.getString(cursor.getColumnIndex(NAME)));
                record.put(LOCATION, cursor.getString(cursor.getColumnIndex(LOCATION)));
                record.put(NOTES, cursor.getString(cursor.getColumnIndex(NOTES)));
            } while (cursor.moveToNext());
        }
        return record;

    }
}
