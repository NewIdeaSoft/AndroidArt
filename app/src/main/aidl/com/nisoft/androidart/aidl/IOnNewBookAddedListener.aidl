// IOnNewBookAddedListener.aidl
package com.nisoft.androidart.aidl;
import com.nisoft.androidart.aidl.Book;
// Declare any non-default types here with import statements

interface IOnNewBookAddedListener {
    void onNewBookAdded(in Book newBook);
}
