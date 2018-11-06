package com.yuong.udpb;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private String TAG = this.getClass().getSimpleName();
    private String IP;

    private TextView tvContent;
    private Button btnClear;
    private StringBuilder mBuilder;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        String data = bundle.getString("data");
                        mBuilder.append(data).append("\n");
                    }
                    tvContent.setText(mBuilder.toString());
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        IP = getIpInLAN().getHostAddress();
        Log.e(TAG, "IP : " + IP);

        ReceiverThread receiverThread = new ReceiverThread(mHandler);
        receiverThread.start();

        mBuilder = new StringBuilder();
    }

    private void initView() {
        tvContent = findViewById(R.id.tv_content);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                mBuilder.delete(0, mBuilder.length());
                tvContent.setText("");
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

}
