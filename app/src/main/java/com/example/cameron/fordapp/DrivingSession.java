package com.example.cameron.fordapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.openxc.NoValueException;
import com.openxc.VehicleManager;
import com.openxc.measurements.AcceleratorPedalPosition;
import com.openxc.measurements.BrakePedalStatus;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleSpeed;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;


/**
 * Created by anita13benita on 11/16/16.
 */

/*
https://developer.android.com/training/implementing-navigation/ancestral.html#NavigateUp
https://developer.android.com/guide/topics/manifest/activity-element.html#lmode
 */

public class DrivingSession extends Activity {

    static final int ACCELERATION = 0;
    static final int DECELERATION = 1;

    TextView start_driving;
    Button button_log=null;
/*
passing information to the LOG class
OPENXC is used here/in this class
 */
    private VehicleManager mVehicleManager;
    private VehicleSpeed vSpeed;
    private EngineSpeed eSpeed;
    private AcceleratorPedalPosition accelPos;
    private BrakePedalStatus brakes;

    Double average;
    int successes,attempts;

    /**
     * Dates of the violations that occurred
     */
    ArrayList<Date> dates = new ArrayList<Date>();

    /**
     * Violations that occurred
     */
    ArrayList<String> violations = new ArrayList<String>();

    private static final String TAG = "DRIVING activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driving);

