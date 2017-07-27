package com.example.cameron.fordapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {
    final static String TAG = "MAIN";



    TextView driving_button, last_ride_button, logs_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        driving_button = (TextView) findViewById(R.id.drive_pressed);

        driving_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                try {
                 //   Toast.makeText(MainActivity.this,
                   //         "driving was selected", Toast.LENGTH_SHORT).show();

                    //Intent intent = new Intent(MainActivity.this,DrivingSession.class);
                    //Testing new Driving class
                    Intent intent = new Intent(MainActivity.this, DosDriving.class);
                    startActivity(intent);

                } catch (Exception except) {
                    Log.e(TAG, "problem with driving button " + except.getMessage());
                }
            }
        });

        last_ride_button = (TextView) findViewById(R.id.last_ride);

        last_ride_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                try {
                  //  Toast.makeText(MainActivity.this,
                    //        "Last ride was selected", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, ViewLog.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    startActivity(intent);

                } catch (Exception except) {
                    Log.e(TAG, "problem with viewing last ride button " + except.getMessage());
                }
            }
        });

        logs_button = (TextView) findViewById(R.id.logs);

        logs_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                try {
               //     Toast.makeText(MainActivity.this,
                 //           "Logs was selected", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this,ViewPastDrives.class);
                    startActivity(intent);

                } catch (Exception except) {
                    Log.e(TAG, "problem with viewing the logs when it was selected " + except.getMessage());
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() {

        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }



}