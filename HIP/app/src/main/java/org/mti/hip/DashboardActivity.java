package org.mti.hip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mti.hip.model.DeviceStatusResponse;
import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSONManager;
import org.mti.hip.utils.NetworkBroadcastReceiver;
import org.mti.hip.utils.StorageManager;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DashboardActivity extends SuperActivity {

    public static final int tallyFileSyncOverdueThresholdDays = 1;
    public static final int serverConstantsSyncOverdueThresholdDays = 1;
    public static final String LAST_TALLY_FILE_SYNC_TIME_KEY = "lastTallyFileSyncTimeKey";
    public static final String LAST_SERVER_CONSTANTS_SYNC_TIME_KEY = "lastServerConstantsSyncTimeKey";
    private static final String APP_VERSION_KEY = "appversionkey";

    private int backPressCount;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        findViewById(R.id.bt_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, LocationSelectionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


        if (getIntent().getStringExtra(EXTRA_MSG) != null) {
            Toast.makeText(this, getIntent().getStringExtra(EXTRA_MSG), Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.new_visit).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readDeviceStatus().matches(deviceActiveCode)) {
                    startVisit();
                } else {
                    new NetworkTask(HttpClient.getDeviceStatus + StorageManager.getSerialNumber(), HttpClient.get) {

                        @Override
                        public void getResponseString(String response) {
                            DeviceStatusResponse deviceStatusResponse = (DeviceStatusResponse) getJsonManagerInstance().read(response, DeviceStatusResponse.class);
                            writeDeviceStatus(deviceStatusResponse.getStatus());
                            if(deviceStatusResponse.getStatus().matches(deviceActiveCode)) {
                                startVisit();
                            } else {
                                alert.showAlert("Device disabled", "This device has been disabled. Please contact [placeholder] to have it activated.");
                            }
                        }
                    }.execute();

                }
            }
        });

    }

    private void startVisit() {
        Visit visit = getStorageManagerInstance().newVisit();
        visit.setVisitDate(new Date());
        visit.setStaffMemberName(currentUserName);
        visit.setDeviceId(StorageManager.getSerialNumber());
        visit.setFacilityName(facilityName);
        visit.setFacility(readLastUsedFacility());
        VisitDiagnosisListAdapter.stiContactsTreated = -1;
        startActivity(new Intent(DashboardActivity.this, ConsultationActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        backPressCount = 0;

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionCode = pInfo.versionCode;

        Log.d("test", String.valueOf(isServerConstantsSyncOverdue()));

        if(readVersionCode() == 0 || readVersionCode() != versionCode) {
            if(isConnected()) updateDeviceRegistration();
        }

        if(isServerConstantsSyncOverdue() && isConnected()) {
            getServerConstants();
        }

        final TextView connectivityStatus = (TextView) findViewById(R.id.dashboard_connectivity_status);

        String tallyJsonIn = getStorageManagerInstance().readTallyToJsonString(this);

        // TODO delete Tally from after fully synced (all == 0 or 1?) and greater than 24 hours have passed

        if(tallyJsonIn != null) {
            // make object from string
            Tally tally = (Tally) getJsonManagerInstance().read(tallyJsonIn, Tally.class);
            getStorageManagerInstance().setTally(tally);
            if (!tally.isEmpty()) {
                writeTallyToDisk(tally);
            }
        } else { // no tally stored on disk so make a new one
            getStorageManagerInstance().setTally(new Tally());
        }

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkBroadcastReceiver = new NetworkBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                int green = getResources().getColor(R.color.lightgreen);
                int red = getResources().getColor(R.color.colorPrimary);
                if (isConnected()) {
                    connectivityStatus.setText(R.string.is_online);
                    connectivityStatus.setTextColor(green);
                } else {
                    connectivityStatus.setText(R.string.is_offline);
                    connectivityStatus.setTextColor(red);
                }
            }
        };
        registerReceiver(networkBroadcastReceiver, intentFilter);

    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkBroadcastReceiver);
        super.onPause();
    }

    private void updateDeviceRegistration() {

        String serialNumber = StorageManager.getSerialNumber();
        String description = "Device serial number last created/updated on " + new Date();
        String jsonBody = JSONManager.getJsonToPutDevice(serialNumber, String.valueOf(versionCode), description);
        new NetworkTask(jsonBody, HttpClient.devicesEndpoint + "/" + serialNumber, HttpClient.put) {

            @Override
            public void getResponseString(String response) {
                Log.d("Registration response", response);
            }

            @Override
            protected void onPostExecute(String r) {

                if(e == null) {
                    getResponseString(r);
                    // TODO make status response and update stat
                    writeVersionCode();
//                    writeDeviceStatus();
                } else {
                    alert.showAlert("Error", "The request to register this device didn't succeed: Error data:\n" + e.getMessage());
                }
                progressDialog.dismiss();
            }
        }.execute();
    }

    private String readDeviceStatus() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(DEVICE_STATUS_KEY, "");
    }

    private void writeVersionCode() {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit().putInt(APP_VERSION_KEY, versionCode).commit();
    }

    private int readVersionCode() {
        return getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getInt(APP_VERSION_KEY, 0);
    }

    private void getServerConstants() {
        // TODO generally works but needs refactoring to chain the requests. Biggest problem is any exceptions each create their own popup
        progressDialog.hide();
        final ProgressDialog localProgress = new ProgressDialog(this);
        localProgress.setMessage("Updating lists");
        localProgress.setCancelable(false);
        localProgress.show();
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

            @Override
            protected void onPostExecute(String r) {
                super.onPostExecute(r);
                writeLastServerConstantsSyncTime();
                localProgress.dismiss();
            }
        }.execute();
    }



    private void writeTallyToDisk(Tally tally) {

        // TODO refactor this is messy (also... App sends "isSent" to the server and this should eventually be removed but will require some other serialization method

        // make tally string
        String tallyJsonOut = getJsonManagerInstance().writeValueAsString(tally);

        // write to file
        getStorageManagerInstance().writeTallyJsonToFile(tallyJsonOut, this);

        // make tally string from file
        String tallyJsonIn = getStorageManagerInstance().readTallyToJsonString(this);

        // make object from string
        Tally tallyFromJson = (Tally) getJsonManagerInstance().read(tallyJsonIn, Tally.class);
        boolean fullySynced = true;
        int sent = 0;
        int total = 0;
        for (Visit visit : tallyFromJson) {
            total++;
            if(visit.getStatus() == visitStatusSuccess || visit.getStatus() == visitStatusDuplicate) {
                sent++;
            } else {
                // we're not fully synced
                fullySynced = false;
            }
         }

        Log.d("fully synced", String.valueOf(fullySynced));

        TextView status = (TextView) findViewById(R.id.tv_tally_status);
        status.setText("You have sent " + sent + " out of " + total + " visits");
        Log.d("test", readDeviceStatus());
        if(!readDeviceStatus().matches("A")) {
            status.append("\nYour device is currently disabled.");
        }
    }

    @Override
    public void onBackPressed() {
        backPressCount++;
        Toast.makeText(DashboardActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        if (backPressCount == 2) {
            super.onBackPressed();
        }
        new CountDownTimer(2000, 2000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                backPressCount = 0;
            }
        }.start();
    }


    /**
     * Save the last date/time (as UTC milliseconds from the epoch) at which the tally file was successfully sent up to the server
     */
    public void writeLastTallyFileSyncTime() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(LAST_TALLY_FILE_SYNC_TIME_KEY, Calendar.getInstance().getTimeInMillis()).commit();
    }

    /**
     *
     * @return The last date/time (as UTC milliseconds from the epoch) at which the tally file was successfully sent up to the server
     */
    public Long readLastTallyFileSyncTime() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(LAST_TALLY_FILE_SYNC_TIME_KEY, 0L);
    }

    /**
     *
     * @return true if the last time the tally file was successfully sent up to the server was too long ago
     */
    public boolean isTallyFileSyncOverdue() {
        Long diffMillis = Calendar.getInstance().getTimeInMillis() - readLastTallyFileSyncTime();
        int diffDays = (int) TimeUnit.MILLISECONDS.toDays(diffMillis);
        if (diffDays >= tallyFileSyncOverdueThresholdDays) {
            return true;
        }
        return false;
    }

    /**
     * Save the last date/time (as UTC milliseconds from the epoch) at which the constants were successfully downloaded from the server
     */
    public void writeLastServerConstantsSyncTime() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(LAST_SERVER_CONSTANTS_SYNC_TIME_KEY, Calendar.getInstance().getTimeInMillis()).commit();
    }

    /**
     *
     * @return The last date/time (as UTC milliseconds from the epoch) at which the constants were successfully downloaded from the server
     */
    public Long readLastServerConstantsSyncTime() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(LAST_SERVER_CONSTANTS_SYNC_TIME_KEY, 0L);
    }

    /**
     *
     * @return true if the last time the constants were successfully downloaded from the server was too long ago
     */
    public boolean isServerConstantsSyncOverdue() {
        Long diffMillis = Calendar.getInstance().getTimeInMillis() - readLastServerConstantsSyncTime();
        int diffDays = (int) TimeUnit.MILLISECONDS.toDays(diffMillis);
        if (diffDays >= serverConstantsSyncOverdueThresholdDays) {
            return true;
        }
        return false;
    }


}
