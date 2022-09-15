package com.example.clockntp.ntp;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.Settings;

import com.example.clockntp.clock.TimePublisher;
import com.example.clockntp.clock.TimeSubscriber;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

public class NTPTimePublisher implements TimePublisher {

    private static final int NTP_PACKET_SIZE = 48;
    private static final int NTP_PORT = 123;

    private static final byte NTP_CLIENT_MODE = 3;
    private static final byte NTP_VERSION = 3;
    private static final byte NTP_HEADER_BYTE = NTP_CLIENT_MODE | (NTP_VERSION << 3) ;



    private String hostname = "";
    private boolean connected = true;

    private InetAddress address;
    private ConnectionListener listener;

    /*
    *   Validate hostname and get inetAddress asynchronously
    * */
    private class AsyncAdressRetriever extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                address = InetAddress.getByName(hostname);
                connected = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                connected = false;
            }
            if (listener == null) return null;
            notifyListener();
            return null;
        }
    }

    /*
    *   Set hostname and start async validation task
    * */
    public void setHostname(String host) {
        hostname = host;
        new AsyncAdressRetriever().execute();
    }

    /*
    *   Set connection listener
    * */
    public void setListener(ConnectionListener l) {
        listener = l;
    }

    /*
    *   Returns true if hostname is valid
    *   if not false, and if last message
    *   to inetAddress did not get a response
    *   false is also returned
    * */
    public boolean isConnected() {
        return connected;
    }

    /*
    *   Return current hostname
    * */
    public String getHostname() {
        return hostname;
    }

    /*
    *   Asynchronuous NTP call
    *   and posts calculated time
    *   to TimeSubscriber on response
    * */
    private class AsyncNTPPoster extends AsyncTask {

        private TimeSubscriber subscriber;

        public AsyncNTPPoster(TimeSubscriber sub) {
            subscriber = sub;
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            DatagramSocket socket = null;
            try {
                /*
                *   Initialize socket and set timeout
                * */
                socket = new DatagramSocket();
                socket.setSoTimeout(1000);

                /*
                *   Construct request packet
                * */
                byte[] buffer = new byte[NTP_PACKET_SIZE];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length, address, NTP_PORT);
                buffer[0] = NTP_HEADER_BYTE;

                /*
                *   Add transmit timestamp to packet
                *   and get reference tick
                * */
                long request_time = System.currentTimeMillis();
                long request_ticks = SystemClock.elapsedRealtime();
                NTPTimeStamp.writeTimeStamp(buffer, NTPTimeStamp.TRANSMIT_TIME_OFFSET, request_time);

                /*
                *   Send request
                * */
                socket.send(request);

                /*
                *   Construct response packet
                *   and wait for response
                * */
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                /*
                *   Get reference tick for response
                *   and calculate response time
                * */
                long response_ticks = SystemClock.elapsedRealtime();
                long response_time = request_time + (response_ticks - request_ticks);

                /*
                *   Extract timestamps from buffer
                * */
                long originate_time = NTPTimeStamp.readTimeStamp(buffer, NTPTimeStamp.ORIGINATE_TIME_OFFSET);
                long receive_time = NTPTimeStamp.readTimeStamp(buffer, NTPTimeStamp.RECEIVE_TIME_OFFSET);
                long transmit_time = NTPTimeStamp.readTimeStamp(buffer, NTPTimeStamp.TRANSMIT_TIME_OFFSET);

                /*
                *   Calculate time
                * */
                //long roundtrip_time = response_ticks - request_ticks - (transmit_time - receive_time);
                long clock_offset = ((receive_time - originate_time) + (transmit_time - response_time))/2;
                long ntp_time = response_time + clock_offset;

                /*
                *   Post time to TimeSubscriber
                * */
                subscriber.onTick(new Date(ntp_time));
                listener.onConnect(hostname);


            } catch (Exception e) {
                /*
                    Disable connection and notifyListener
                */
                e.printStackTrace();
                connected = false;
                notifyListener();
            } finally {
                /*
                *   Close socket
                * */
                if (socket != null) socket.close();
            }
            return null;
        }
    }

    /*
    *   Start async task to post time to sub
    * */
    @Override
    public void postTime(TimeSubscriber sub) {
        new AsyncNTPPoster(sub).execute();
    }

    /*
     *   Notify ConnectionListener if connected or not
     * */
    private void notifyListener() {
        if (listener == null) return;
        if (connected) listener.onConnect(hostname);
        else listener.onDisconnect();
    }
}
