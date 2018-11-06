package com.yuong.udpa;

import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by yuandong on 2018/11/5.
 */

public class SendThread extends Thread {
    private static String TAG = SendThread.class.getSimpleName();

    private Queue<byte[]> sendMsgQueue = new LinkedList<>();

    private ReceiverThread receiverThread;

    private boolean send = true;

    public void setSend(boolean send) {
        this.send = send;
    }

    public SendThread(ReceiverThread receiverThread) {
        this.receiverThread = receiverThread;
    }


    public synchronized void putMsg(byte[] msg) {
        // 唤醒线程
        if (sendMsgQueue.size() == 0)
            notify();
        sendMsgQueue.offer(msg);
    }


    @Override
    public void run() {
        Log.e(TAG, "----------------- start to send data ....");

        synchronized (this) {
            while (send) {
                // 当队列里的消息发送完毕后，线程等待
                while (sendMsgQueue.size() > 0) {
                    byte[] msg = sendMsgQueue.poll();
                    Log.e(TAG, "----------------- send data : "+new String(msg));
                    if (receiverThread != null)
                        receiverThread.sendMsg(msg);
                }
                try {
                    Log.e(TAG, "----------------- no data to send and  to wait");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
