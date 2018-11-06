package com.yuong.udpa;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 用于接收服务端的数据
 * Created by yuandong on 2018/11/5.
 */

public class ReceiverThread extends Thread {
    private static String TAG = ReceiverThread.class.getSimpleName();

    private final String IP = "192.168.43.255";//广播地址（一般手机热点开的热点的ip 以192.168.43.xxx）
    //private final String IP = "255.255.255.255";//广播地址
    private int PORT = 52100;
    private DatagramSocket socket;

    private int targetPort = 48999;

    private boolean receive = true;

    public ReceiverThread() {
        init();
    }

    private void init() {
        try {
            socket = new DatagramSocket(null);
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (socket == null) {
            return;
        }
        try {
            byte[] data = new byte[1024];
            //创建一个空的DatagramPacket对象
            DatagramPacket revPacket = new DatagramPacket(data, data.length);
            while (receive) {
                //服务端接收数据
                Log.e(TAG, "------------------------------> receive data ...");
                socket.receive(revPacket);
                byte[] realData = new byte[revPacket.getLength()];
                System.arraycopy(data, 0, realData, 0, realData.length);
                Log.e(TAG, "------------------------------> " + new String(realData));
            }
        } catch (Exception e) {
            e.printStackTrace();
            socket.close();
        }
    }


    /**
     * 发送数据
     *
     * @param msg
     */
    public void sendMsg(byte[] msg) {
        if (socket != null) {
            try {
                DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, InetAddress.getByName(IP), targetPort);
                System.out.println("---------------------- send  ---------------------");
                socket.send(sendPacket);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setReceive(boolean receive) {
        this.receive = receive;
    }

    public void close() {
        if (socket == null)
            return;
        socket.close();
    }

}
