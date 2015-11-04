package org.mti.hip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.mti.hip.model.Diagnosis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SuperActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super);
        getSupportActionBar().setSubtitle("Nakivale, " + getDateNowString());
    }

    public String getDateNowString() {
        return new SimpleDateFormat("dd-MMM-yyyy").format(new Date());
    }

}
