package com.example.xbeeandroidapp.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "history";
    private static final int DB_VERSION = 1;

    public HistoryDatabaseHelper (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE HISTORY ("
                 + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                 + "URL TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
