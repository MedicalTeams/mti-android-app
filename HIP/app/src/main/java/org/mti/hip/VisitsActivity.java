package org.mti.hip;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.JSON;
import java.util.Iterator;

public class VisitsActivity extends SuperActivity {

    private TableLayout table;
    private Button btNext;
    private ArrayAdapter<Visit> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);
        displayMode();

        table = (TableLayout) findViewById(R.id.linlay);

        findViewById(R.id.bt_next_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNext();
            }
        });

        String tallyJson = getStorageManagerInstance().readTallyToJsonString(this);
        Tally tally;
        if(tallyJson == "" || tallyJson == null) {
            tally = new Tally();
        } else {
            tally = JSON.loads(tallyJson, Tally.class);
        }
        TextView tv = (TextView)findViewById(R.id.tv_count);
        tv.setText(getString(R.string.number_of_results) + ": " + tally.size());

        table.addView(buildHeader());
        for(int i = 0; i < tally.size(); i++) {
            table.addView(buildRow(tally.get(i)));
        }
        for(int i = tally.size(); i < 20; i++) {
            table.addView(buildBlank());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        gotoNext();
        return true;
    }

    private void gotoNext() {
        Intent i = new Intent(VisitsActivity.this, DashboardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private TableRow buildHeader() {
        TableRow row = (TableRow)LayoutInflater.from(VisitsActivity.this).inflate(R.layout.item_visit, null);
        TextView tvSync = (TextView) row.findViewById(R.id.tv_sync);
        tvSync.setText("SYNC");
        tvSync.setTypeface(null, Typeface.BOLD);

        TextView tvStatus = (TextView) row.findViewById(R.id.tv_status);
        tvStatus.setText("STATUS");
        tvStatus.setTypeface(null, Typeface.BOLD);

        TextView tvAge = (TextView) row.findViewById(R.id.tv_age);
        tvAge.setText("AGE");
        tvAge.setTypeface(null, Typeface.BOLD);

        TextView tvDiagnosis = (TextView) row.findViewById(R.id.tv_diagnosis);
        tvDiagnosis.setText("DIAGNOSES");
        tvDiagnosis.setTypeface(null, Typeface.BOLD);

        TextView tvVisitDate = (TextView) row.findViewById(R.id.tv_visit_date);
        tvVisitDate.setText("DATE");
        tvVisitDate.setTypeface(null, Typeface.BOLD);

        TextView tvVisitType = (TextView) row.findViewById(R.id.tv_visit_type);
        tvVisitType.setText("TYPE");
        tvVisitType.setTypeface(null, Typeface.BOLD);

        TextView tvStaff = (TextView) row.findViewById(R.id.tv_staff);
        tvStaff.setText("STAFF");
        tvStaff.setTypeface(null, Typeface.BOLD);

        TextView tvGender = (TextView) row.findViewById(R.id.tv_gender);
        tvGender.setText("GENDER");
        tvGender.setTypeface(null, Typeface.BOLD);
        return row;
    }

    private TableRow buildRow(Visit visit) {
        TableRow row = (TableRow)LayoutInflater.from(VisitsActivity.this).inflate(R.layout.item_visit, null);
        TextView tvSync = (TextView) row.findViewById(R.id.tv_sync);
        if(visit.getStatus() == Visit.statusUnsent) {
            tvSync.setText("Not Synced");
        } else if(visit.getStatus() == Visit.statusSuccess) {
            tvSync.setText("Synced");
        } else if(visit.getStatus() == Visit.statusDuplicate) {
            tvSync.setText("Synced");
        } else if(visit.getStatus() == Visit.statusFailure) {
            tvSync.setText("Not Synced");
        } else if(visit.getStatus() == Visit.statusDisabled) {
            tvSync.setText("Not Synced");
        }else{
            tvSync.setText("Unknown");
        }

        TextView tvStatus = (TextView) row.findViewById(R.id.tv_status);
        if(visit.getBeneficiaryType() == Visit.national) {
            tvStatus.setText("National");
        }else{
            tvStatus.setText("Refugee");
        }

        TextView tvAge = (TextView) row.findViewById(R.id.tv_age);
        tvAge.setText("");
        int years = visit.getPatientAgeYears();
        int months = visit.getPatientAgeMonths();
        int days = visit.getPatientAgeDays();
        if(years > 0) {
            tvAge.append(String.valueOf(years) + " " + getString(R.string.years) + " ");
        }
        if(months > 0) {
            tvAge.append(String.valueOf(months) + " " + getString(R.string.months) + " ");
        }
        if(days > 0) {
            tvAge.append(String.valueOf(days) + " " + getString(R.string.days) + " ");
        }

        String diagnosesString = "";
        Iterator<Diagnosis> iterDiagnosis = visit.getPatientDiagnosis().iterator();
        while(iterDiagnosis.hasNext()) {
            Diagnosis diagnosis = iterDiagnosis.next();
            diagnosesString += diagnosis.getName() + "\n";
            Iterator<Supplemental> iterSupplemental = diagnosis.getSupplementals().iterator();
            while(iterSupplemental.hasNext()) {
                Supplemental supplemental = iterSupplemental.next();
                diagnosesString += "- " + supplemental.getName() + "\n";
            }
        }
        TextView tvDiagnosis = (TextView) row.findViewById(R.id.tv_diagnosis);
        tvDiagnosis.setText(diagnosesString);

        TextView tvVisitDate = (TextView) row.findViewById(R.id.tv_visit_date);
        tvVisitDate.setText(getFormattedDate(visit.getVisitDate()));

        TextView tvVisitType = (TextView) row.findViewById(R.id.tv_visit_type);
        if(visit.getIsRevisit()) {
            tvVisitType.setText("Revisit");
        }else{
            tvVisitType.setText("New Visit");
        }

        TextView tvStaff = (TextView) row.findViewById(R.id.tv_staff);
        tvStaff.setText(visit.getStaffMemberName());

        TextView tvGender = (TextView) row.findViewById(R.id.tv_gender);
        if(visit.getGender() == 'M') {
            tvGender.setText(getString(R.string.male));
        } else {
            tvGender.setText(getString(R.string.female));
        }
        return row;
    }

    private TableRow buildBlank() {
        TableRow row = (TableRow)LayoutInflater.from(VisitsActivity.this).inflate(R.layout.item_visit, null);
        return row;
    }
}