package com.example.clockntp.clock;

import java.util.Date;

public interface TimePublisher {
    void postTime(TimeSubscriber sub);
}
