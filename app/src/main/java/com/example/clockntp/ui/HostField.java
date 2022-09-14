package com.example.clockntp.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.RequiresApi;

import com.example.clockntp.R;
import com.example.clockntp.ntp.ConnectionListener;


public class HostField implements ConnectionListener, TextWatcher {

    private EditText field;
    private Handler handler = new Handler();
    private TextListener listener = null;
    private int enabled_color = 0;
    private int disabled_color = 0;
    private int text_changed_flag = 0;

    public HostField(EditText view) {
        field = view;
        field.addTextChangedListener(this);
    }

    @Override
    public void onConnect(String hostname) {
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                if (!field.hasFocus()) {
                    text_changed_flag = 1;
                    field.setText(hostname);

                }
                field.setTextColor(enabled_color);
            }
        });

    }

    @Override
    public void onDisconnect() {
        handler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                field.setTextColor(disabled_color);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (text_changed_flag == 1) {
            text_changed_flag = 0;
            return;
        }
        if (field.hasFocus() && listener != null) {
            listener.onTextChanged(field.getText().toString());
        }
    }

    public void setListener(TextListener l) {
        listener = l;
    }

    public void setEnabledColor(int clr) {
        enabled_color = clr;
    }
    public void setDisabledColor(int clr) {
        disabled_color = clr;
    }
}

