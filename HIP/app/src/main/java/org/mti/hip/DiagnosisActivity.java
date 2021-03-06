package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.ArrayList;

public class DiagnosisActivity extends SuperActivity {

    VisitDiagnosisListAdapter listAdapter;
    ExpandableListView expListView;
    EditText searchPhrase;
    private StringBuilder errorBuilder = new StringBuilder();
    private Visit visit = getStorageManagerInstance().currentVisit();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_entry);
        displayMode();

        searchPhrase = (EditText) findViewById(R.id.search_phrase);
        expListView = (ExpandableListView) findViewById(R.id.visitdiaglist);

        listAdapter = new VisitDiagnosisListAdapter(this);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.setOnChildClickListener(listAdapter.getListener());

        searchPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int groupCount = listAdapter.getGroupCount();
                for (int i =0; i < groupCount; i++) {
                    expListView.expandGroup(i);
                }
                final int childPosition = listAdapter.search(searchPhrase.getText().toString());
                listAdapter.notifyDataSetChanged();

                expListView.post(new Runnable() {
                    @Override
                    public void run() {
                        expListView.setSelectionFromTop(childPosition, 0);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.bt_next_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNext();
            }
        });
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
                gotoNext();
                return true;
            default:
                return false;
        }
    }

    private void gotoNext() {
        if (valid()) {
            startActivity(new Intent(this, VisitSummaryActivity.class));
        } else {
            alert.showAlert(getString(R.string.invalid_visit), errorBuilder.toString());
        }
    }

    private boolean valid() {
        boolean valid = true;
        errorBuilder = new StringBuilder();
        ArrayList<Diagnosis> diags;

        ArrayList<Supplemental> supps;
        Visit visit = getStorageManagerInstance().currentVisit();
        visit.getPatientDiagnosis().clear();
        visit.setStiContactsTreated(VisitDiagnosisListAdapter.stiContactsTreated);
        visit.setInjuryLocation(0);
        boolean hadSomethingChecked = false;
        if (!checkInjuryValidity()) {
            return false;
        }
        if(!checkForStiContactsTreated()) {
            return false;
        }
        for (int i = 0; i < VisitDiagnosisListAdapter.check_states.size(); i++) {
            // this block = group lists 0-5
            ArrayList<Supplemental> suppsSet = new ArrayList<>();
            suppsSet.clear();

            for (int j = 0; j < VisitDiagnosisListAdapter.check_states.get(i).size(); j++) {
                // this block is all check boxes

                if (VisitDiagnosisListAdapter.check_states.get(i).get(j) == 0) {
                    // these have checked state
                    hadSomethingChecked = true;
                    if (listAdapter.getSelectableOthers().containsValue(j)) {
                        // skip any duplicate diags from selectable others group
                        continue;
                    }

                    if (i == diagId) {
                        diags = (ArrayList<Diagnosis>) listAdapter.getList(i);
                        Diagnosis diag = diags.get(j);
                        visit.getPatientDiagnosis().add(diag);
                    } else if (i == stiId) {
                        supps = listAdapter.getList(i);
                        getAndAddDiagnosis(suppsSet, supps, i, j);
                    } else if (i == injuryLocId) {
                        supps = listAdapter.getList(i);
                        Supplemental supp = supps.get(j);
                        visit.setInjuryLocation(supp.getId());
                        injuryListPosition = j;
                    } else {
                        supps = listAdapter.getList(i);
                        getAndAddDiagnosis(suppsSet, supps, i, j);
                    }
                }
            } // end of check boxes loop
        } // end of groups loop
        if (!hadSomethingChecked) {
            errorBuilder.append(getString(R.string.u_must_select_a_dx));
            return false;
        }
        return valid;
    }

    private Diagnosis getAndAddDiagnosis(ArrayList<Supplemental> suppsSet, ArrayList<Supplemental> supps, int i, int j) {
        Supplemental supp = supps.get(j);
        suppsSet.add(supp);
        Diagnosis diag = new Diagnosis();
        diag.setId(supp.getDiagnosis());
        diag.setName(String.valueOf(listAdapter.getGroup(i)));
        diag.setSupplementals(suppsSet);
        visit.getPatientDiagnosis().add(diag);
        return diag;
    }

    private boolean checkInjuryValidity() {
        //(the number of 1s varies per child list, but this shows the basic flow}
        //1111-1111 is valid
        //1111-1011 is not valid
        //1101-1111 similarly is not valid
        //1110-1101 is valid
        //0001-1111 is not valid since all the zeros are in the first list
        boolean canProceed = false;
        ArrayList<ArrayList<Integer>> checks = VisitDiagnosisListAdapter.check_states;
        if(isCompletelyUnchecked(checks.get(injuryId)) && isCompletelyUnchecked(checks.get(injuryLocId))) {
            canProceed = true;
        }

        if(checks.get(injuryId).contains(0) && checks.get(injuryLocId).contains(0)) {
            canProceed = true;
        }
        if(!canProceed) {
            errorBuilder.append(getString(R.string.tooltip_injury_mode));
        }

        return canProceed;
    }

    private boolean checkForStiContactsTreated() {
        ArrayList<Integer> checks = VisitDiagnosisListAdapter.check_states.get(stiId);
        if(!checks.contains(0)) {
            return true; // skipping if it doesn't contain any values
        }
        boolean hasOnlyAllowableSupps = false;
        ArrayList<Supplemental> supplementals = listAdapter.getList(stiId);
        ArrayList<Integer> checkedIds = new ArrayList<>();
        for(int i = 0;i < supplementals.size();i++) {
            if(checks.get(i) == 0) {
                checkedIds.add(supplementals.get(i).getId());
            }
        }

        ArrayList<Integer> allowedIds = new ArrayList<>();
        allowedIds.add(40);
        allowedIds.add(41);
        for(Integer id : checkedIds) {
            hasOnlyAllowableSupps = allowedIds.contains(id);
            if(!hasOnlyAllowableSupps) {
                break;
            }
        }

        if(hasOnlyAllowableSupps) {
            return true;
        }

        boolean containsBothStiAndContactsTreated = false;
        if(checks.contains(0) && visit.getStiContactsTreated() != -1) {
            containsBothStiAndContactsTreated = true;
        }

        if (!containsBothStiAndContactsTreated) {
            if(!errorBuilder.toString().contains(getString(R.string.tooltip_morbidity_sti_contacts)))
            errorBuilder.append(getString(R.string.tooltip_morbidity_sti_contacts) + "\n");
        } else {
            return true;
        }
        return false;
    }

    public static boolean isCompletelyUnchecked(ArrayList<Integer> array) {
        for(Integer i : array) if(i == 0) return false;
        return true;
    }
}
