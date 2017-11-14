package com.nisoft.binderpooltest;

import android.os.RemoteException;

/**
 * Created by Administrator on 2017/11/14.
 */

public class ComputeImpl extends ICompute.Stub {
    @Override
    public int add(int a, int b) throws RemoteException {
        return a+b;
    }
}
