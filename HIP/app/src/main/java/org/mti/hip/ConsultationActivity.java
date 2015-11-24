package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import org.mti.hip.model.Visit;

public class ConsultationActivity extends SuperActivity {

    private Visit visit = getStorageManagerInstance().currentVisit();

    private RadioButton rbMale;
    private RadioButton rbFemale;
    private RadioButton rbVisit;
    private RadioButton rbRevisit;
    private RadioButton rbNational;
    private RadioButton rbRefugee;
    private RadioButton rbYears;
    private RadioButton rbMonths;
    private EditText opdNum;
    private EditText age;
    private StringBuilder errorBuilder;
    private Button validationToggle;
    private boolean validate = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultation);
        opdNum = (EditText) findViewById(R.id.opd_number);
        age = (EditText) findViewById(R.id.patient_age);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        rbNational = (RadioButton) findViewById(R.id.rb_national);
        rbRefugee = (RadioButton) findViewById(R.id.rb_refugee);
        rbVisit = (RadioButton) findViewById(R.id.rb_visit);
        rbRevisit = (RadioButton) findViewById(R.id.rb_revisit);
        rbRefugee = (RadioButton) findViewById(R.id.rb_refugee);
        rbYears = (RadioButton) findViewById(R.id.rb_years);
        rbMonths = (RadioButton) findViewById(R.id.rb_months);

        setupDebugButton();
    }

    private void setupDebugButton() {
        validationToggle = (Button) findViewById(R.id.bt_consultation_debug);
        validationToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate) {
                    validate = false;
                    validationToggle.setText("Enable validation");
                } else {
                    validate = true;
                    validationToggle.setText("Disable validation");
                }
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
                errorBuilder = new StringBuilder();
                if (valid() && validate) {
                    startActivity(new Intent(this, DiagnosisActivity.class));
                } else if(!validate) {
                    startActivity(new Intent(this, DiagnosisActivity.class)); // debug bypass of validator
                } else {
                    alert.showAlert("Errors found", errorBuilder.toString());
                }
                return true;
            default:
                return false;
        }
    }

    private boolean valid() {
        boolean valid = true;
        if (editTextHasContent(opdNum)) {
            visit.setOPD(Integer.valueOf(opdNum.getText().toString()));
        } // not worried about the else case since OPD# is optional

        if (editTextHasContent(age)) {
            // something is entered
            int ageVal = Integer.valueOf(age.getText().toString());

            if (rbMonths.isChecked()) {
                visit.setPatientAgeMonths(Integer.valueOf(age.getText().toString()));
                visit.setIsAgeMonths(true);
                if(ageVal == 0 | ageVal > 11) {
                    addErrorString(R.string.error_age_range_months);
                    valid = false;
                }
            } else if (rbYears.isChecked()){
                visit.setPatientAgeMonths(Integer.valueOf(age.getText().toString()) * 12);
                visit.setIsAgeMonths(false);
                if (ageVal == 0 | ageVal > 150){
                    addErrorString(R.string.error_age_range);
                    valid = false;
                }
            } else {
                valid = false;
                addErrorString(R.string.error_age);
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
            visit.setBeneficiaryType(0);
        } else if (rbRefugee.isChecked()) {
            visit.setBeneficiaryType(1);
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
}
