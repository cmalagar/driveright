package com.example.cameron.fordapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.*;
import org.json.JSONObject;
import 	org.json.JSONArray;
import 	org.json.JSONObject;
import 	org.json.*;
import android.app.Activity;
import android.app.Fragment;

import java.math.MathContext;
import  	java.text.DecimalFormat;
import java.text.DateFormat;
import android.view.View;
import 	java.text.NumberFormat;
import android.widget.Button;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.View.OnClickListener;
import android.widget.SimpleCursorAdapter;

import android.content.Context;
import android.util.Log;import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import
        java.lang.Object;
import android.view.MenuItem;
import android.view.View;
import java.text.DateFormat;
import 	java.util.Calendar;
import java.text.SimpleDateFormat;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Object;
import 	org.json.JSONArray;
import 	org.json.JSONObject;
import 	org.json.*;
/**
 * Created by Cameron on 11/16/2016.
 *
 * The ViewLog activity shows the user the generated logs during the duration of their last drive.
 */
public class ViewLog extends ListActivity {
    private static final String TAG = "ViewLog";
    private LogsAdapter mAdapter;
    ArrayList<Date> dates;
    ArrayList<String> violations;
    ArrayList<String> logs_list;
    String str_Date = "";
    private int REQUEST_SAVE_INFO = 2;
    ArrayList<String> templist = new ArrayList<String>();
   int total_acc = 0 ;
    int total_dec = 0 ;
    String date = "" ;
    String startofdrive;
    long totaltime=0;

    private LogsDataSource logsDataSource;
    private CommentsDataSource datasource;
    JSONArray jArray = new JSONArray();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        android.util.Log.i(TAG, "entering ViewLog");
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        datasource = new CommentsDataSource(this);
        logsDataSource = new LogsDataSource(this);
        TextView footerView = (TextView)getLayoutInflater().inflate(R.layout.footer, null);
        TextView headerView = (TextView)getLayoutInflater().inflate(R.layout.savelogbutton, null);

        // Creates Logs using the corresponding dates and violations, and adds them to the adapter
        Intent intent = this.getIntent();
        if(intent.getFlags()==Intent.FLAG_ACTIVITY_MULTIPLE_TASK) {
        //    Toast.makeText(ViewLog.this,
          //          "You call from Maine", Toast.LENGTH_SHORT).show();

            getActionBar().setDisplayHomeAsUpEnabled(true);

            lv.addHeaderView(headerView);

            mAdapter = new LogsAdapter(getApplicationContext());

            logsDataSource.open();
            List<com.example.cameron.fordapp.Log> logs = logsDataSource.getLastDriveLogs();
            logsDataSource.close();

            for (com.example.cameron.fordapp.Log log : logs) {
                mAdapter.add(log);
            }

            lv.setAdapter(mAdapter);
        }else {
            final String jsonArray = intent.getStringExtra("jsonArray");

            if (null == footerView) {
                return;
            }
            if (jsonArray != null) {
                mAdapter = new LogsAdapter(getApplicationContext());
                try {

                    str_Date = intent.getStringExtra("dateofdrive");
                    //startofdrive = new Date(intent.getExtras().getString("startofdrive"));

                    totaltime = intent.getLongExtra("totaltime",0);

                    jArray = new JSONArray(jsonArray);
                    for (int i = 0; i < jArray.length(); i++) {

                        JSONObject temp = jArray.getJSONObject(i);

                        String date = temp.getString("time");

                        String v = temp.getString("violation");
                        if (v.toString().compareTo("Accelerating too quickly") == 0) {
                            total_acc++;
                        } else if (v.toString().compareTo("Decelerating too quickly") == 0) {
                            total_dec++;
                        }
                        Double s = temp.getDouble("speed");

                        com.example.cameron.fordapp.Log log1 =
                                new com.example.cameron.fordapp.Log
                                        (date, v, s);

                        mAdapter.add(log1);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            lv.addHeaderView(headerView);
            lv.addFooterView(footerView);

            footerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int driveID;
                    NumberFormat formatter = new DecimalFormat("#0.0");
                    long secondsInMilli = 1000;
                    long minutesInMilli = secondsInMilli * 60;
                    long hoursInMilli = minutesInMilli * 60;
                    StringBuilder temp = new StringBuilder();

                    long elapsedHours = totaltime / hoursInMilli;
                    totaltime  = totaltime %  hoursInMilli;
                     if(elapsedHours>0){
                         String t = formatter.format((totaltime) /3600000d);
                         temp.append(t +"hr");

                     }
                    long elapsedMinutes = totaltime   / minutesInMilli;
                    totaltime  = totaltime  % minutesInMilli;
                    if(elapsedMinutes>0){
                        String t = formatter.format((totaltime) /60000d);
                        temp.append(t +"m ") ;

                    }
                    long elapsedSeconds = totaltime  / secondsInMilli;
                    if(elapsedSeconds>0){
                        String t = formatter.format((totaltime) /1000d);
                        temp.append(new String(t) +"s") ;

                    }
                    Intent i = new Intent(ViewLog.this, ViewPastDrives.class);

                    datasource.open();
                    StringBuilder stringtoplace = new StringBuilder();
                    String temp_first = str_Date.substring(0,12);
                    String temp_end = str_Date.substring(12,17);
                    stringtoplace.append( temp_first );
                    stringtoplace.append( temp_end );
                    if(total_dec==0&&total_acc==0){
                        stringtoplace.append("\n");
                        stringtoplace.append("No Violations!");
                    }else if(total_dec==0){
                        stringtoplace.append("\n"+"Total Violations:"+"\n");
                        stringtoplace.append("\nAccelerating -> " + total_acc);
                    }else if(total_acc==0){
                        stringtoplace.append("\n"+"Total Violations:"+"\n");
                        stringtoplace.append("\nDecelerating -> " + total_dec);
                    }else{
                       // stringtoplace.append("Total Violations:"+"\n");
                        stringtoplace.append("\n"+"Total Violations:"+"\n");
                        stringtoplace.append("\nAccelerating -> " + total_acc);
                        stringtoplace.append("\nDecelerating -> " + total_dec);
                    }

                    Comment currComment = datasource.createComment( stringtoplace.toString() );

                    driveID = (int) currComment.getId();

                    datasource.close();

                    logsDataSource.open();

                    for (int j = 0; j < mAdapter.getCount(); j++) {
                        com.example.cameron.fordapp.Log log = (com.example.cameron.fordapp.Log) mAdapter.getItem(j);
                        log.setDriveID(driveID);
                        System.out.println(log.getSpeed());
                        logsDataSource.createLog(log);
                    }

                    logsDataSource.close();

                    startActivity(i);
                }
            });

            getActionBar().setDisplayHomeAsUpEnabled(true);

            lv.setAdapter(mAdapter);


        }
    }

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

}