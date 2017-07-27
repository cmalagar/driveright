package com.example.cameron.fordapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * Created by anita13benita on 11/20/16.
 */


//http://stackoverflow.com/questions/4452538/location-of-sqlite-database-on-the-device
public class DatabaseOpenhelper extends SQLiteOpenHelper {

    final static String TABLE_NAME = "logtable";
    final static String GRADE= "name";
    final static String _ID = "_id";
    final static String[] columns = { _ID, GRADE };

    final private static String CREATE_CMD =

            "CREATE TABLE logtable (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + GRADE + " TEXT NOT NULL)";

    final private static String NAME = "logs_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public DatabaseOpenhelper(Context context) {
        //  super(context, "/mnt/sdcard/logs.db", null, 0); testing
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // N/A
    }

    void deleteDatabase() {
        mContext.deleteDatabase(NAME);
    }
}
