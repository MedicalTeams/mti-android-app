package org.mti.hip;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.InjuryLocation;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.NetworkConnectivityManager;

import java.util.ArrayList;

public class VisitSummaryActivity extends SuperActivity {

    private Button submit;
    private String visitJson;
    private LinearLayout consultationData;
    private LinearLayout diagData;
    private ArrayList<InjuryLocation> injuryLocations;
    private Boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injuryLocations = (ArrayList<InjuryLocation>) getObjectFromPrefsKey(INJURY_LOCATIONS_KEY);

        setContentView(R.layout.activity_visit_summary);
        Visit visit = getStorageManagerInstance().currentVisit();
        consultationData = (LinearLayout) findViewById(R.id.ll_consultation_data);

        addVisitData(visit);
        diagData = (LinearLayout) findViewById(R.id.ll_diag_data);
        TextView diagHeader = new TextView(VisitSummaryActivity.this);
        diagHeader.setText(bold("Diagnosis Summary"));
        diagData.addView(diagHeader);
        for(Diagnosis diag : visit.getPatientDiagnosis()) {
            Supplemental supplementalDiagnosis = null;
            TextView tv = new TextView(VisitSummaryActivity.this);
            tv.setText(bold(diag.getName()));
            for(Supplemental supp : diag.getSupplementals()) {
                supplementalDiagnosis = supp;
                tv.append("\n - " + supplementalDiagnosis.getName());
            }

            diagData.addView(tv);
        }

        if(visit.getInjuryLocation() != 0) {
            TextView tv = new TextView(VisitSummaryActivity.this);
            // server is 1 based list is 0 based
            String injuryLocationName = injuryLocations.get(visit.getInjuryLocation() - 1).getName();
            tv.setText("Injury Location: " + bold(injuryLocationName));
            diagData.addView(tv);
        }

        if(visit.getStiContactsTreated() != 0) {
            TextView tv = new TextView(VisitSummaryActivity.this);

            // TODO refactor. Spagetti code surrounding getting/setting this in various classes
            tv.setText(parseStiContactsTreated(visit.getStiContactsTreated()));
            diagData.addView(tv);
        }



        visitJson = getJsonManagerInstance().writeValueAsString(visit);

        Log.d("visit json", visitJson);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sendVisit();
                test();

            }
        });
    }

    private void test() {
        new NetworkTask(visitJson, HttpClient.visitEndpoint, HttpClient.post) {

            @Override
            public void getResponseString(String response) {
                Log.d("Visit response string", response);
                Intent i = new Intent(VisitSummaryActivity.this, DashboardActivity.class);
                i.putExtra(EXTRA_MSG, "Visit submitted");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }.execute();
    }

    private void addVisitData(Visit visit) {
        Context c = VisitSummaryActivity.this;
        int ageVal = visit.getPatientAgeMonths() /12;
        TextView age = new TextView(c);
        age.setText("Age: ");
        TextView gender = new TextView(c);
        gender.setText("Gender: ");
        TextView staffMember = new TextView(c);
        staffMember.setText("Staff member: ");
        TextView facility = new TextView(c);
        facility.setText("Facility: ");
        TextView date = new TextView(c);
        TextView opId = new TextView(c);
        opId.setText("OPD ID: ");
        TextView isNational = new TextView(c);
        TextView isRevisit = new TextView(c);
        age.append(String.valueOf(ageVal));
        // TODO highlight if age is months
        date.setText(getFormattedDate(visit.getVisitDate()));

        if(visit.getGender() == 'M') {
            gender.append(getString(R.string.male));
        } else {
            gender.append(getString(R.string.female));
        }

        if(visit.getIsRevisit()) {
            isRevisit.append("Revisit");
        } else {
            isRevisit.append("Regular visit");
        }

        if(visit.getBeneficiaryType() == Visit.national) {
            isNational.append("National");
        } else {
            isNational.append("Refugee");
        }

        facility.append(visit.getFacilityName());
        staffMember.append(visit.getStaffMemberName());

        consultationData.addView(date);
        consultationData.addView(facility);
        consultationData.addView(staffMember);
        TextView consultHeader = new TextView(VisitSummaryActivity.this);
        consultHeader.setText(bold("Consultation Summary"));
        consultationData.addView(consultHeader);
        consultationData.addView(opId);
        consultationData.addView(age);
        consultationData.addView(gender);
        consultationData.addView(isNational);
        consultationData.addView(isRevisit);




    }

    /**
     *
     * @return True if the device is connected to the internet, False otherwise
     */
    Boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return NetworkConnectivityManager.isConnected(activeNetwork);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected = isConnected();
    }


}
