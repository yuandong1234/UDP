package com.yuong.udpb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by yuandong on 2018/11/6.
 */

public class ReceiverThread extends Thread {
    private final static int PORT = 48999;

    private DatagramSocket socket;
    private Handler mHandler;

    public ReceiverThread(Handler handler) {
        this.mHandler = handler;
        init();
    }

    private void init() {
        try {
            socket = new DatagramSocket(PORT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                //准备一个空的数据包
                byte buf[] = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                //调用udp的服务接收数据
                System.out.println(" ------------------> receiver data <--------------------------------");
                socket.receive(packet);//receive是非阻塞性的，如果接收不到数据会一直等待

                String data = new String(buf, 0, packet.getLength());//getLength()接收到的数据长度
                System.out.println("服务端: " + data);

                Message msg = new Message();
                msg.what = 0;
                Bundle bundle = new Bundle();
                bundle.putString("data", data);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                //关闭资源
                socket.close();
                break;
            }
        }
    }
}
