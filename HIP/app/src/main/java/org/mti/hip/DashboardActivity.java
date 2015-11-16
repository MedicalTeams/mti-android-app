package org.mti.hip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.mti.hip.model.Visit;

import java.util.Date;

public class DashboardActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.new_visit).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Visit visit = getStorageManagerInstance().newVisit();
                visit.setDate(new Date());
                visit.setClinician(currentUserName);
                visit.setFacility(facilityName);
                startActivity(new Intent(DashboardActivity.this, DemographicEntryActivity.class));
            }
        });
    }
}
