package com.nisoft.contentprovidertest;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = Uri.parse("content://com.nisoft.contentprovidertest.book.provider/book");
        getContentResolver().query(uri,null,null,null,null);
        ContentValues values = new ContentValues();
        values.put("_id",6);
        values.put("name","老人与海");
        getContentResolver().insert(uri,values);
        Cursor cursor = getContentResolver().query(uri,new String[]{"_id","name"},null,null,null);
        while (cursor.moveToNext()){
            Book book = new Book();
            book.setId(cursor.getInt(cursor.getColumnIndex("_id")));
            book.setName(cursor.getString(cursor.getColumnIndex("name")));
            Log.d(TAG,"query book:"+book.toString());
        }
        cursor.close();
    }

}
