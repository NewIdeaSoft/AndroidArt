package com.nisoft.sockettest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class TCPServerService extends Service {
    private boolean isServiceDestroyed = false;
    private String[] mDefinedMessage = new String[]{
            "你好啊，哈哈",
            "请问你叫什么名字？",
            "今天天气不错。",
            "你知道吗？我可以多人同时聊天。",
            "据说爱笑的人运气不会太差。"
    };

    public TCPServerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        new Thread(new TCPServer()).start();
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        isServiceDestroyed = true;
        super.onDestroy();
    }

    private class TCPServer implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket = null;
            try {
                //监听端口8688
                serverSocket = new ServerSocket(8688);
            } catch (IOException e) {
                System.err.println("establish tcp server failed,port:8688");
                e.printStackTrace();
                return;
            }
            try {
                while (!isServiceDestroyed) {
                    final Socket client = serverSocket.accept();
                    System.out.println("accept");
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                responseClient(client);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 响应客户端
     * @param client
     * @throws IOException
     */
    private void responseClient(Socket client) throws IOException {
        //从客户端的socket获取输入流,读取客户端发送的消息
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        //获取输出流,发送服务器端的消息到客户端的输出流
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
        out.println("欢迎来到聊天室！");
        while (!isServiceDestroyed) {
            //读取消息
            String str = in.readLine();
            System.out.println("msg from client:" + str);
            if (str == null) {
                break;
            }
            int i = new Random().nextInt(mDefinedMessage.length);
            String msg = mDefinedMessage[i];
            //发送消息
            out.println(msg);
            System.out.println("send :" + msg);
        }
        System.out.println("client quit.");
        out.close();
        in.close();
        client.close();
    }
}
