package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.mti.hip.model.Diagnosis;
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
                Intent i = new Intent(VisitsActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        String tallyJson = getStorageManagerInstance().readTallyToJsonString(this);
        Tally tally = JSON.loads(tallyJson, Tally.class);

        TextView tv = (TextView)findViewById(R.id.tv_count);
        tv.setText(getString(R.string.number_of_results) + ": " + tally.size());

        for(int i = 0; i < tally.size(); i++) {
            table.addView(buildRow(tally.get(i)));
        }
    }

    private TableRow buildRow(Visit visit) {
        TableRow row = (TableRow)LayoutInflater.from(VisitsActivity.this).inflate(R.layout.item_visit, null);
        TextView tvStatus = (TextView) row.findViewById(R.id.tv_status);
        if(visit.getBeneficiaryType() == Visit.national) {
            tvStatus.setText("National");
        }else{
            tvStatus.setText("Refugee");
        }

        TextView tvAge = (TextView) row.findViewById(R.id.tv_age);
        tvAge.setText("" + visit.getPatientAgeYears());

        String diagnosesString = "";
        visit.getPatientDiagnosis();
        Iterator<Diagnosis> iter = visit.getPatientDiagnosis().iterator();
        while(iter.hasNext()) {
            Diagnosis diagnosis = iter.next();
            diagnosesString += diagnosis.getName() + ",";
        }
        TextView tvDiagnosis = (TextView) row.findViewById(R.id.tv_diagnosis);
        tvDiagnosis.setText(diagnosesString);

        TextView tvVisitDate = (TextView) row.findViewById(R.id.tv_visit_date);
        tvVisitDate.setText(visit.getVisitDate().toString());

        TextView tvVisitType = (TextView) row.findViewById(R.id.tv_visit_type);
        tvVisitType.setText(visit.getIsRevisit().toString());

        TextView tvStaff = (TextView) row.findViewById(R.id.tv_staff);
        tvStaff.setText(visit.getStaffMemberName());

        TextView tvGender = (TextView) row.findViewById(R.id.tv_gender);
        tvGender.setText("" + visit.getGender());
        return row;
    }
}