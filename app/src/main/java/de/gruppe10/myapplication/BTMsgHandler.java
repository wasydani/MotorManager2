package de.gruppe10.myapplication;

import android.os.Handler;

import java.nio.charset.StandardCharsets;

public abstract class BTMsgHandler extends Handler {

    abstract void receiveMessage(String msg);

    abstract void receiveConnectStatus(boolean isConnected);

    abstract void handleException(Exception e);


    public void handleMessage(android.os.Message msg) {

        if (msg.what == BTManager.MESSAGE_READ) {
            String readMessage = null;
            readMessage = new String((byte[]) msg.obj, StandardCharsets.UTF_8);
            receiveMessage(readMessage);
        }

        if (msg.what == BTManager.CONNECTING_STATUS) {

            if (msg.arg1 == 1)
                //Connected to device
                receiveConnectStatus(true);
            else {
                //Connection failed
                receiveConnectStatus(false);
                handleException(new Exception("Connection Failed"));
            }

        }
    }

}
