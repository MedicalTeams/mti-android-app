package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;

import org.mti.hip.model.Visit;

public class DemographicEntryActivity extends SuperActivity {

    private Visit visit = getStorageManagerInstance().currentVisit();

    private char gender;
    private RadioButton rbMale;
//    private RadioButton rbFemale;
    private RadioButton rbVisit;
//    private RadioButton rbRevisit;
    private RadioButton rbNational;
//    private RadioButton rbRefugee;
    private EditText opdNum;
    private EditText age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographic_entry);
        opdNum = (EditText) findViewById(R.id.opd_number);
        age = (EditText) findViewById(R.id.patient_age);
        rbMale = (RadioButton) findViewById(R.id.rb_male);
//        rbFemale = (RadioButton) findViewById(R.id.rb_female);
        rbNational = (RadioButton) findViewById(R.id.rb_national);
//        rbRefugee = (RadioButton) findViewById(R.id.rb_refugee);
        rbVisit = (RadioButton) findViewById(R.id.rb_visit);
//        rbRefugee = (RadioButton) findViewById(R.id.rb_revisit);
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
                if(valid()) {
                    startActivity(new Intent(this, DiagnosisActivity.class));
                } else {
                    // validator
                }
                return true;
            default:
                return false;
        }
    }

    private boolean valid() {
        if(editTextHasContent(opdNum)) {
            visit.setOpId(Integer.valueOf(opdNum.getText().toString()));
        }
        if(editTextHasContent(age)) {
            visit.setAgeMonths(Integer.valueOf(age.getText().toString()) * 12);
        }
        if(rbMale.isChecked()) {
            visit.setGender('M');
        } else {
            visit.setGender('F');
        }
        if(rbNational.isChecked()) {
            visit.setIsNational(true);
        } else {
            visit.setIsNational(false);
        }
        if(rbVisit.isChecked()) {
            visit.setIsRevisit(false);
        } else {
            visit.setIsRevisit(true);
        }
        return true;
    }
}
