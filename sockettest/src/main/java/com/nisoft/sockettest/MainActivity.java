package com.nisoft.sockettest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int MESSAGE_RECEIVE_NEW_MSG = 1;
    private static final int MESSAGE_SOCKET_CONNECTED = 2;
    private TextView tv_msg;
    private Button btn_send_msg;
    private EditText et_msg_send;
    private Socket mClientSocket;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RECEIVE_NEW_MSG :
                    tv_msg.setText(tv_msg.getText()+(String)msg.obj);
                    break;
                case MESSAGE_SOCKET_CONNECTED :
                    btn_send_msg.setEnabled(true);
                    break;
                default:
                    break;
            }
        }
    };
    private PrintWriter mPrintWriter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_msg = (TextView)findViewById(R.id.tv_msg_receive);
        btn_send_msg = (Button)findViewById(R.id.btn_send_msg);
        et_msg_send = (EditText)findViewById(R.id.et_msg_send);
        btn_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = et_msg_send.getText().toString();
                if(!TextUtils.isEmpty(msg)&&mPrintWriter!=null) {
                    mPrintWriter.println(msg);
                    et_msg_send.setText("");
                    String time = formatDateTime(System.currentTimeMillis());
                    String showedMsg = "self "+time+":"+msg+"\n";
                    tv_msg.setText(tv_msg.getText()+showedMsg);
                }
            }
        });
        Intent service = new Intent(this,TCPServerService.class);
        startService(service);
        new Thread(){
            @Override
            public void run() {
                connectTCPServer();
            }
        }.start();
    }

    @SuppressLint("SimpleDateFormat")
    private String formatDateTime(long l) {
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(l));
    }

    @Override
    protected void onDestroy() {
        if(mClientSocket!=null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void connectTCPServer() {
        Socket socket = null;
        while (socket==null){
            try {
                socket = new Socket("localhost",8688);
                mClientSocket = socket;
                mPrintWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                mHandler.sendEmptyMessage(MESSAGE_SOCKET_CONNECTED);
                System.out.println("connect server success");
            } catch (IOException e) {
                SystemClock.sleep(1000);
                System.out.println("connect TCP server failed,retry...");
            }
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (!MainActivity.this.isFinishing()){
                String msg = br.readLine();
                System.out.println("receive:"+msg);
                if(msg!=null) {
                    String time = formatDateTime(System.currentTimeMillis());
                    String showedMsg = "server "+time+":"+msg+"\n";
                    mHandler.obtainMessage(MESSAGE_RECEIVE_NEW_MSG,showedMsg).sendToTarget();
                }
            }
            System.out.println("quit...");
            mPrintWriter.close();
            br.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
