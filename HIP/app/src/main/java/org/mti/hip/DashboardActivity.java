package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.mti.hip.model.Visit;

import java.util.Date;

public class DashboardActivity extends SuperActivity {

    private int backPressCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.bt_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


        if (getIntent().getStringExtra(EXTRA_MSG) != null) {
            Toast.makeText(this, getIntent().getStringExtra(EXTRA_MSG), Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.new_visit).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Visit visit = getStorageManagerInstance().newVisit();
                visit.setVisitDate(new Date());
                visit.setStaffMemberName(currentUserName);
                visit.setDeviceId("MAC");
                visit.setFacilityName(facilityName);
                startActivity(new Intent(DashboardActivity.this, DemographicEntryActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;
    }

    @Override
    public void onBackPressed() {
        backPressCount++;
        Toast.makeText(DashboardActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        if (backPressCount == 2) {
            super.onBackPressed();
        }
        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                backPressCount = 0;
            }
        }.start();
    }
}
