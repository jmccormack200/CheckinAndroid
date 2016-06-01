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

    private static final String INTREPID_ID = "intrepidGeoFence";
    private static final double INTREPID_LAT = 42.367152;
    private static final double INTREPID_LONG = -71.080197;
    private static final float GEOFENCE_RADIUS_IN_METERS = 50.0f;

    private final String LOG_TAG = this.getClass().getSimpleName();


    private GoogleApiClient mGoogleApiClient;
    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();

    public GeoFenceIntent() {
        super("GeoFenceIntent");
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
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = null;

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
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent(){
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v(LOG_TAG, "Destroying");
        if (mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }


    public void addToGeoFenceList(){
        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(INTREPID_ID)
                .setCircularRegion(INTREPID_LAT, INTREPID_LONG, GEOFENCE_RADIUS_IN_METERS)
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
