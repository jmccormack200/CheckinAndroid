package com.example.jmack.checkinandroid;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onStart(){
        super.onStart();
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET},
                10);



        Intent intent = new Intent(this, GeoFenceIntent.class);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.add(Calendar.DAY_OF_MONTH, 1);

        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                cal.getTimeInMillis(),
                1000*60*60*24,
                pintent);

        startService(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }





}
