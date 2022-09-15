package com.example.clockntp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clockntp.clock.ClockHandler;
import com.example.clockntp.debug.Logger;
import com.example.clockntp.ntp.CompoundConnectionListener;
import com.example.clockntp.ui.HostnamePropagator;
import com.example.clockntp.ui.NTPButton;
import com.example.clockntp.ui.HostField;
import com.example.clockntp.ui.UIClock;

public class MainActivity extends AppCompatActivity {

    private UIClock ui_clock;
    private HostField host_field;
    private NTPButton ntp_button;

    private TimePublisherMediator mediator = new TimePublisherMediator();
    private HostnamePropagator propagator = new HostnamePropagator();

    /*
    *   Bind UI and code
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.connectivity_manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        setContentView(R.layout.activity_main);

        /*
        *   create and bind ui elements
        * */
        createUIClock();
        createHostField();
        createButton();
        bindListeners();

        /*
        *   set ntp server and enable NTP
        * */
        mediator.setNTPHost("3.se.pool.ntp.org");
        mediator.enableNTP();
    }

    /*
    *   Initialize host_field
    * */
    private void createHostField() {
        host_field = new HostField(findViewById(R.id.host_input));
        host_field.setListener(propagator);
        host_field.setEnabledColor(getResources().getColor(R.color.primary));
        host_field.setDisabledColor(getResources().getColor(R.color.error));
    }

    /*
    *   Bind button press to function-call
    * */
    private void createButton() {
        Button button = findViewById(R.id.Connect_Button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toggleNTP();
                mediator.postTime(ui_clock);
            }
        });
        ntp_button = new NTPButton(button);
    }

    /*
    *   Center clock and
    * */
    private void createUIClock() {
        TextView clock_view = findViewById(R.id.UI_Clock);
        // Center Clock on screen
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        clock_view.setTranslationY(- getResources().getDimensionPixelSize(resourceId)/2.0f);
        // bind ui
        ui_clock = new UIClock(clock_view);
    }

    /*
    *   Bind listeners to subject
    * */
    private void bindListeners() {
        // bind hostname from host_input to mediator
        propagator.setMediator(mediator);

        // add host_field and ntp_button as mediator listeners
        CompoundConnectionListener compound = new CompoundConnectionListener();
        compound.addListener(host_field);
        compound.addListener(ntp_button);
        mediator.setListener(compound);

        // Add debug_view as log output
        Logger.getInstance().setOutput(findViewById(R.id.debug_view));

        /*
         *   get task clock, set mediator as TimePublisher
         *   and add ui_clock as TimeSubscriber
         * */
        ClockHandler clock_handler = ClockHandler.getInstance();
        clock_handler.setPublisher(mediator);
        clock_handler.setSubscriber(ui_clock);
    }

    /*
    *   Enable/Disable NTP as TimePublisher
    * */
    private void toggleNTP() {
        if (mediator.usesNTP()) mediator.disableNTP();
        else mediator.enableNTP();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private static ConnectivityManager connectivity_manager;
    public static ConnectivityManager getConnectivityManager() {
        return connectivity_manager;
    }

}