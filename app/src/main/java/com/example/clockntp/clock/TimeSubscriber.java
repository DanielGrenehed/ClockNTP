package com.example.clockntp.clock;

import java.util.Date;

public interface TimeSubscriber {

    void onTick(Date time);
}
