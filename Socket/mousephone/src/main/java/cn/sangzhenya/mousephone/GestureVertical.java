package cn.sangzhenya.mousephone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by sangz on 2016/2/2.
 */
public class GestureVertical extends AppCompatActivity{

    private final static int getConnect = 1;

    private Button leftButton = null;
    private Button rightButton = null;
    private TextView messageBox=null;

    private static  String ServerIP = "192.168.0.20";
    private static  int ServerPort = 8899;

    private Socket socket = null;
    private String strMessage="";
    private boolean isConnect = false;
    private OutputStream outStream;
    private Handler myHandler = null;
    private ReceiveThread receiveThread = null;
    private boolean isReceive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesturevertical);

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();


        try {
            ServerIP=bundle.getString("ip");
            ServerPort=Integer.parseInt(bundle.getString("port"));
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),"ip或port出错",Toast.LENGTH_SHORT).show();
            finish();
        }

        leftButton= (Button) findViewById(R.id.leftVer);
        rightButton= (Button) findViewById(R.id.rightVer);
        messageBox= (TextView) findViewById(R.id.messageBoxVer);

        if (!isConnect) {//如果没有连接则新开线程连接
            new Thread(connectThread).start();
        }
        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        messageBox.setOnTouchListener(new MyLinstener());
        leftButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        sendMessage("0:2|");
                        break;
                    case MotionEvent.ACTION_UP:
                        sendMessage("0:3|");
                        break;
                }
                return false;
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("0:1|");
            }
        });
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case getConnect:
                        Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

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

                StopSocket();
                finish();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("IOException" + e.toString());

                StopSocket();
                finish();
            }
        }
    };

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
                    StopSocket();
                    e.printStackTrace();
                }
                try {
                    str = new String(buffer, "UTF-8").trim();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
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
            Log.i("GestureRecognition","socket error");
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
                            sendMessage("0:0|");
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
                                sendMessage("3:3|");
                            } else {
                                sendMessage("3:2|");
                            }
                        } else {
                            if (temp3Y > 0) {
                                sendMessage("3:1|");
                            } else {
                                sendMessage("3:0|");
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
                            sendMessage("1:" + (int)(newY1 - oldY1) + "," + (int)(oldX1-newX1) + "|");
                        }
                    } else if (pointerCount == 2) {
                        if ((Math.abs(newX1 - oldX1) > 1 || Math.abs(newY1 - oldY1) > 1
                                || Math.abs(newX2 - oldX2) > 1 || Math.abs(newY2 - oldY2) > 1)
                                && (Math.abs(newX1 - oldX1) < 80 && Math.abs(newY1 - oldY1) < 80
                                && Math.abs(newX2 - oldX2) < 80 && Math.abs(newY2 - oldY2) < 80)) {
                            float distance = ((newY1 - oldY1) + (newY2 - oldY2)) / 2;
                            sendMessage("2:" + (int)distance + "|");
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

    @Override
    protected void onDestroy() {
        StopSocket();
        super.onDestroy();
    }

    private void sendMessage(final String msg) {
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
}

