package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.JSON;
import org.mti.hip.utils.VisitListAdapter;

import java.util.ArrayList;

public class VisitsActivity extends SuperActivity {

    private ListView lv;
    private Button btNext;
    private ArrayAdapter<Visit> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);
        displayMode();

        lv = (ListView) findViewById(android.R.id.list);

        findViewById(R.id.bt_next_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(VisitsActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String tallyJson = getStorageManagerInstance().readTallyToJsonString(this);
        Tally tally = JSON.loads(tallyJson, Tally.class);

        adapter = new VisitListAdapter(this, tally);
        lv.setAdapter(adapter);
    }
}