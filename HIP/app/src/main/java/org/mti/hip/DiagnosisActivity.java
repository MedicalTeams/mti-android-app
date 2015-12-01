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

public class DiagnosisActivity extends SuperActivity {

    VisitDiagnosisListAdapter listAdapter;
    ExpandableListView expListView;
    private StringBuilder errorBuilder = new StringBuilder();
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
                    alert.showAlert("Invalid visit", errorBuilder.toString());
                }
                return true;
            default:
                return false;
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
                    } else {
                        supps = listAdapter.getList(i);
                        getAndAddDiagnosis(suppsSet, supps, i, j);
                    }
                }
            } // end of check boxes loop

        } // end of groups loop
        if (!hadSomethingChecked) {
            errorBuilder.append("You must select at least one diagnosis");
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

        // TODO refactor. This got kinda ugly but it's bullet proof for now so I'm leaving it alone.

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
        if(checks.contains(0) && visit.getStiContactsTreated() != 0) {
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

    public static boolean isCompletelyUnchecked(ArrayList<Integer> array)
    {
        for(Integer i : array) if(i == 0) return false;
        return true;
    }
}


