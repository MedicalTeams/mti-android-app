package org.mti.hip;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mti.hip.model.DeviceStatusResponse;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSON;
import org.mti.hip.utils.NetworkBroadcastReceiver;
import org.mti.hip.utils.StorageManager;

import java.io.IOException;
import java.util.Date;

public class MainActivity extends SuperActivity {

    private Button btRegister;
    private String versionName;
    private boolean initialized;
    private boolean registered;
    private String serialNumber;
    private NetworkBroadcastReceiver networkBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userTimeout.stop();
        setContentView(R.layout.activity_main);
        displayMode();

        if(checkForAppReadiness()) {
            return;
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = (TextView) findViewById(R.id.tv_version);
        versionName = String.valueOf(pInfo.versionName);
        version.setText(getString(R.string.version_code) + " " + versionName);

        btRegister = (Button) findViewById(R.id.register);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected()) {
                    alert.showAlert(getString(R.string.no_network), getString(R.string.plz_connect2_internet_ntry_again));
                    return;
                }
                if(!registered) {
                    register();
                    return;
                }

               if (initialized){ // text = "Start"
                   progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                       @Override
                       public void onDismiss(DialogInterface dialog) {
                           gotoNext();
                       }
                   });
                   new NetworkTask(HttpClient.devicesEndpoint + "/" + serialNumber, HttpClient.get) {

                       @Override
                       public void getResponseString(String response) {
                            Log.d("device status", response);
                            DeviceStatusResponse statusResponse = JSON.loads(response, DeviceStatusResponse.class);

                           writeDeviceStatus(statusResponse.getStatus());
                           if(statusResponse.getStatus().matches(deviceActiveCode)) {
                               progressDialog.dismiss();
                           } else {
                               DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                   }
                               };
                               alert.showPermissionsAlert(listener);
                           }
                       }

                       @Override
                       protected void onPostExecute(String r) {
                           super.onPostExecute(r);
                           userTimeout.stop();
                       }
                   }.execute();

                } else {
                   initApp();
               }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(MainActivity.this, SettingsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return true;
    }

    private void gotoNext(){
        startActivity(new Intent(MainActivity.this, LocationSelectionActivity.class));
        finish();
    }

    public boolean checkForAppReadiness() {
        boolean ready = true;
        if(readLastUsedLocation().matches("")) {
            ready = false;
        }

        if(readLastUsedClinician().matches("")) {
            ready = false;
        }

        if(readLastUsedFacility() == 0) {
            ready = false;
        }
        if(ready) {
            startActivity(new Intent(MainActivity.this, DashboardActivity.class));
            finish();
        }
        return ready;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(networkBroadcastReceiver == null) {
            networkBroadcastReceiver = new NetworkBroadcastReceiver();
        }
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(networkBroadcastReceiver);
        } catch (Exception e){
        }
    }

    private void register() {
        serialNumber = StorageManager.getSerialNumber();
        String description = "Device serial number last created/updated on " + new Date();
        String jsonBody = "{" +
            " \"uuid\": \"" + serialNumber + "\", " +
            "\"appVersion\": \"" + versionName + "\", " +
            "\"description\": \"" + description + "\" " +
            "}";
        new NetworkTask(jsonBody, HttpClient.devicesEndpoint + "/" + serialNumber, HttpClient.put) {

            @Override
            public void getResponseString(String response) {
                registered = true;
                Log.d("Registration response", response);
                initApp();
                btRegister.setText(getString(R.string.start));
            }

            @Override
            protected void onPostExecute(String r) {
                progressDialog.dismiss();
                if (e == null) {
                    getResponseString(r);
                } else {
                    if(e.getMessage() == null || e.getMessage().isEmpty()) {
                        alert.showAlert(getString(R.string.error), getString(R.string.request_2register_no_succeed) + "\n" + "There was a networking issue. Please check your connection and try again.");
                    } else {
                        alert.showAlert(getString(R.string.error), getString(R.string.request_2register_no_succeed) + ":\n" + e.getMessage());
                    }
                }
            }
        }.execute();
    }

    private void initApp() {
        new AsyncTask<Void, Void, Void>() {
            Exception e;
            HttpClient client = getHttpClientInstance();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    writeString(DIAGNOSIS_LIST_KEY, client.get(HttpClient.diagnosisEndpoint));
                    writeString(FACILITIES_LIST_KEY, client.get(HttpClient.facilitiesEndpoint));
                    writeString(SUPPLEMENTAL_LIST_KEY, client.get(HttpClient.supplementalEndpoint));
                    writeString(SETTLEMENT_LIST_KEY, client.get(HttpClient.settlementEndpoint));
                    writeString(INJURY_LOCATIONS_KEY, client.get(HttpClient.injuryLocationsEndpoint));
                } catch (IOException e1) {
                    e = e1;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if(e != null) {
                    alert.showAlert("Error", "Failed to retrieve updated lists.");
                } else {
                    Log.d(DEFAULT_LOG_TAG, "initialized");
                    initialized = true;
                    btRegister.setText(getString(R.string.start));
                }
                progressDialog.dismiss();
                super.onPostExecute(aVoid);
            }

        }.execute();
    }
}
