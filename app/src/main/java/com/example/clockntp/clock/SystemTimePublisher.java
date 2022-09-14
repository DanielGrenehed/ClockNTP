package com.example.clockntp.clock;

import java.util.Calendar;
import java.util.Date;

public class SystemTimePublisher implements TimePublisher {

    /*
    *   Notify TimeSubscriber of current time
    *   according current system time
    * */
    @Override
    public void postTime(TimeSubscriber subscriber) {
        subscriber.onTick(Calendar.getInstance().getTime());
    }
}
