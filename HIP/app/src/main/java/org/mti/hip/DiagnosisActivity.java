package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.mti.hip.utils.VisitDiagnosisListAdapter;

public class DiagnosisActivity extends SuperActivity {

    VisitDiagnosisListAdapter listAdapter;
    ExpandableListView expListView;
    private int diagId = 0;
    private int stiId = 1;
    private int chronicDiseaseId = 2;
    private int mentalIllnessId = 3;
    private int injuryId = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_entry);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.visitdiaglist);


        listAdapter = new VisitDiagnosisListAdapter(this);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.expandGroup(0);

        expListView.setOnChildClickListener(listAdapter.getListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_next:
                startActivity(new Intent(this, VisitSummaryActivity.class));
                return true;
            default:
                return false;
        }
    }
}
