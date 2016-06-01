package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jmack on 5/26/16.
 */
public class GeofenceTransitionsIntentService extends IntentService implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static int mId = 404;
    private NotificationManager mNotificationManager;

    private GoogleApiClient mGoogleApiClient;

    public GeofenceTransitionsIntentService() {
        super("Geofence");
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
    protected void onHandleIntent(Intent intent) {
        buildNotification();

        // After Geofence triggers we remove the geofence, it will be added again at midnight
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        List<Geofence> triggeredGeofence = event.getTriggeringGeofences();
        List<String> toRemove = new ArrayList<>();
        for (Geofence geofence : triggeredGeofence){
            toRemove.add(geofence.getRequestId());
        }
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, toRemove);
    }

    private void buildNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_explore_white_24dp)
                .setContentTitle(getString(R.string.popup_title))
                .setContentText(getString(R.string.popup_message))
                .addAction(
                        R.drawable.ic_done_black_24dp,
                        getString(R.string.popup_button),
                        sendSlackMessage()
                )
                .setAutoCancel(true)
                .setVibrate(new long[] {250, 1000, 250, 1000, 2000, 500});


        //mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    private PendingIntent sendSlackMessage(){
        Intent resultIntent = new Intent(this, SlackService.class);
        resultIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(mId));

        PendingIntent resultPendingIntent = PendingIntent.getService(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return resultPendingIntent;
    }
    @Override
    public void onDestroy(){
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
