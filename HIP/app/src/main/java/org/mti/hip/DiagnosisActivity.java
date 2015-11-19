package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.SupplementalDiagnosis;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

public class DiagnosisActivity extends SuperActivity {

    VisitDiagnosisListAdapter listAdapter;
    ExpandableListView expListView;
    private String errorMsg = "Error placeholder";
    private Visit visit = getStorageManagerInstance().currentVisit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_entry);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.visitdiaglist);


        listAdapter = new VisitDiagnosisListAdapter(this);

        // setting list adapter
        expListView.setAdapter(listAdapter);
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
                // TODO retrieve each selected item with appropriate switching and populate visit
                if(valid()) {
                    startActivity(new Intent(this, VisitSummaryActivity.class));
                } else {
                    alert.showAlert("Invalid visit", errorMsg);
                }
                return true;
            default:
                return false;
        }
    }

    private boolean valid() {
        ArrayList<SupplementalDiagnosis> inJuryLoc = (ArrayList<SupplementalDiagnosis>) listAdapter.getList(injuryLocId);
        ArrayList<SupplementalDiagnosis> mentalIllness = (ArrayList<SupplementalDiagnosis>) listAdapter.getList(mentalIllnessId);
        ArrayList<Diagnosis> diags;
        HashSet<SupplementalDiagnosis> suppsSet = new HashSet<>();
        ArrayList<SupplementalDiagnosis> supps;
        Visit visit = getStorageManagerInstance().currentVisit();
        for(int i = 0; i < listAdapter.check_states.size(); i++) {


            for(int j = 0; j < listAdapter.check_states.get(i).size(); j++) {
                // this block = group lists 0-5
//                suppsSet.clear();
                if(listAdapter.check_states.get(i).get(j) == 0) {
                    // these have checked state
                    if (i == diagId) {
                        diags = (ArrayList<Diagnosis>) listAdapter.getList(i);
                        Diagnosis diag = diags.get(j);
                        visit.getDiags().add(diag);
                    } else {
                        supps = listAdapter.getList(i);
                        SupplementalDiagnosis supp = supps.get(j);
                        suppsSet.add(supp);
                        Diagnosis diag = new Diagnosis();
                        diag.setDescription("Header" + i);
                        diag.setSupplementalDiags(suppsSet);
                        visit.getDiags().add(diag);
                    }
                }

//                if (i != diagId) {
//
//                }

            }

        }
        Log.d("test123", String.valueOf(getJsonManagerInstance().writeValueAsString(visit)));
        return true;
    }
}
