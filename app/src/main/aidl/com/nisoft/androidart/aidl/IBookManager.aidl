// IBookManager.aidl
package com.nisoft.androidart.aidl;
import com.nisoft.androidart.aidl.Book;
import com.nisoft.androidart.aidl.IOnNewBookAddedListener;

// Declare any non-default types here with import statements

interface IBookManager {
    List<Book> getBookList();
    void addBook(in Book book);
    void registerListener(IOnNewBookAddedListener listener);
    void unregisterListener(IOnNewBookAddedListener listener);
}
