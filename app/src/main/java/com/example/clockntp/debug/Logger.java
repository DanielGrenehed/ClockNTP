package com.example.clockntp.debug;

import android.widget.TextView;

public class Logger {

    TextView output = null;

    /*
    *   Set log output view
    * */
    public void setOutput(TextView out) {
        output = out;
    }

    /*
    *   Log string
    * */
    public void log(String str) {
        if (output != null) output.setText(str);
    }

    /*
    *   Log exception
    * */
    public void log(Exception e) {
        if (output != null) output.setText(e.getClass().toString() + " " + e.getStackTrace().toString() + " " + e.getLocalizedMessage());
    }

    /*
    *   Log exception with mesage tag
    * */
    public void log(Exception e, String msg) {
        if (output != null) output.setText(msg+' '+e.getClass().toString() + " " + e.getStackTrace().toString() + " " + e.getLocalizedMessage());
    }

    /*
    *   Shared singleton Logger object
    * */
    private static Logger logger = new Logger();

    public static Logger getInstance() {
        return logger;
    }

    /*
    *   Log error with tag to shared log
    * */
    public static void error(Exception e, String str) {
        logger.log(e, str);
    }

    /*
    *   Log message to shared log
    * */
    public static void message(String str) {
        logger.log(str);
    }

}
