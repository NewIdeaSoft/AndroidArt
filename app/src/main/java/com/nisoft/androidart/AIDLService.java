package com.nisoft.androidart;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.nisoft.androidart.aidl.Book;
import com.nisoft.androidart.aidl.IBookManager;
import com.nisoft.androidart.aidl.IOnNewBookAddedListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class AIDLService extends Service {
    private static final String TAG = "AIDLService";
    private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookAddedListener> mListeners = new RemoteCallbackList<>();
    private Binder mBinder = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            return mBookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            mBookList.add(book);
        }

        @Override
        public void registerListener(IOnNewBookAddedListener listener) throws RemoteException {
            mListeners.register(listener);

            Log.e(TAG, "listener list size:" + mListeners.getRegisteredCallbackCount());
        }

        @Override
        public void unregisterListener(IOnNewBookAddedListener listener) throws RemoteException {
            mListeners.unregister(listener);
            Log.e(TAG, "listener list size:" + mListeners.getRegisteredCallbackCount());
        }
    };
    private AtomicBoolean isServiceDestroyed = new AtomicBoolean();

    public AIDLService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBookList.add(new Book(1, "月亮与六便士"));
        mBookList.add(new Book(2, "老人与海"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        isServiceDestroyed.set(true);
        super.onDestroy();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while(!isServiceDestroyed.get()) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bookId = mBookList.size()+1;
                Book newBook = new Book(bookId,"new book#"+bookId);
                try {
                    onNewBookAdded(newBook);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void onNewBookAdded(Book newBook) throws RemoteException{
        mBookList.add(newBook);
        final int N = mListeners.beginBroadcast();
        for (int i=0;i<N;i++){
            IOnNewBookAddedListener listener = mListeners.getBroadcastItem(i);
            Log.e(TAG,"new book added,notify listener:"+listener);
            if(listener!=null) {

                listener.onNewBookAdded(newBook);
            }
        }
        mListeners.finishBroadcast();
    }
}
