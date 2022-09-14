package com.example.clockntp.ui;

import com.example.clockntp.TimePublisherMediator;
import com.example.clockntp.ui.TextListener;

public class HostnamePropagator implements TextListener {

    private TimePublisherMediator mediator = null;

    public void setMediator(TimePublisherMediator m) {
        mediator = m;
    }

    @Override
    public void onTextChanged(String str) {
        if (mediator != null) mediator.setNTPHost(str);
    }
}
