package org.mti.hip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.InjuryLocation;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSON;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.ArrayList;

public class VisitSummaryActivity extends SuperActivity {

    private Button submit;
    private Button editDiags;
    private Button editConsultation;
    private LinearLayout consultationData;
    private LinearLayout diagData;
    private ArrayList<InjuryLocation> injuryLocations;
    private Visit visit;
    private ArrayList<Spanned> prompts = new ArrayList<>();
    private Tally tally;

    private float headerSize = 20f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);
        displayMode();

        String tallyJson = JSON.dumps(getStorageManagerInstance().getTally());
        tally = JSON.loads(tallyJson, Tally.class);
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
        diagHeader.setText(bold(getString(R.string.dx_summary)));
        diagHeader.setTextSize(headerSize);
        diagData.addView(diagHeader);
//        ArrayList<Diagnosis> displayList = new ArrayList<>(visit.getPatientDiagnosis());
//        Collections.sort(displayList, Collections.reverseOrder());
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
            if(diag.getName().startsWith("STI")) {
                if (visit.getStiContactsTreated() != -1) {
                    // TODO refactor. Spaghetti code surrounding getting/setting this in various classes (look at stiContactsTreated static constant in adapter vs. Visit getter/setters)
                    tv.append("\n" + parseStiContactsTreated(visit.getStiContactsTreated()));
                }
            }
            if(diag.getName().startsWith("Injuries")) {
                if (visit.getInjuryLocation() != 0) {
                    String injuryLocationName = injuryLocations.get(injuryListPosition).getName();
                    tv.append("\n" + getString(R.string.injury_location) + ": " + bold(injuryLocationName));
                }
            }
            diagData.addView(tv);
        }

        addAlerts();

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTally();
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

    private void sendTally() {
        if(!isConnected()) {
            processUnsuccessfulResponse("");
            startDashboard("");
            return;
        }

        String tallyJson = JSON.dumps(tally.getLimitedUnsynced());
        Log.d("sendTally", tallyJson);

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                startDashboard("");
            }
        });

        new NetworkTask(tallyJson, HttpClient.tallyEndpoint, HttpClient.post) {

            @Override
            public void getResponseString(String response) {
                processSuccessfulResponse(response);
            }

            @Override
            protected void onPostExecute(String r) {
                progressDialog.dismiss();
                userTimeout.start();
                if (super.e == null) {
                    getResponseString(r);
                } else if(!isCancelled()){
                   processUnsuccessfulResponse(r);
                }
            }
        }.execute();
    }

    private void processSuccessfulResponse(String r) {
        boolean disabled = false;
        boolean failures = false;
        Log.d("Visit response string", r);
        // update the tally - if tally response contains status == 4 then device is disabled
        Tally serverTally = JSON.loads(r, Tally.class);
        for(Visit serverVisit : serverTally) {
            if(serverVisit.getStatus() == Visit.statusDisabled) {
                Log.d("testing 4", "device disabled");
                disabled = true;
                writeDeviceStatus("D");
                break;
            } else {
                Log.d("testing 4", "code is: " + serverVisit.getStatus());
            }
            for(Visit visit : tally) {
                if(serverVisit.getStatus() == Visit.statusFailure) {
                    failures = true;
                }
                if(visit.getVisitDate().equals(serverVisit.getVisitDate())) {
                    visit.setStatus(serverVisit.getStatus());
                }
            }
        }
    }

    private void processUnsuccessfulResponse(String r) {
        if(r != null) {
            Log.e("Visit error string", r);
        }
    }

    private void startDashboard(String message) {
        String tallyJson = JSON.dumps(tally);
        getStorageManagerInstance().writeTallyJsonToFile(tallyJson, this);
        VisitDiagnosisListAdapter.check_states.clear();
        Intent i = new Intent(VisitSummaryActivity.this, DashboardActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void checkForDiagPrompt(Diagnosis diag) {
        int id = diag.getId();
        if(id == 3 || id == 4 || id == 9 || id == 10 || id == 14) {
            addDiagPrompt(diag, getString(R.string.prompt_outbreak_potential));
        } else if (id == 12 || id == 13 || id == 29) {
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
        SpannableStringBuilder builder = new SpannableStringBuilder(bold(diag.getName()));
        builder.append(": ");
        builder.append(string);
        prompts.add(builder);
    }

    private void addSupplementalDiagPrompt(Supplemental diag, String string) {
        SpannableStringBuilder builder = new SpannableStringBuilder(bold(diag.getName()));
        builder.append(": ");
        builder.append(string);
        prompts.add(builder);
    }

    private void addVisitData(Visit visit) {
        Context c = VisitSummaryActivity.this;

        TextView age = new TextView(c);

        int years = visit.getPatientAgeYears();
        int months = visit.getPatientAgeMonths();
        int days = visit.getPatientAgeDays();
        age.setText(getString(R.string.age) + ": ");
        if(years > 0) {
            age.append(String.valueOf(years) + " " + getString(R.string.years) + " ");
        }
        if(months > 0) {
            age.append(String.valueOf(months) + " " + getString(R.string.months) + " ");
        }
        if(days > 0) {
            age.append(String.valueOf(days) + " " + getString(R.string.days) + " ");
        }

        TextView gender = new TextView(c);
        gender.setText(getString(R.string.gender) + ": ");
        if (visit.getGender() == 'M') {
            gender.append(getString(R.string.male));
        } else {
            gender.append(getString(R.string.female));
        }

        TextView staffMember = (TextView) findViewById(R.id.tv_summary_staff_member);
        staffMember.setText(getString(R.string.staff_member) + ": ");
        staffMember.append(visit.getStaffMemberName());

        TextView facility = (TextView) findViewById(R.id.tv_summary_centre);
        facility.setText(getString(R.string.center) + ": ");
        facility.append(visit.getFacilityName());

        TextView date = (TextView) findViewById(R.id.tv_summary_date);
        date.setText(getFormattedDate(visit.getVisitDate()));

        TextView opId = new TextView(c);
        opId.setText("OPD ID: ");
        if (visit.getOPD() != 0) {
            opId.append(String.valueOf(visit.getOPD()));
        } else {
            opId.append(getString(R.string.not_recorded));
        }

        TextView isNational = new TextView(c);

        if (visit.getBeneficiaryType() == Visit.national) {
            isNational.append(getString(R.string.national));
        } else {
            isNational.append(getString(R.string.refugee));
        }

        TextView isRevisit = new TextView(c);
        if (visit.getIsRevisit()) {
            isRevisit.append(getString(R.string.revisit));
        } else {
            isRevisit.append(getString(R.string.new_visit));
        }

        TextView consultHeader = new TextView(VisitSummaryActivity.this);
        consultHeader.setText(bold(getString(R.string.consultation_summary)));
        consultHeader.setTextSize(headerSize);
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
        } else {
            TextView alerts = new TextView(this);
            alerts.setText(bold(getString(R.string.alerts)));
            alerts.setTextSize(headerSize);
            ll.addView(alerts);
        }
        int pos = 0;
        for(Spanned alert : prompts) {
            pos++;
            TextView tv = new TextView(this);
            tv.setText(alert);
            ll.addView(tv);
            if(prompts.size() > 1 && pos != prompts.size()) {
                View line = new View(VisitSummaryActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                params.bottomMargin = 4;
                params.topMargin = 4;
                line.setBackgroundColor(Color.DKGRAY);
                line.setLayoutParams(params);
                ll.addView(line);
            }
        }
    }
}
