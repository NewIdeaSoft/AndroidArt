package com.nisoft.messengertest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button btn_main_send_data;
    private EditText et_main_input;
    private Messenger mService;
    private Messenger mGetReplyMessenger = new Messenger(new ClientHandler());
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initListener();
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
    }

    private void initListener() {
        btn_main_send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_main_input.getText().toString();
                Message msg = Message.obtain(null,MessengerConstants.MSG_FROM_CLIENT);
                Bundle data = new Bundle();
                data.putString("msg",text);
                msg.setData(data);
                msg.replyTo = mGetReplyMessenger;
                try {
                    mService.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        btn_main_send_data = (Button)findViewById(R.id.btn_main_send_data);
        et_main_input = (EditText)findViewById(R.id.et_main_input);
    }

    private void initData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    private class ClientHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == MessengerConstants.MSG_FROM_SERVICE) {
                Toast.makeText(MainActivity.this, msg.getData().getString("reply"), Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    }
}
