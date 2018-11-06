package com.yuong.udpa;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private EditText etContent;
    private Button btnSend;


    private SendThread mSendThread;
    private ReceiverThread mReceiverThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        InetAddress inetAddress = getIpInLAN();
        if (inetAddress != null) {
            String ip = inetAddress.getHostAddress();
            Log.e(TAG, " ip : " + ip);
        }

        mReceiverThread = new ReceiverThread();
        mReceiverThread.start();

        mSendThread = new SendThread(mReceiverThread);
        mSendThread.start();
        String data = "请告诉我你的ip";
        mSendThread.putMsg(data.getBytes());
    }

    private void initView() {
        etContent = findViewById(R.id.et_content);
        btnSend = findViewById(R.id.bt_send);

        btnSend.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_send:
                String content = etContent.getText().toString().trim();
                if(!TextUtils.isEmpty(content)){
                    mSendThread.putMsg(content.getBytes());
                }else{
                    Toast.makeText(this,"发送内容不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public InetAddress getIpInLAN() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                if (nif.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();

                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        System.out.println("网卡接口名称：" + nif.getName());
                        if (addr.getAddress().length == 4) { // 速度快于 instanceof
                            return addr;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSendThread.setSend(false);
        mReceiverThread.setReceive(false);
        mReceiverThread.close();
    }
}
