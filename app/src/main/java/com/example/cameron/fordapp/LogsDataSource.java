package com.example.cameron.fordapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cameron on 12/4/2016.
 *
 * D
 */

public class LogsDataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_DRIVE_ID,
             MySQLiteHelper.COLUMN_DATE, MySQLiteHelper.COLUMN_LOG, MySQLiteHelper.COLUMN_SPEED };

    public LogsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Log createLog(Log log) {
        ContentValues values = new ContentValues();

        values.put(MySQLiteHelper.COLUMN_DATE, log.getDate());
        values.put(MySQLiteHelper.COLUMN_LOG, log.getViolation());
        values.put(MySQLiteHelper.COLUMN_DRIVE_ID, log.getDriveID());
        values.put(MySQLiteHelper.COLUMN_SPEED, log.getSpeed());
        long insertId = database.insert(MySQLiteHelper.TABLE_LOGS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Log newLog = cursorToLog(cursor);
        cursor.close();
        return newLog;
    }

    /**
     * Gets the logs corresponding to a driveID
     *
     * @return
     */
    public List<Log> getLastDriveLogs() {
        List<Log> logs = new ArrayList<Log>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGS,
                allColumns, null, null, null, null, null);

        if (cursor.moveToLast()) {
            Log log = cursorToLog(cursor);
            long driveID = log.getDriveID();
            while (!cursor.isBeforeFirst() && log.getDriveID() == driveID) {
                log = cursorToLog(cursor);
                if (log.getDriveID() == driveID) {
                    logs.add(log);
                }
                cursor.moveToPrevious();
            }
        }
        // make sure to close the cursor
        cursor.close();
        return logs;
    }

    private Log cursorToLog(Cursor cursor) {
        Log log = new Log();
        log.setId(cursor.getInt(0));
        log.setDriveID(cursor.getInt(1));
        log.setDate(cursor.getString(2));
        log.setViolation(cursor.getString(3));
        log.setSpeed_count(cursor.getInt(4));
        return log;
    }

}
