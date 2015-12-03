package org.mti.hip;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.mti.hip.model.DeviceStatusResponse;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.NetworkBroadcastReceiver;
import org.mti.hip.utils.StorageManager;

import java.util.Date;

public class MainActivity extends SuperActivity {

    private Button btRegister;
    private String versionCode;
    private boolean initialized;
    private boolean registered;
    private ProgressBar progress;
    private String serialNumber;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private ProgressDialog localProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        if(checkForAppReadiness()) {
            return;
        }

        progressDialog.hide();

        localProgress = new ProgressDialog(this);
        localProgress.setMessage("Please wait...");
        localProgress.setCancelable(false);
        progress = (ProgressBar) findViewById(R.id.progress);


        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkBroadcastReceiver = new NetworkBroadcastReceiver();
        registerReceiver(networkBroadcastReceiver, intentFilter);

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        TextView version = (TextView) findViewById(R.id.tv_version);
        versionCode = String.valueOf(pInfo.versionCode);
        version.setText("Version code: " + versionCode);

        btRegister = (Button) findViewById(R.id.register);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                localProgress.show();
                if(!isConnected()) {
                    alert.showAlert("No network", "Please connect to the internet and try again");
                    localProgress.dismiss();
                    return;
                }
                if(!registered) {
                    register();
                }

               if (initialized && registered){ // text = "Start"
                   new NetworkTask(HttpClient.devicesEndpoint + "/" + serialNumber, HttpClient.get) {

                       @Override
                       public void getResponseString(String response) {
                            Log.d("device status", response);
//
                           DeviceStatusResponse statusResponse = (DeviceStatusResponse)
                                   getJsonManagerInstance().read(response, DeviceStatusResponse.class);

                           writeDeviceStatus(statusResponse.getStatus());
                           if(statusResponse.getStatus().matches(deviceActiveCode)) {
                               startActivity(new Intent(MainActivity.this, LocationSelectionActivity.class));
                               finish();
                           } else {
                               alert.showAlert("Device error", "This device hasn't been activated yet. Please set the device status to Active in the database.");
                           }
                           localProgress.dismiss();
                       }
                   }.execute();

                } else {
                   initApp();
               }
            }
        });
        toggleProgressOverlay(View.INVISIBLE);

    }

    private boolean checkForAppReadiness() {
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

    private void toggleProgressOverlay(int visibility) {
        progress.setVisibility(visibility);
        if(visibility == View.INVISIBLE) {
            btRegister.setVisibility(View.VISIBLE);
        } else {
            btRegister.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkBroadcastReceiver);
    }

    private void register() {
//        toggleProgressOverlay(View.VISIBLE);
        serialNumber = StorageManager.getSerialNumber();
        String description = "Device serial number last created/updated on " + new Date();
        String jsonBody = JSONManager.getJsonToPutDevice(serialNumber, versionCode, description);
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

                if(e == null) {
                    getResponseString(r);
                } else {
                    alert.showAlert("Error", "The request to register this device didn't succeed: Error data:\n" + e.getMessage());
                    localProgress.dismiss();
                }
            }
        }.execute();
    }

    private void initApp() {

//        toggleProgressOverlay(View.VISIBLE);

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
                initialized = true;
                btRegister.setText(getString(R.string.start));
            }

            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                localProgress.dismiss();
            }
        }.execute();

    }


}
