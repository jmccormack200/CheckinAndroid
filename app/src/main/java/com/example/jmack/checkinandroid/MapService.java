package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by jmack on 5/25/16.
 */
public class MapService extends IntentService {

    private GoogleApiClient mGoogleApiClient;


    public static final String EXTRA_MESSAGE = "temporary message";
    Location mLastLocation;
    String mLatitudeText;
    String mLongitudeText;


    public MapService() {
        super("MapService");
    }




    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized(this){
            try {
                wait (100);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        showText(text);
    }

    private void showText(final String text){
        Log.v("DelayedMessageService", "The message is: " + text);
    }

}
