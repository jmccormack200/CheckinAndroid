package com.example.jmack.checkinandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    private static int ONE_DAY = 1000 * 60 * 60 * 24;

    @Override
    protected void onStart(){
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Permission for Fine Location and Internet access
        ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.INTERNET},
                10);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String perimssions[],
                                           int[] grantResults){
        if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // We setup an intent and alarm manager so we can
            // initialize, then reset, the geofence every 24 hours.
            Intent intent = new Intent(this, GeoFenceIntent.class);
            Calendar cal = Calendar.getInstance();

            cal = setCalToMidnight(cal);

            PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

            // RTC is used with wakeup, everyday at midnight.
            AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    ONE_DAY,
                    pintent);

            startService(intent);
        }

    }

    /**
     * This function returns a calendar object that is set to midnight of the following day.
     * @param cal - The calendar object being configured.
     * @return The configured calendar object.
     */
    protected Calendar setCalToMidnight(Calendar cal){
        //Sets the calendar to begin at midnight
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }
}
