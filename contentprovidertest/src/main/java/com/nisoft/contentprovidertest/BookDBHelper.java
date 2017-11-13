package com.nisoft.contentprovidertest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/11/13.
 */

public class BookDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "book_provider_db";
    public static final String BOOK_TABLE_NAME = "book";
    public static final String USER_TABLE_NAME = "user";
    private static final int DB_VERSION = 1;
    private static final String CREATE_BOOK_TABLE = "create table "+BOOK_TABLE_NAME+"( _id integer primary key,name text)";
    private static final String CREATE_USER_TABLE = "create table "+USER_TABLE_NAME+"( _id integer primary key,name text,sex int)";

    public BookDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
