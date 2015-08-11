package me.app.template;


import android.app.Application;

import com.parse.Parse;

public class AppInit extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parse_application_ID), getString(R.string.parse_client_key));
    }
}