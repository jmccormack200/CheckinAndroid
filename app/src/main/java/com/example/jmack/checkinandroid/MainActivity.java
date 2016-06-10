package com.example.jmack.checkinandroid;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * The Main Activity displays the intrepid logo and initialises a repeating intent that
 * is sent immediately and then re-sends each night at midnight. This Intent will be sent to
 * The GeofenceIntent class.
 */

public class MainActivity extends AppCompatActivity {

    private static final long ONE_DAY_MS = TimeUnit.DAYS.toMillis(1);

    /**
     * Displays the activity_main.xml file and requests the appropriate permissions for the app.
     *
     * @param savedInstanceState The saved state of the Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Permission for Fine Location and Internet access
        ActivityCompat.requestPermissions(this, new String[] {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.INTERNET }, 10);
    }

    /**
     * When the user responds to the permission request, either the intent services are set up
     * or a toast is shown with an error message.
     *
     * @param requestCode  the code returned from the permission request
     * @param permissions  the list of permissions requested
     * @param grantResults the id of the permissions which indicates if it is granted or denied
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupGeoFenceIntents();
        } else {
            Toast.makeText(MainActivity.this,
                           getString(R.string.permission_error_msg),
                           Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Configures an initial Geofencing request in addition to
     * a reoccurring one each night at midnight.
     */
    public void setupGeoFenceIntents() {
        // We setup an intent and alarm manager so we can
        // initialize, then reset, the geofence every 24 hours.
        Calendar cal = Calendar.getInstance();
        cal = setCalToMidnight(cal);

        Intent intent = new Intent(this, GeoFenceIntent.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        // RTC is used with wakeup, everyday at midnight.
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP,
                           cal.getTimeInMillis(),
                           ONE_DAY_MS,
                           pendingIntent);

        //One instance of the intent is triggered immediately.
        startService(intent);
    }

    /**
     * This function returns a calendar object that is set to midnight of the following day.
     *
     * @param cal - The calendar object being configured.
     * @return The configured calendar object.
     */
    protected Calendar setCalToMidnight(Calendar cal) {
        //Sets the calendar to begin at midnight
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.AM_PM, Calendar.AM);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }
}
