package org.mti.hip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import org.mti.hip.model.Visit;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

public class ConsultationActivity extends SuperActivity {

    private Visit visit = getStorageManagerInstance().currentVisit();

    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbVisit;
    private RadioButton rbRevisit;
    private RadioButton rbNational;
    private RadioButton rbRefugee;
    private EditText opdNum;
    private EditText patientYears;
    private EditText patientMonths;
    private EditText patientDays;
    private StringBuilder errorBuilder;
    private int backPressCount;
    private boolean editMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
        opdNum = (EditText) findViewById(R.id.opd_number);
        patientYears = (EditText) findViewById(R.id.patient_years);
        patientMonths = (EditText) findViewById(R.id.patient_months);
        patientDays = (EditText) findViewById(R.id.patient_days);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        rbNational = (RadioButton) findViewById(R.id.rb_national);
        rbRefugee = (RadioButton) findViewById(R.id.rb_refugee);
        rbVisit = (RadioButton) findViewById(R.id.rb_visit);
        rbRevisit = (RadioButton) findViewById(R.id.rb_revisit);
        rbRefugee = (RadioButton) findViewById(R.id.rb_refugee);

        findViewById(R.id.opd_tooltip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.showAlert(getString(R.string.info), getString(R.string.tooltip_opd_number));
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        editMode = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editMode = false;
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
                errorBuilder = new StringBuilder();
                if (valid()) {
                    startDiagnosisActivity();
                } else {
                    alert.showAlert(getString(R.string.errors_found), errorBuilder.toString());
                }
                return true;
            default:
                return false;
        }
    }

    private void startDiagnosisActivity() {
        Intent i = new Intent(this, DiagnosisActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
    }

    private boolean valid() {
        boolean valid = true;
        if (editTextHasContent(opdNum)) {
            try {
                visit.setOPD(Long.valueOf(opdNum.getText().toString()));
            } catch (NumberFormatException e) {
                alert.showAlert("Invalid number", "Please enter a valid number.");
            }
        } // not worried about the else case since OPD# is optional

        if (editTextHasContent(patientYears) || editTextHasContent(patientMonths) || editTextHasContent(patientDays)) {
            visit.setPatientAgeYears(editTextToInt(patientYears, 0));
            visit.setPatientAgeMonths(editTextToInt(patientMonths, 0));
            visit.setPatientAgeDays(editTextToInt(patientDays, 0));
            double ageVal = visit.getPatientAgeLow();
            if (ageVal <= 0 | ageVal > 150) {
                addErrorString(R.string.error_age_range);
                valid = false;
            }
        } else {
            // handles nothing entered scenario
            addErrorString(R.string.error_age);
           valid = false;
        }


        if (rbMale.isChecked()) {
            visit.setGender('M');
        } else if (rbFemale.isChecked()) {
            visit.setGender('F');
        } else {
            addErrorString(R.string.error_gender);
            valid = false;
        }
        if (rbNational.isChecked()) {
            visit.setBeneficiaryType(Visit.national);
        } else if (rbRefugee.isChecked()) {
            visit.setBeneficiaryType(Visit.refugee);
        } else {
            addErrorString(R.string.error_status_type);
           valid = false;
        }
        if (rbVisit.isChecked()) {
            visit.setIsRevisit(false);
        } else if (rbRevisit.isChecked()) {
            visit.setIsRevisit(true);
        } else {
            addErrorString(R.string.error_visit_type);
           valid = false;
        }
        return valid;
    }

    private void addErrorString(int id) {
        errorBuilder.append(getString(id));
        errorBuilder.append("\n");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(getString(R.string.ru_sure_u_wanna_delete_visit));
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VisitDiagnosisListAdapter.check_states.clear();
                finish();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }
}
