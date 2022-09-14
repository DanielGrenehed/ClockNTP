package com.example.clockntp.clock;


import android.os.Build;
import android.os.Handler;

import java.util.Date;


public class ClockHandler {


    private Handler handler = new Handler();
    private long interval_ms = 60000;
    private TimePublisher publisher = new SystemTimePublisher();
    private TimeSubscriber subscriber = null;
    private int counter = 0;

    /*
    *   Construct and schedule task for next minute
    * */
    private ClockHandler() {
        new Thread() {
            public void run() {
                // sync handler to minutes
                handler.postDelayed(repeated_task, millisToNextMinute());
                publisher.postTime(subscriber);
            }
        }.start();
    }

    /*
    *   Repeated postTime call with interval
    * */
    private Runnable repeated_task = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, interval_ms);
            publisher.postTime(subscriber);
        }
    };


    /*
    *   Set TimePublisher responsible for notifying
    *   TimeSubscriber of current time
    * */
    public void setPublisher(TimePublisher publisher) {
        this.publisher = publisher;
    }

    /*
    *   Set TimeSubscriber to get notified with
    *   current time at a set interval
    * */
    public void setSubscriber(TimeSubscriber sub) {
        subscriber = sub;
    }

    /*
    *   Returns the number of milliseconds from now until next minute
    * */
    private long millisToNextMinute() {
        Date now = new Date();
        Date future = new Date(now.getYear(), now.getMonth(), now.getDate(), now.getHours(), now.getMinutes()+1);

        return future.getTime() - now.getTime();
    }

    /*
    *   Shared singleton ClockHandler Object
    * */
    private static ClockHandler singleton = new ClockHandler();

    public static ClockHandler getInstance() {
        return singleton;
    }

}
