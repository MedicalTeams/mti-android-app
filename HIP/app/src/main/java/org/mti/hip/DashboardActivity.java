package org.mti.hip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mti.hip.model.Tally;
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
                Intent i = new Intent(DashboardActivity.this, LocationSelectionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


        if (getIntent().getStringExtra(EXTRA_MSG) != null) {
            Toast.makeText(this, getIntent().getStringExtra(EXTRA_MSG), Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.new_visit).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Visit visit = getStorageManagerInstance().newVisit();
                visit.setVisitDate(new Date());
                visit.setStaffMemberName(currentUserName);
                visit.setDeviceId("MAC");
                visit.setFacilityName(facilityName);
                visit.setFacility(readLastUsedFacility());
                startActivity(new Intent(DashboardActivity.this, ConsultationActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;
        TextView connectivityStatus = (TextView) findViewById(R.id.dashboard_connectivity_status);
        if (isConnected()) {
            connectivityStatus.setText(R.string.is_online);
            connectivityStatus.setTextColor(Color.GREEN);
        } else {
            connectivityStatus.setText(R.string.is_offline);
            connectivityStatus.setTextColor(Color.RED);
        }
        // make tally string from file

        // Rearrange to make this not overwrite the old one every time!
        String tallyJsonIn = getStorageManagerInstance().readTallyToJsonString(this);

        // TODO delete Tally from disk after 7 days?

        if(tallyJsonIn != null) {
            // make object from string
            Tally tally = (Tally) getJsonManagerInstance().read(tallyJsonIn, Tally.class);
            getStorageManagerInstance().setTally(tally);
            if (!tally.isEmpty()) {
                writeTallyToDisk(tally);
            }
        } else { // no tally stored on disk so make a new one
            getStorageManagerInstance().setTally(new Tally());
        }


    }

    private void writeTallyToDisk(Tally tally) {

        // TODO refactor this is messy (also... App sends "isSent" to the server and this should eventually be removed but will require some other serialization method

        // TODO deal with isSent
        // make tally string
        String tallyJsonOut = getJsonManagerInstance().writeValueAsString(tally);

        // write to file
        getStorageManagerInstance().writeTallyJsonToFile(tallyJsonOut, this);

        // make tally string from file
        String tallyJsonIn = getStorageManagerInstance().readTallyToJsonString(this);

        // make object from string
        Tally tallyFromJson = (Tally) getJsonManagerInstance().read(tallyJsonIn, Tally.class);
        int sent = 0;
        int total = 0;
        for (Visit visit : tallyFromJson) {
            total++;
            if(visit.isSent()) {
                sent++;
            }
         }

        TextView status = (TextView) findViewById(R.id.tv_tally_status);
        status.setText("You have sent " + sent + "/" + total + " visits");
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
