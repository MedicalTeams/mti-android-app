package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.ArrayList;
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
                if (valid()) {
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
        // TODO make it possible to return false when local selections aren't valid
        boolean valid = true;
        ArrayList<Diagnosis> diags;

        ArrayList<Supplemental> supps;
        Visit visit = getStorageManagerInstance().currentVisit();
        visit.getPatientDiagnosis().clear();
        for (int i = 0; i < listAdapter.check_states.size(); i++) {
            // this block = group lists 0-5
            HashSet<Supplemental> suppsSet = new HashSet<>();
            suppsSet.clear();

            for (int j = 0; j < listAdapter.check_states.get(i).size(); j++) {

                if (listAdapter.check_states.get(i).get(j) == 0) {
                    // these have checked state
                    if (i == diagId) {
                        diags = (ArrayList<Diagnosis>) listAdapter.getList(i);
                        Diagnosis diag = diags.get(j);

                        visit.getPatientDiagnosis().add(diag);
                    } else if (i == stiId) {
                        supps = listAdapter.getList(i);
                        Supplemental supp = supps.get(j);
                        suppsSet.add(supp);
                        Diagnosis diag = new Diagnosis();
                        diag.setName(String.valueOf(listAdapter.getGroup(i)));
                        diag.setSupplementals(suppsSet);
                        // TODO manage supp diag id
                        visit.getPatientDiagnosis().add(diag);
                        valid = checkForStiContactsTreated(diag);
                    } else {
                        supps = listAdapter.getList(i);
                        Supplemental supp = supps.get(j);
                        suppsSet.add(supp);
                        Diagnosis diag = new Diagnosis();
                        diag.setId(supp.getDiagnosis());
                        diag.setName(String.valueOf(listAdapter.getGroup(i)));
                        diag.setSupplementals(suppsSet);
                        visit.getPatientDiagnosis().add(diag);
                    }
                }
            }

        }
        return valid;
    }

    private boolean checkForStiContactsTreated(Diagnosis diag) {
        if(visit.getStiContactsTreated() == 0) {
            errorMsg = "STI Contacts Treated was not entered";

            /* avoid STI trigger
             {
    "id": 40,
    "name": "Opthamalia Neonatorum",
    "diagnosis": 16
  },
  {
    "id": 41,
    "name": "Congential Syphilis",
    "diagnosis": 16
  },
             */
            return false;
        }
        return true;
    }
}


