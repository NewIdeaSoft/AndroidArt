package com.nisoft.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by Administrator on 2017/11/13.
 */

public class BookProvider extends ContentProvider {
    private static final String TAG = "BookProvider";
    public static final String AUTHORITY = "com.nisoft.contentprovidertest.book.provider";
    private static final Uri BOOK_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/book");
    private static final Uri USER_CONTENT_URI = Uri.parse("content://"+AUTHORITY+"/user");
    public static final int BOOK_URI_CODE = 0;
    public static final int USER_URI_CODE =1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY,"book",BOOK_URI_CODE);
        sUriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
    }

    private Context mContext;
    private SQLiteDatabase mDatabase;
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDatabase = new BookDBHelper(mContext).getWritableDatabase();
        mDatabase.execSQL("delete from "+BookDBHelper.BOOK_TABLE_NAME);
        mDatabase.execSQL("delete from "+BookDBHelper.USER_TABLE_NAME);
        mDatabase.execSQL("insert into "+BookDBHelper.BOOK_TABLE_NAME+" values (3,'Android');");
        mDatabase.execSQL("insert into "+BookDBHelper.BOOK_TABLE_NAME+" values (4,'Ios');");
        mDatabase.execSQL("insert into "+BookDBHelper.BOOK_TABLE_NAME+" values (5,'Html5');");
        mDatabase.execSQL("insert into "+BookDBHelper.USER_TABLE_NAME+" values (1,'jake',1);");
        mDatabase.execSQL("insert into "+BookDBHelper.USER_TABLE_NAME+" values (2,'jasmine',0);");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.d(TAG,"query current thread:"+Thread.currentThread().getName());
        String table = getTableName(uri);
        if(table==null) {
            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        return mDatabase.query(table,projection,selection,selectionArgs,null,sortOrder,null);
    }

    private String getTableName(Uri uri) {
        String tableName = null;
        switch (sUriMatcher.match(uri)){
            case BOOK_URI_CODE:
                tableName = BookDBHelper.BOOK_TABLE_NAME;
                break;
            case USER_URI_CODE:
                tableName = BookDBHelper.USER_TABLE_NAME;
                break;
        }
        return tableName;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Log.d(TAG,"getType");
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        String table = getTableName(uri);
        if(table==null) {
            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        mDatabase.insert(table,null,values);
        Log.d(TAG,"insert");
        return uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = getTableName(uri);
        if(table==null) {
            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        int count = mDatabase.delete(table,selection,selectionArgs);
        if(count>0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        String table = getTableName(uri);
        if(table==null) {
            throw new IllegalArgumentException("Unsupported URI:"+uri);
        }
        int row = mDatabase.update(table, values, selection, selectionArgs);
        if(row>0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return row;
    }
}
