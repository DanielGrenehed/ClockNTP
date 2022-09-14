package com.example.clockntp.ntp;

import java.util.ArrayList;

public class CompoundConnectionListener implements ConnectionListener {

    private ArrayList<ConnectionListener> listeners = new ArrayList<>();

    /*
    *
    *   ConnectionListener event aggregate
    *
    * */


    /*
    *   Propagate onConnect call to connection listeners
    * */
    @Override
    public void onConnect(String hostname) {
        for (ConnectionListener listener: listeners) listener.onConnect(hostname);
    }

    /*
    *   Propagate onDisconnect call to connection listeners
    * */
    @Override
    public void onDisconnect() {
        for (ConnectionListener listener: listeners) listener.onDisconnect();
    }

    /*
    *   Add connection listener
    * */
    public void addListener(ConnectionListener listener) {
        if (listener != this) listeners.add(listener);
    }

    /*
    *   Remove connection listener
    * */
    public void removeListener(ConnectionListener listener) {
        listeners.remove(listener);
    }
}
