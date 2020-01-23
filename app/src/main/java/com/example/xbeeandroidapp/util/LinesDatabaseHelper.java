package com.example.xbeeandroidapp.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LinesDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "lines";
    private static final int DB_VERSION = 1;

    public LinesDatabaseHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE LINES ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "AT TEXT,"
                + "AT_INDEX INTEGER);");
        insertLines(db, "D0", 0);
        insertLines(db, "D1", 1);
        insertLines(db, "D2", 2);
        insertLines(db, "D3", 3);
        insertLines(db, "D4", 4);
        insertLines(db, "D5", 5);
        insertLines(db, "P0", 10);
        insertLines(db, "P1", 11);
        insertLines(db, "P2", 12);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void insertLines(SQLiteDatabase db, String at, Integer index) {
        ContentValues values = new ContentValues();
        values.put("AT", at);
        values.put("AT_INDEX", index);
        db.insert("LINES", null, values);
    }
}
