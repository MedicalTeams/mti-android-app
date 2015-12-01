package org.mti.hip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.ArrayList;

public class VisitSummaryActivity extends SuperActivity {

    private Button submit;
    private Button editDiags;
    private Button editConsultation;
    private String visitJson;
    private LinearLayout consultationData;
    private LinearLayout diagData;
    private ArrayList<InjuryLocation> injuryLocations;
    private Visit visit;
    private ArrayList<String> prompts = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);



        editDiags = (Button) findViewById(R.id.bt_diag_edit);
        editConsultation = (Button) findViewById(R.id.bt_consultation_edit);
        editDiags.setOnClickListener(editDiagListener);
        editConsultation.setOnClickListener(editConsultationListener);

        injuryLocations = (ArrayList<InjuryLocation>) getObjectFromPrefsKey(INJURY_LOCATIONS_KEY);


        visit = getStorageManagerInstance().currentVisit();
        consultationData = (LinearLayout) findViewById(R.id.ll_consultation_data);

        addVisitData(visit);
        diagData = (LinearLayout) findViewById(R.id.ll_diag_data);
        TextView diagHeader = new TextView(VisitSummaryActivity.this);
        diagHeader.setText(bold("Diagnosis Summary"));
        diagData.addView(diagHeader);
        for (Diagnosis diag : visit.getPatientDiagnosis()) {
            Supplemental supplementalDiagnosis = null;
            TextView tv = new TextView(VisitSummaryActivity.this);
            tv.setText(bold(diag.getName()));
            checkForDiagPrompt(diag);
            for (Supplemental supp : diag.getSupplementals()) {
                supplementalDiagnosis = supp;
                checkForSupplementalPrompt(supp);
                tv.append("\n - " + supplementalDiagnosis.getName());
            }

            diagData.addView(tv);
        }

        if (visit.getInjuryLocation() != 0) {
            TextView tv = new TextView(VisitSummaryActivity.this);
            // server is 1 based list is 0 based
            String injuryLocationName = injuryLocations.get(visit.getInjuryLocation() - 1).getName();
            tv.setText("Injury Location: " + bold(injuryLocationName));
            diagData.addView(tv);
        }

        if (visit.getStiContactsTreated() != 0) {
            TextView tv = new TextView(VisitSummaryActivity.this);

            // TODO refactor. Spagetti code surrounding getting/setting this in various classes
            tv.setText(parseStiContactsTreated(visit.getStiContactsTreated()));
            diagData.addView(tv);
        }

        addAlerts();

        visitJson = getJsonManagerInstance().writeValueAsString(visit);

        Log.d("visit json", visitJson);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVisit();

            }
        });
    }

    private View.OnClickListener editDiagListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    private View.OnClickListener editConsultationListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(VisitSummaryActivity.this, ConsultationActivity.class);
            // bring Consultation to the top of the stack
            i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(i);
            finish();
        }
    };

    private void sendVisit() {
        final ProgressDialog progDiag = progressDialog;
        progDiag.setCancelable(false);

        // TODO test cancelable

        new NetworkTask(visitJson, HttpClient.visitEndpoint, HttpClient.post) {


            @Override
            public void getResponseString(String response) {
               processSuccessfulResponse(response);
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }



            @Override
            protected void onPostExecute(String r) {
                progDiag.dismiss();
                if (super.e == null) {
                    getResponseString(r);
                } else if(!isCancelled()){
                   processUnsuccesfulResponse(r);
                }
            }



        }.execute();
    }

    private void processSuccessfulResponse(String r) {
        Log.d("Visit response string", r);
        visit.setSent(true);
        startDashboard("Visit submitted");
    }

    private void processUnsuccesfulResponse(String r) {
        if(r != null) {
            Log.e("Visit error string", r);
        }
        visit.setSent(false);
        startDashboard("VISIT DID NOT SEND. Soon you will be able to resend failed visits.");
    }

    private void startDashboard(String message) {

        String tallyJsonOut = getJsonManagerInstance().writeValueAsString(getStorageManagerInstance().getTally());

        getStorageManagerInstance().writeTallyJsonToFile(tallyJsonOut, this);

        VisitDiagnosisListAdapter.check_states.clear();
        Intent i = new Intent(VisitSummaryActivity.this, DashboardActivity.class);
        i.putExtra(EXTRA_MSG, message);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void checkForDiagPrompt(Diagnosis diag) {
        int id = diag.getId();
        if(id == 3 || id == 4 || id == 9 || id == 10 || id == 14) {
            addDiagPrompt(diag, getString(R.string.prompt_outbreak_potential));
        } else if (id == 12 || id == 13) {
        // TODO add cholera once it is in the service
            addDiagPrompt(diag, getString(R.string.prompt_threshold_reached));
        } else if (id == 15) {
            addDiagPrompt(diag, getString(R.string.prompt_hiv));
        }

    }

    private void checkForSupplementalPrompt(Supplemental diag) {
        int id = diag.getId();

        if (id == 65 || id == 66) {
            addSupplementalDiagPrompt(diag, getString(R.string.prompt_assault));
        }



    }

    private void addDiagPrompt(Diagnosis diag, String string) {
        prompts.add(diag.getName() + ": " + string);
    }

    private void addSupplementalDiagPrompt(Supplemental diag, String string) {
        prompts.add(diag.getName() + ": " + string);
    }

    private void addVisitData(Visit visit) {
        Context c = VisitSummaryActivity.this;

        int ageVal = visit.getPatientAgeMonths();
        String ageString;
        if (visit.isAgeMonths()) {
            ageString = String.valueOf(ageVal);
        } else {
            ageString = String.valueOf(ageVal / 12);
        }
        TextView age = new TextView(c);
        age.setText("Age: ");
        age.append(ageString);

        TextView gender = new TextView(c);
        gender.setText("Gender: ");
        if (visit.getGender() == 'M') {
            gender.append(getString(R.string.male));
        } else {
            gender.append(getString(R.string.female));
        }

        TextView staffMember = (TextView) findViewById(R.id.tv_summary_staff_member);
        staffMember.setText("Staff member: ");
        staffMember.append(visit.getStaffMemberName());

        TextView facility = (TextView) findViewById(R.id.tv_summary_centre);
        facility.setText("Centre: ");
        facility.append(visit.getFacilityName());

        TextView date = (TextView) findViewById(R.id.tv_summary_date);
        date.setText(getFormattedDate(visit.getVisitDate()));

        TextView opId = new TextView(c);
        opId.setText("OPD ID: ");
        if (visit.getOPD() != 0) {
            opId.append(String.valueOf(visit.getOPD()));
        } else {
            opId.append("Not recorded");
        }

        TextView isNational = new TextView(c);

        if (visit.getBeneficiaryType() == Visit.national) {
            isNational.append("National");
        } else {
            isNational.append("Refugee");
        }

        TextView isRevisit = new TextView(c);
        if (visit.getIsRevisit()) {
            isRevisit.append("Revisit");
        } else {
            isRevisit.append("Regular visit");
        }

        TextView consultHeader = new TextView(VisitSummaryActivity.this);
        consultHeader.setText(bold("Consultation Summary"));
        consultationData.addView(consultHeader);
        consultationData.addView(opId);
        consultationData.addView(age);
        consultationData.addView(gender);
        consultationData.addView(isNational);
        consultationData.addView(isRevisit);


    }

    private void addAlerts() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_summary_alerts);
        if(prompts.isEmpty()) {
            ll.setVisibility(View.GONE);
        }
        for(String alert : prompts) {
            TextView tv = new TextView(this);
            tv.setText(alert);
            ll.addView(tv);
        }
    }


}
