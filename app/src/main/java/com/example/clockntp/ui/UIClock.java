package com.example.clockntp.ui;

import android.os.Handler;
import android.widget.TextView;

import com.example.clockntp.clock.TimeSubscriber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UIClock implements TimeSubscriber {

    private Handler handler = new Handler();
    private TextView clock_textview;
    private DateFormat dformat = new SimpleDateFormat("HH:mm");

    public UIClock(TextView view) {
        clock_textview = view;
    }

    /*
    * Set clock text on UI-thread
    * */
    @Override
    public void onTick(Date time) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                clock_textview.setText(dformat.format(time));
            }
        });
    }
}
