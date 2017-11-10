package com.nisoft.androidart;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nisoft.androidart.aidl.Book;
import com.nisoft.androidart.aidl.IBookManager;
import com.nisoft.androidart.aidl.IOnNewBookAddedListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG="MainActivity";
    private IOnNewBookAddedListener mNewBookAddedListener=new IOnNewBookAddedListener.Stub() {

        @Override
        public void onNewBookAdded(Book newBook) throws RemoteException {
            Log.e(TAG,newBook.getBookName());
        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBookManager = IBookManager.Stub.asInterface(service);
            try {
                List<Book> list = mBookManager.getBookList();
                Log.e(TAG,"query book list,list type:"+list.getClass().getCanonicalName());
                Log.e(TAG,"query book list:"+list.toString());
                mBookManager.addBook(new Book(3,"把时间当朋友"));
                List<Book> newList = mBookManager.getBookList();
                Log.e(TAG,"query book list:"+newList.toString());

            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                mBookManager.registerListener(mNewBookAddedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private IBookManager mBookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this,AIDLService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        if(mBookManager!=null&&mBookManager.asBinder().isBinderAlive()) {
            Log.e(TAG,"unregister listener:"+mNewBookAddedListener);
            try {
                mBookManager.unregisterListener(mNewBookAddedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(mConnection);
        super.onDestroy();
    }
}
