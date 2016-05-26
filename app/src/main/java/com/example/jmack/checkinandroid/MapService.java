package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by jmack on 5/25/16.
 */
public class MapService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = MapService.class.getSimpleName();


    public static final String EXTRA_MESSAGE = "temporary message";

    public MapService() {
        super("MapService");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();



        //TODO This is where we will set the interval to be 15 minutes

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v("Tag", "Destroying");
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;

        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                handleNewLocation(location);
            }
        } catch (SecurityException e){
            Log.v(TAG, e.getMessage());
        }

        Log.v(TAG, "Location services connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void handleNewLocation(Location location){

        double Lat = location.getLatitude();
        double Long = location.getLongitude();


        Log.v(TAG, "Latitude = " + String.valueOf(Lat));
        Log.v(TAG, "Longitude = " + String.valueOf(Long));
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


}
