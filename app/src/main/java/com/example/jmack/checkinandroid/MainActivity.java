package com.example.jmack.checkinandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startServiceButton(View view){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                10);
        Intent intent = new Intent(this, MapService.class);
        intent.putExtra(MapService.EXTRA_MESSAGE, "What's in a name");
        startService(intent);
    }
}
