package com.example.jmack.checkinandroid;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {

    private static final String INTREPID_ID = "intrepidGeoFence";
    private static final double INTREPID_LAT = 42.367152;
    private static final double INTREPID_LONG = -71.080197;
    private static final float GEOFENCE_RADIUS_IN_METERS = 1000.0f;

    private ArrayList<Geofence> mGeofenceList = new ArrayList<>();

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart(){
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET},
                10);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startServiceButton(View view){
        Intent intent = new Intent(this, MapService.class);
        intent.putExtra(MapService.EXTRA_MESSAGE, "What's in a name");
        startService(intent);
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
    public void onResult(@NonNull Result result) {
        Log.v("Callback", "Callback");
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
            Log.v("-----------", e.getMessage());
        } catch (Exception e) {
            Log.v("This", "Other Error");
        }
        Log.v("This", "Location services connected.");
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Suspended", "Suspended");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Failed", "Failed");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.v("Tag", "Destroying");
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
}
