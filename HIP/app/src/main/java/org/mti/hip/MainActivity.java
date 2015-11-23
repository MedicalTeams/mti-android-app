package org.mti.hip;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mti.hip.utils.HttpClient;
import org.w3c.dom.Text;

public class MainActivity extends SuperActivity {

    private Button signIn;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        version = (TextView) findViewById(R.id.tv_version);
        version.setText("Version code: " + String.valueOf(pInfo.versionCode));

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
//                if(readString(DIAGNOSIS_LIST_KEY).matches(""))// TODO this is dumb right now and assumes all values are initialized if DIAG is
                    initApp();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

            }
        }.execute();
        signIn = (Button) findViewById(R.id.sign_in);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationSelectionActivity.class));
                finish();
            }
        });

    }

    private void toggleProgressOverlay() {
        // TODO toggle visibility of Sign In button and progress
        // spinner (block sign in until Async Task is done)
    }

    private void initApp() {
        new NetworkTask(HttpClient.diagnosisEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                writeString(DIAGNOSIS_LIST_KEY, response);
            }
        }.execute();

        new NetworkTask(HttpClient.facilitiesEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                writeString(FACILITIES_LIST_KEY, response);
            }
        }.execute();

        new NetworkTask(HttpClient.supplementalEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                writeString(SUPPLEMENTAL_LIST_KEY, response);
            }
        }.execute();

        new NetworkTask(HttpClient.settlementEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                writeString(SETTLEMENT_LIST_KEY, response);
            }
        }.execute();

        new NetworkTask(HttpClient.injuryLocationsEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                writeString(SuperActivity.INJURY_LOCATIONS_KEY, response);
            }
        }.execute();

    }





}
