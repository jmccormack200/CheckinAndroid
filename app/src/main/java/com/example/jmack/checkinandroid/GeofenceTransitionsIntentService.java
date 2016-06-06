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

    private GoogleApiClient mGoogleApiClient;

    public GeofenceTransitionsIntentService() {
        super("Geofence");
    }

    /**
     * A connection to the GoogleApiClient is made
     * requesting the use of location services.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * We build the notification and then remove the geofence.
     *
     * @param intent The intent received from the previous service or activity.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        buildNotification();
        removeGeofence(intent);
    }

    /**
     * Removes the Geofence event attached to the intent
     *
     * @param intent The intent holding the Geofence event information.
     */
    public void removeGeofence(Intent intent) {
        // After Geofence triggers we remove the geofence, it will be added again at midnight
        GeofencingEvent event = GeofencingEvent.fromIntent(intent);
        List<Geofence> triggeredGeofence = event.getTriggeringGeofences();
        List<String> toRemove = new ArrayList<>();
        for (Geofence geofence : triggeredGeofence) {
            toRemove.add(geofence.getRequestId());
        }
        LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient, toRemove);
    }

    /**
     * Sets the popup notification with a vibration pattern, icon, and text.
     * The user can press a button to send an intent.
     */
    private void buildNotification() {
        NotificationCompat.Builder builder =
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
                        .setVibrate(new long[] { 250, 1000, 250, 1000, 2000, 500 });

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(mId, builder.build());
    }

    /**
     * Configures a pending intent which will trigger the Slack Service class.
     * The intent includes extra text containing the id of the notification displayed through
     * this class.
     *
     * @return The pending intent to start the Slack Service Class
     */
    private PendingIntent sendSlackMessage() {
        Intent resultIntent = new Intent(this, SlackService.class);
        resultIntent.putExtra(Intent.EXTRA_TEXT, String.valueOf(mId));

        return PendingIntent.getService(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
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
