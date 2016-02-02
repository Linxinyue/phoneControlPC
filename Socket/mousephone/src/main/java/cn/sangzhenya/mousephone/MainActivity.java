package cn.sangzhenya.mousephone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText ip=null;
    private EditText port=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip= (EditText) findViewById(R.id.ip);
        port= (EditText) findViewById(R.id.port);

    }
    public void getConnect(View v){
        String ipStr=ip.getText().toString();
        String portStr=port.getText().toString();
        if(ipStr.length()!=0&&portStr.length()!=0){
            Intent intent=new Intent(MainActivity.this,GestureRecognition.class);
            intent.putExtra("ip",ipStr);
            intent.putExtra("port",portStr);
            startActivity(intent);
        }

    }
    public void getConnectVertical(View v){
        String ipStr=ip.getText().toString();
        String portStr=port.getText().toString();
        if(ipStr.length()!=0&&portStr.length()!=0){
            Intent intent=new Intent(MainActivity.this,GestureVertical.class);
            intent.putExtra("ip",ipStr);
            intent.putExtra("port",portStr);
            startActivity(intent);
        }
    }


}
