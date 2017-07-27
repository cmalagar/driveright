package com.example.cameron.fordapp;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.openxc.VehicleManager;
import com.openxc.measurements.AcceleratorPedalPosition;
import com.openxc.measurements.BrakePedalStatus;
import com.openxc.measurements.EngineSpeed;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleSpeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

//import java.util.logging.Handler;

public class DosDriving extends Activity {
    private static final String TAG = "Drive In Session";

    private VehicleManager mVehicleManager;
    private TextView mEngineSpeedView;
    private TextView mAcceleratorPedalView;
    private TextView mVehicleSpeedView;
    private TextView mBrakePedalView;

    private static AcceleratorPedalPosition accel;
    private static EngineSpeed speed;
    private static VehicleSpeed vehicleSpeed;
    private static BrakePedalStatus brakePedal;

    private static long speedBreakTime = 0;
    private static long engBreakTime = 0;
    private static long accelBreakTime = 0;
    private int errorMargin = 10000;
    double elapsedTime = 0;
    Button  mSaveLogsView;
    ArrayList<Log> logs = new ArrayList<Log>();
    ArrayList<String> lesing = new ArrayList<String>();
    JSONArray jsonArray = new JSONArray();
    Fragment fr;
    Vibrator vibrator;
    long startTime,endTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.driving_new);
        setTitle("Drive In Session");

        mEngineSpeedView = (TextView) findViewById(R.id.engine_speed);
        mAcceleratorPedalView = (TextView) findViewById(R.id.accel_pos);
        mVehicleSpeedView = (TextView) findViewById(R.id.veh_speed);
        mBrakePedalView = (TextView) findViewById(R.id.brake_pedal);
        mSaveLogsView = (Button) findViewById(R.id._dos_save);
        vibrator =  (Vibrator) getSystemService(VIBRATOR_SERVICE);
        startTime = System.currentTimeMillis();

        //final LinearLayout bg = (LinearLayout) findViewById(R.id.driving_layout);

        mSaveLogsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View viewIn) {
                try {
                   // Toast.makeText(DosDriving.this,
                     //       "displayinfo", Toast.LENGTH_SHORT).show();

                    endTime   = System.currentTimeMillis();
                    long totalTime = endTime - startTime;
                    Intent intent = new Intent(DosDriving.this, ViewLog.class);
                    //intent.putExtra("list",lesing);
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
                    //SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                    String formattedDate = df.format(c.getTime());//DateFormat.getDateTimeInstance().format(new Date());//df.format(c.getTime());
                    intent.putExtra("dateofdrive",formattedDate);
                   // intent.putExtra("startofdrive",startTime);
                    intent.putExtra("totaltime",totalTime);
                    intent.putExtra("jsonArray", jsonArray.toString());

                    startActivity(intent);
                } catch (Exception except) {
                    android.util.Log.e(TAG, "passing " + except.getMessage());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        // When the activity goes into the background or exits, we want to make
        // sure to unbind from the service to avoid leaking memory
        if(mVehicleManager != null) {
          //  android.util.Log.i(TAG, "Unbinding from Vehicle Manager");
            // Remember to remove your listeners, in typical Android
            // fashion.
            mVehicleManager.removeListener(EngineSpeed.class,
                    mSpeedListener);
            mVehicleManager.removeListener(AcceleratorPedalPosition.class,
                    mPedalListener);
            mVehicleManager.removeListener(VehicleSpeed.class,
                    mVehListener);
            mVehicleManager.removeListener(BrakePedalStatus.class,
                    mBrakePedalListener);
            unbindService(mConnection);
            mVehicleManager = null;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // When the activity starts up or returns from the background,
        // re-connect to the VehicleManager so we can receive updates.
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /* This is an OpenXC measurement listener object - the type is recognized
     * by the VehicleManager as something that can receive measurement updates.
     * Later in the file, we'll ask the VehicleManager to call the receive()
     * function here whenever a new EngineSpeed value arrives.
     */
    AcceleratorPedalPosition.Listener mPedalListener = new AcceleratorPedalPosition.Listener() {
        @Override
        public void receive(Measurement measurement) {

            accel = (AcceleratorPedalPosition) measurement;

            DosDriving.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAcceleratorPedalView.setText("Accel Pedal: " +
                            accel.getValue().doubleValue());
                    if (SystemClock.elapsedRealtime() > accelBreakTime + errorMargin) {
                        if (accel.getValue().doubleValue() > 33 && accel.getAge() < 5) {
                            setAccelBreakTime();
                            try {

                                Date now = new Date();

                                DateFormat df = new SimpleDateFormat("h:mm:ss a");
                                String date = df.format(now);
                                Toast.makeText(getApplicationContext(), "You are accelerating too quickly!", Toast.LENGTH_SHORT).show();
                                JSONObject log_issue = new JSONObject();
                                log_issue.put("time", date);
                                log_issue.put("violation", "Accelerating too quickly");
                                log_issue.put("speed", vehicleSpeed.getValue().intValue());
                                jsonArray.put(log_issue);
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            vibrator.vibrate(1000);
                           // Toast.makeText(getApplicationContext(), "You are accelerating too quickly!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
            });
        }
    };

    EngineSpeed.Listener mSpeedListener = new EngineSpeed.Listener() {
        @Override
        public void receive(Measurement measurement) {
            // When we receive a new EngineSpeed value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an EngineSpeed.
            speed = (EngineSpeed) measurement;
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            DosDriving.this.runOnUiThread(new Runnable() {
                public void run() {
                    // Finally, we've got a new value and we're running on the
                    // UI thread - we set the text of the EngineSpeed view to
                    // the latest value
                    mEngineSpeedView.setText("Engine speed (RPM): "
                            + speed.getValue().doubleValue());

                    final VehicleSpeed initialSpeed;
                    final VehicleSpeed[] finalSpeed = new VehicleSpeed[1];
                    //boolean toggle = false;
                    initialSpeed = vehicleSpeed;
                    if (SystemClock.elapsedRealtime() > speedBreakTime + errorMargin) {
                        if (brakePedal != null) {
                            if (brakePedal.getValue().booleanValue()) {
                                Handler h = new Handler();
                                h.postAtTime(new Runnable() {
                                    boolean toggle = false;
                                    @Override
                                    public void run() {
                                        finalSpeed[0] = vehicleSpeed;
                                        toggle = calculateDeceleration(initialSpeed, finalSpeed[0]);
                                        if (toggle) {
                                            setVehicleSpeedBreakTime();
                                            //  Toast.makeText(getApplicationContext(), "Deceleration Time: " + speedBreakTime, Toast.LENGTH_SHORT).show();
                                            vibrator.vibrate(1000);
                                            Toast.makeText(getApplicationContext(), "You are decelerating too quickly!", Toast.LENGTH_SHORT).show();
                                            try {
                                                Date now = new Date();
                                                DateFormat df = new SimpleDateFormat("h:mm:ss a");
                                                String date = df.format(now);
                                                JSONObject log_issue = new JSONObject();
                                                log_issue.put("time", date);
                                                log_issue.put("violation", "Decelerating too quickly");
                                                log_issue.put("speed", vehicleSpeed.getValue().intValue());
                                                jsonArray.put(log_issue);
                                            } catch (JSONException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }, 2000);

                            }
                        }
                    }

                }
            });
        }
    };

    public boolean calculateDeceleration(VehicleSpeed init, VehicleSpeed finalS) {
        final double initial = init.getValue().doubleValue();
        final double f = finalS.getValue().doubleValue();

        double elapsed = (2000)/ (1000 * 60 * 60);

        return ((Math.abs(f - initial) / elapsed) > 54);
    }

    VehicleSpeed.Listener mVehListener = new VehicleSpeed.Listener() {
        @Override
        public void receive(Measurement measurement) {

            vehicleSpeed = (VehicleSpeed) measurement;

            DosDriving.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVehicleSpeedView.setText("Vehicle Speed (KPH): " +
                            vehicleSpeed.getValue());

                }


            });
        }
    };

    BrakePedalStatus.Listener mBrakePedalListener = new BrakePedalStatus.Listener() {
        @Override
        public void receive(Measurement measurement) {

          brakePedal = (BrakePedalStatus) measurement;
          DosDriving.this.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                  mBrakePedalView.setText("Brakes: " + brakePedal);


              }
          });

        }
    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is
        // established, i.e. bound.
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            android.util.Log.i(TAG, "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.Listener (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            mVehicleManager.addListener(EngineSpeed.class, mSpeedListener);
            mVehicleManager.addListener(AcceleratorPedalPosition.class, mPedalListener);
            mVehicleManager.addListener(VehicleSpeed.class, mVehListener);
            mVehicleManager.addListener(BrakePedalStatus.class, mBrakePedalListener);

        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
          //  android.util.Log.w(TAG, "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

    public AcceleratorPedalPosition getAccel() {
        return accel;
    }

    public EngineSpeed getSpeed() {
        return speed;
    }

    public VehicleSpeed getVehicleSpeed() {
        return vehicleSpeed;
    }

    public BrakePedalStatus getBrakePedal() {
        return brakePedal;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.top_menu, menu);
//        return true;
//    }

    public static void setAccelBreakTime() {
        accelBreakTime = SystemClock.elapsedRealtime();
    }

    public static void setVehicleSpeedBreakTime() {
        speedBreakTime = SystemClock.elapsedRealtime();
    }

}