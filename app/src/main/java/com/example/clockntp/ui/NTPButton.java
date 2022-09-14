package com.example.clockntp.ui;

import android.os.Handler;
import android.widget.Button;

import com.example.clockntp.ntp.ConnectionListener;

import java.net.InetAddress;

public class NTPButton implements ConnectionListener {

    private Button connect_button;
    private Handler handler = new Handler();

    public NTPButton(Button button) {
        connect_button = button;
    }

    @Override
    public void onConnect(String hostname) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                connect_button.setText("Disconnect");
            }
        });

    }

    @Override
    public void onDisconnect() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                connect_button.setText("Connect");
            }
        });
    }
}
