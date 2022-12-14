package com.example.clockntp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;

import com.example.clockntp.clock.SystemTimePublisher;
import com.example.clockntp.clock.TimePublisher;
import com.example.clockntp.clock.TimeSubscriber;
import com.example.clockntp.ntp.ConnectionListener;
import com.example.clockntp.ntp.NTPTimePublisher;


public class TimePublisherMediator implements TimePublisher, ConnectionListener {

    private boolean use_ntp = false;
    private boolean connected = false;

    private TimePublisher system_time = new SystemTimePublisher();
    private NTPTimePublisher ntp_time = new NTPTimePublisher();
    private ConnectionListener listener;
    private TimeSubscriber last_subscriber;

    public TimePublisherMediator() {
        /*
        *   Subscribe to NTP connection events
        * */
        ntp_time.setListener(this);
    }

    /*
    *   Delegate postTime call to NTP or system clock
    * */
    @Override
    public void postTime(TimeSubscriber subscriber) {
        last_subscriber = subscriber;
        if (use_ntp) {
            checkNetworkConnection();
            if (connected) {
                ntp_time.postTime(subscriber);
                return;
            }
        }
        system_time.postTime(subscriber);
    }

    public void enableNTP() {
        setUseNTP(true);
    }

    public void disableNTP() {
        setUseNTP(false);
    }


    /*
    *   Set hostname of NTP server
    * */
    public void setNTPHost(String host) {
        ntp_time.setHostname(host);
    }

    /*
    *   Enables or Disables NTP as clock source
    *   and notifies connectionListener
    * */
    private void setUseNTP(boolean on) {
        if (on) use_ntp = true;
        else use_ntp = false;
        notifyListener();
    }

    /*
    *   Notify connection listener whether NTP is in use or not
    * */
    private void notifyListener() {
        if (listener == null) return;
        if (use_ntp && connected) listener.onConnect(ntp_time.getHostname());
        else listener.onDisconnect();
    }

    /*
    *   Set connection-listener to receive calls
    *   when NTP goes in our out of use
    * */
    public void setListener(ConnectionListener l) {
        listener = l;
    }

    public boolean usesNTP() {
        return use_ntp;
    }


    private void checkNetworkConnection() {
        NetworkInfo network = MainActivity.getConnectivityManager().getActiveNetworkInfo();
        connected = network == null ? false : network.isConnected();
        notifyListener();
    }

    /*
    * Forward NTPTimePublisher connect to listener
    * if NTP is in use
    * */
    @Override
    public void onConnect(String hostname) {
        if (use_ntp) listener.onConnect(hostname);
    }

    /*
    * Forward NTPTimePublisher disconnect to listener
    * if NTP is in use
    * */
    @Override
    public void onDisconnect() {
        if (use_ntp) {
            listener.onDisconnect();

            /*
            * Post system-time to last subscriber
            * connection lost, ntp_time failed to
            * postTime to subscriber.
            * */
            if (last_subscriber != null) system_time.postTime(last_subscriber);
        }
    }
}
