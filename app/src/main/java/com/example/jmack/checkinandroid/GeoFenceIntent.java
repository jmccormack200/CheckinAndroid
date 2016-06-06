package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by jmack on 5/27/16.
 */
public class GeoFenceIntent extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {


    private final String LOG_TAG = this.getClass().getSimpleName();


    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();

    public GeoFenceIntent() {
        super("GeoFenceIntent");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        //At start we initialize the Google API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;
        // Once the Google API Connects, we register the geofence

        addToGeoFenceList();

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    getGeofencingRequest(),
                    getGeofencePendingIntent()
            ).setResultCallback(this);
        } catch (SecurityException e ){
            Log.v(LOG_TAG, e.getMessage());
        } catch (Exception e) {
            Log.v(LOG_TAG, e.getMessage());
        }
        Log.v(LOG_TAG, "Location services connected.");
    }


    private GeofencingRequest getGeofencingRequest(){
        // Add the geofencing list to the geofence builder
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        // Create a pending intent that fires when we cross into the geofence.
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy(){
        // On exit we simply disconnect from the Google API
        super.onDestroy();
        Log.v(LOG_TAG, "Destroying");
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }


    public void addToGeoFenceList(){
        // Additional Geofences should be added here.
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(LocationConstants.INTREPID_ID)
                .setCircularRegion(
                        LocationConstants.INTREPID_LAT,
                        LocationConstants.INTREPID_LONG,
                        LocationConstants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
    }

    @Override
    public void onResult(@NonNull Result result) {
        //Any Callbacks we need to run can be added here
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(LOG_TAG, "Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(LOG_TAG, "Failed");
    }

}
