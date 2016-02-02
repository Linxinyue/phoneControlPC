package com.example.zhenya.socket;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends Activity implements View.OnClickListener {
    private final static int acceptMsg = 0;
    private final static int getConnect = 1;
    private final static int stopSocket = 2;

    private TextView textReceive = null;
    private Button btnConnect = null;
    private Button btnSend = null;
    private Button btnDisconnect = null;

    private static final String ServerIP = "192.168.0.20";
    private static final int ServerPort = 8899;

    private Socket socket = null;
    private String strMessage;
    private boolean isConnect = false;
    private OutputStream outStream;
    private Handler myHandler = null;
    private ReceiveThread receiveThread = null;
    private boolean isReceive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //界面元素初始化
        textReceive = (TextView) findViewById(R.id.messageBox);
        btnConnect = (Button) findViewById(R.id.getConnect);
        btnSend = (Button) findViewById(R.id.send);
        btnDisconnect = (Button) findViewById(R.id.getDisconnect);
        btnDisconnect.setEnabled(false);
        btnSend.setEnabled(false);


        textReceive.setOnTouchListener(new MyLinstener());
        textReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnConnect.setOnClickListener(this);

        btnDisconnect.setOnClickListener(this);

        //发送消息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strMessage = "0:1|";//获取
                new Thread(sendThread).start();//新建线程发送
            }
        });

        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case acceptMsg:
                        textReceive.append((msg.obj).toString());
                        break;
                    case getConnect:
                        btnConnect.setEnabled(false);
                        btnDisconnect.setEnabled(true);
                        btnSend.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                        break;
                    case stopSocket:
                        StopSocket();
                        break;

                }
            }
        };
    }

    //连接到服务器的接口
    Runnable connectThread = new Runnable() {
        @Override
        public void run() {
            Message msg = new Message();
            try {
                //初始化Socket，连接到服务器
                socket = new Socket(ServerIP, ServerPort);
                isConnect = true;

                //启动接收线程
                isReceive = true;
                receiveThread = new ReceiveThread(socket);
                receiveThread.start();
                System.out.println("----connected success----");

                msg.what = getConnect;
                msg.obj = "----connected success----";
                myHandler.sendMessage(msg);

                strMessage = "连接成功";//获取
                new Thread(sendThread).start();//新建线程发送
            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println("UnknownHostException-->" + e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException" + e.toString());
            }
        }
    };

    //发送消息的接口
    Runnable sendThread = new Runnable() {

        @Override
        public void run() {
            byte[] sendBuffer = null;
            try {
                sendBuffer = strMessage.getBytes("utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            try {
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (sendBuffer != null) {
                    outStream.write(sendBuffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getConnect:
                if (!isConnect) {//如果没有连接则新开线程连接
                    new Thread(connectThread).start();
                }
                break;
            case R.id.getDisconnect:
//                onDestroy();
                Toast.makeText(getApplicationContext(), "正在关闭。。。", Toast.LENGTH_LONG).show();
                StopSocket();
                break;
        }
    }

    private void StopSocket() {
        if (receiveThread != null) {
            isConnect = false;
            isReceive = false;
            receiveThread.interrupt();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        btnSend.setEnabled(false);
        btnConnect.setEnabled(true);
        btnDisconnect.setEnabled(false);
        Toast.makeText(getApplicationContext(), "断开连接成功", Toast.LENGTH_LONG).show();
    }

    //接收消息线程
    private class ReceiveThread extends Thread {
        private InputStream inStream = null;

        private byte[] buffer;
        private String str = null;

        ReceiveThread(Socket socket) {
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isReceive) {
                buffer = new byte[512];
                try {
                    inStream.read(buffer);
                } catch (IOException e) {
                    Message msg = new Message();
                    msg.what = stopSocket;
                    myHandler.sendMessage(msg);
                    e.printStackTrace();
                }
                try {
                    str = new String(buffer, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = acceptMsg;
                msg.obj = str + "\n";
                myHandler.sendMessage(msg);
            }
        }
    }

    float oldX1 = 0;
    float oldY1 = 0;
    float oldX2 = 0;
    float oldY2 = 0;
    float oldX3 = 0;
    float oldY3 = 0;


    float newX1 = 0;
    float newY1 = 0;
    float newX2 = 0;
    float newY2 = 0;
    float newX3 = 0;
    float newY3 = 0;

    float temp1X = 0;
    float temp1Y = 0;

    float temp3X = 0;
    float temp3Y = 0;

    private class MyLinstener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int pointerCount = event.getPointerCount();

            newX1 = event.getX(0);
            newY1 = event.getY(0);

            if (pointerCount > 1) {
                newX2 = event.getX(1);
                newY2 = event.getY(1);
            }
            if (pointerCount > 2) {
                newX3 = event.getX(2);
                newY3 = event.getY(2);
            }
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    if (pointerCount == 1) {
                        if (Math.abs(temp1X) == 0 && Math.abs(temp1Y) == 0) {
                            printLog("0:0|");
                            Log.i("text", (int)temp1X + ":" + (int)temp1Y);
                        }
                        temp1X = 0;
                        temp1Y = 0;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    if (pointerCount == 3) {
                        if (Math.abs(temp3X) > Math.abs(temp3Y)) {
                            if (temp3X > 0) {
                                printLog("3:1|");
                            } else {
                                printLog("3:0|");
                            }
                        } else {
                            if (temp3Y > 0) {
                                printLog("3:2|");
                            } else {
                                printLog("3:3|");
                            }
                        }

                        temp3Y = 0;
                        temp3X = 0;
                    }

                    return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    if (pointerCount == 1) {
                        temp1X += newX1 - oldX1;
                        temp1Y += newY1 - oldY1;
                        if ((Math.abs(newX1 - oldX1) > 1 || Math.abs(newY1 - oldY1) > 1)
                                && (Math.abs(newX1 - oldX1) < 80 && Math.abs(newY1 - oldY1) < 80)) {
                            printLog("1:" + (int)(newX1 - oldX1) + "," + (int)(newY1 - oldY1) + "|");
                        }
                    } else if (pointerCount == 2) {
                        if ((Math.abs(newX1 - oldX1) > 1 || Math.abs(newY1 - oldY1) > 1
                                || Math.abs(newX2 - oldX2) > 1 || Math.abs(newY2 - oldY2) > 1)
                                && (Math.abs(newX1 - oldX1) < 80 && Math.abs(newY1 - oldY1) < 80
                                && Math.abs(newX2 - oldX2) < 80 && Math.abs(newY2 - oldY2) < 80)) {
                            float distance = ((newX1 - oldX1) + (newX2 - oldX2)) / 2;
                            printLog("2:" + (int)distance + "|");
                        }
                    } else if (pointerCount == 3) {
                        temp3X += newX3 - oldX3;
                        temp3Y += newY3 - oldY3;
                    }
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            oldX1 = newX1;
            oldY1 = newY1;
            oldX2 = newX2;
            oldY2 = newY2;

            oldX3 = newX3;
            oldY3 = newY3;
            return false;
        }
    }


    private void printLog(final String msg) {

        if (isConnect) {
            Thread taskThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    strMessage = msg;//获取
                    new Thread(sendThread).start();//新建线程发送
                }
            });
            taskThread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiveThread != null) {
            isReceive = false;
            receiveThread.interrupt();
        }
    }
}