        final ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.drive);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        start_drive();
        stop_drive();
    }

    /**
     * Toggles on the drive to begin generating logs on violations until the drive is stopped
     */
    private void start_drive() {
        // Get a reference to a background container
        final LinearLayout bg = (LinearLayout) findViewById(R.id.driving_layout);

        start_driving = (ToggleButton) findViewById(R.id.driving_start);

        start_driving.setOnClickListener(new OnClickListener() {
            Fragment fr;
            public void onClick(View v) {

                // Toggle the Background color between a light and dark color
                if (start_driving.isSelected()) { //end drive
                    start_driving.setSelected(false);

                    bg.setBackgroundColor(Color.WHITE);
                    fr = new End_Drive_Fragment();
                    TextView t = (TextView)  findViewById(R.id.gradestring);
                    t.setText(getGrade());

                    button_log.setVisibility(View.VISIBLE);
                    button_log.setSystemUiVisibility(View.VISIBLE);

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.ended_driving_view,fr);
                    fragmentTransaction.commit();

                    //1.CALC

                    //2. adding a fragment view that will give the user Grade

                    //3. View full Summary - View Log ( save log or not )

                } else {
                    start_driving.setSelected(true); //start drive
                    bg.setBackgroundColor(0xFF000000);

                }
            }
        });
    }

    /**
     * Toggles off the drive.
     */
    private void stop_drive() {
        button_log = (Button)  findViewById(R.id.gradestring_button);

        if (button_log != null) {
            button_log.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewIn) {
                    try {
                      //  Toast.makeText(DrivingSession.this,
                        //        "Get Summary", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(DrivingSession.this, ViewLog.class);

                        // Pass over the information of the dates and violations that were committed
                        // during the duration of the drive
                        intent.putExtra("dates", dates);
                        intent.putExtra("rules", violations);

                        startActivity(intent);

                    } catch (Exception except) {
                        android.util.Log.e(TAG, "Get Summary " + except.getMessage());
                    }
                }
            });
        }
    }

    protected void onResume(Bundle savedInstanceState){
        super.onResume();
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    VehicleSpeed.Listener mVSpeedListener = new VehicleSpeed.Listener() {

        public void receive(Measurement meas) {
            vSpeed = (VehicleSpeed) meas;
            double speed = vSpeed.getValue().doubleValue();

            DrivingSession.this.runOnUiThread(new Runnable() {
                VehicleSpeed finalSpeed, initialSpeed;
                double length;
                @Override
                public void run() {
                    //Getting initial speed of car
                    try {
                        initialSpeed = (VehicleSpeed) mVehicleManager.get(VehicleSpeed.class);
                    } catch (UnrecognizedMeasurementTypeException e) {
                        e.printStackTrace();
                    } catch (NoValueException e) {
                        e.printStackTrace();
                    }

                    //Measuring how long car is decelerating for
                    while (brakes.getValue().booleanValue()) {
                        double length = brakes.getAge();

                    }

                    //Getting speed of vehicle after deceleration
                    try {
                        finalSpeed = (VehicleSpeed) mVehicleManager.get(VehicleSpeed.class);
                    } catch (UnrecognizedMeasurementTypeException e) {
                        e.printStackTrace();
                    } catch (NoValueException e) {
                        e.printStackTrace();
                    }

                    //Converting from milliseconds to hours
                    length = length /( 1000 * 60 * 60);
                    //Not really sure if math is right
                    if (Math.abs(finalSpeed.getValue().doubleValue() - initialSpeed.getValue().doubleValue()) / length > 54) {
                       // Toast.makeText(getApplicationContext(), "You are decelerating too quickly!", Toast.LENGTH_SHORT).show();
                        setLogMessage(DECELERATION);
                    }
                }
            });

        }
    };

    BrakePedalStatus.Listener mbrakeListener = new BrakePedalStatus.Listener() {

        public void receive (Measurement meas) {
            brakes = (BrakePedalStatus) meas;
        }
    };

    EngineSpeed.Listener mEngineSpeedListener = new EngineSpeed.Listener() {

        public void receive(Measurement meas) {
            eSpeed = (EngineSpeed) meas;
        }
    };

    AcceleratorPedalPosition.Listener mAccelListener = new AcceleratorPedalPosition.Listener() {

        public void receive(Measurement meas) {
            accelPos = (AcceleratorPedalPosition) meas;
            double position = accelPos.getValue().doubleValue();


            DrivingSession.this.runOnUiThread(new Runnable() {
                public void run() {
                    // Finally, we've got a new value and we're running on the
                    // UI thread
                    if(accelPos.getValue().doubleValue() > 40 && accelPos.getAge() < 5 && vSpeed.getValue().doubleValue() < 30){
                     //   Toast.makeText(getApplicationContext(), "You are accelerating too quickly!", Toast.LENGTH_SHORT).show();
                        attempts++;
                        setLogMessage(ACCELERATION);
                    }
                    else{
                        successes++;
                        attempts++;
                    }
                }
            });


        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {

        /** Actions performed when the connection is obtained. */
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
           // Log.i(TAG, "Bound to VehicleManager");
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            //add all of the listeners that are defined above in this class.
            mVehicleManager.addListener(EngineSpeed.class, mEngineSpeedListener);
            mVehicleManager.addListener(VehicleSpeed.class, mVSpeedListener);
            mVehicleManager.addListener(AcceleratorPedalPosition.class, mAccelListener);
            mVehicleManager.addListener(BrakePedalStatus.class, mbrakeListener);
            //mVehicleManager.addListener(Longitude.class, mLongListener);
        }

        /** Actions performed when the connection is broken. */
        public void onServiceDisconnected(ComponentName className) {
            //Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Records all logs when the listeners pick up on any violations.
     *
     * @param type
     */
    public void setLogMessage(int type){
        if (type == ACCELERATION) {
            dates.add(new Date());
            violations.add("You are accelerating too quickly!");
        } else if (type == DECELERATION) {
            dates.add(new Date());
            violations.add("You are decelerating too quickly!");
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /*
    Based on the results from the end of the ride.
    This will be for calculating, the number
    of times out of range etc
     */

    public String getGrade(){
        int grade;
        if(attempts==0){
            return "No driving data.";
        }
        else{
            grade = successes/attempts;
        }

        if(grade >= 90){
            return "Your grade is a "+grade+"%. You got an A! You are a very good driver.";
        }
        else if(grade < 90 && grade >= 80){
            return "Your grade is a "+grade+"%. You got a B! You are a good driver.";
        }
        else if(grade < 80 && grade >= 70){
            return "Your grade is a "+grade+"%. You got a C. You should try to drive less aggressively.";
        }
        else if(grade < 70 && grade >= 60){
            return "Your grade is a "+grade+"%. You got a D. You really need to improve you driving.";
        }
        else return "Your grade is a " + grade + "%. You got an F. " +
                    "You are accelerating too quickly when driving. " +
                    "You really need to change your driving habits.";


    }


}

