package com.example.cameron.fordapp;

import android.widget.Space;

import java.util.Date;
import java.util.Timer;

/**
 * Created by anita13benita on 11/17/16.
 *
 * Model of the Log
 */
public class Log {

    /**
     * ID of the Log
     */
    int id;

    /**
     * ID corresponding to which drive the log belongs to
     */
    long driveID;

    /**
     * Time when the Log was generated
     */
    private String date;

    /**
     * Violation made
     */
    private String violation;
    private double speed_count;

    /**
     *
     */
//    int acceleration;

//    int distance;

    /**
     * Public constructor for Log
     */
    public Log() {
    }
    /**
     * Public constructor for Log
     *
     * @param violation
     */
    public Log(String violation) {
        this.violation = violation;
    }

    /**
     * Public constructor for Log
     *
     * @param date
     * @param violation
     */
    public Log(String date, String violation, double speed) {
        this.date = date;
        this.violation = violation;
        this.speed_count = speed;
    }
    public double getSpeed() {
        return this.speed_count;
    }

    /**
     * Getter for Drive ID corresponding to the Log
     * @return
     */
    public long getDriveID() {
        return this.driveID;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }

    public void setSpeed(double speed_count) {

        this.speed_count=speed_count;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDriveID(long driveID) {
        this.driveID = driveID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setSpeed_count(int speed_count) {
        this.speed_count = speed_count;
    }
}