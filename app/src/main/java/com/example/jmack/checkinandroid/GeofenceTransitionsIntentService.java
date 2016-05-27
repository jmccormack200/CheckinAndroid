package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by jmack on 5/26/16.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    private static int mId = 404;
    NotificationManager mNotificationManager;

    public GeofenceTransitionsIntentService() {
        super("Geofence");
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        buildNotification();
    }

    private void buildNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_explore_white_24dp)
                .setContentTitle("Welcome Back!")
                .setContentText("Looks like you're back at Intrepid. What to Check in?")
                .addAction(R.drawable.ic_done_black_24dp, "Check In", sendSlackMessage())
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
                PendingIntent.FLAG_CANCEL_CURRENT);
        return resultPendingIntent;
    }
    @Override
    public void onDestroy(){

    }
}
