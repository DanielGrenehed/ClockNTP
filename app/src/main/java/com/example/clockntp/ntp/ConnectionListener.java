package com.example.clockntp.ntp;

public interface ConnectionListener {

    void onConnect(String hostname);
    void onDisconnect();

}
