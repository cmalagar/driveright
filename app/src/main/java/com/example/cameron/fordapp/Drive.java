package com.example.cameron.fordapp;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Cameron on 11/29/2016.
 */

public class Drive {

    Date date;
    String overallGrade;
    ArrayList<Log> logs;

    public Drive(String overallGrade) {
        this.date = new Date();
        this.overallGrade = overallGrade;
        this.logs = new ArrayList<Log>();
    }

}
