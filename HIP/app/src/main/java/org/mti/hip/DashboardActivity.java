package org.mti.hip;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.mti.hip.model.DeviceRegistrationObj;
import org.mti.hip.model.DeviceStatusResponse;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;
import org.mti.hip.utils.AdvProgressDialog;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSON;
import org.mti.hip.utils.NetworkBroadcastReceiver;
import org.mti.hip.utils.StorageManager;
import org.mti.hip.utils.VisitDiagnosisListAdapter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class DashboardActivity extends SuperActivity {

    private static final int tallyFileSyncOverdueThresholdDays = 1;
    private static final int visitEnteredOverdueThresholdMintues = 30;
    private static final int serverConstantsSyncOverdueThresholdDays = 1;
    private static final String LAST_VISIT_ENTERED_TIME_KEY = "lastTallyFileSyncTimeKey";
    private static final String LAST_TALLY_FILE_SYNC_TIME_KEY = "lastTallyFileSyncTimeKey";
    private static final String LAST_SERVER_CONSTANTS_SYNC_TIME_KEY = "lastServerConstantsSyncTimeKey";
    private static final String APP_VERSION_KEY = "appversionkey";

    private int backPressCount;
    private NetworkBroadcastReceiver networkBroadcastReceiver;
    private int versionCode;
    private Tally tally;
    private boolean needsSync;
    private TextView manualSync;
    private Button connectivityStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        displayMode();

        manualSync = (TextView) findViewById(R.id.bt_manual_sync);
        manualSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTally();
            }
        });
        connectivityStatus = (Button) findViewById(R.id.bt_connectivity_status);
        connectivityStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNetworkMode();
            }
        });

        findViewById(R.id.bt_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, VisitsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.bt_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashboardActivity.this, LocationSelectionActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });

        findViewById(R.id.new_visit).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isConnected()) {
                    startVisit();
                    return;
                }
                if (readDeviceStatus().matches(deviceActiveCode)) {
                    startVisit();
                } else {
                    new NetworkTask(HttpClient.getDeviceStatus + StorageManager.getSerialNumber(), HttpClient.get) {

                        @Override
                        public void getResponseString(String response) {
                            DeviceStatusResponse deviceStatusResponse = JSON.loads(response, DeviceStatusResponse.class);
                            writeDeviceStatus(deviceStatusResponse.getStatus());
                            startVisit();
                        }
                    }.execute();
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
    public boolean onMenuOpened(int featureId, Menu menu) {
        Intent i = new Intent(DashboardActivity.this, SettingsActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return true;
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


        String tallyJson = getStorageManagerInstance().readTallyToJsonString(this);

        if (tallyJson != null && !tallyJson.equals("")) {
            // make object from string
            tally = JSON.loads(tallyJson, Tally.class);
            getStorageManagerInstance().setTally(tally);
            if (!tally.isEmpty()) {
                manageTally();
            }
        } else { // no tally stored on disk so make a new one (will get written to disk on next visit)
            tally = new Tally();
            getStorageManagerInstance().setTally(tally);
        }

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkBroadcastReceiver = new NetworkBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                if (isConnected()) {
                    if(needsSync) {
                        manualSync.setVisibility(View.VISIBLE);
                    }

                    if (readVersionCode() == 0 || readVersionCode() != versionCode) {
                        if (isConnected()) {
                            updateDeviceRegistration();
                        }
                    }

                    if (isServerConstantsSyncOverdue()) {
                        getServerConstants();
                    }
                    connectivityStatus.setText(R.string.is_online);
                    connectivityStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi, 0, 0, 0);
                } else {
                    connectivityStatus.setText(R.string.is_offline);
                    connectivityStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_nowifi, 0, 0, 0);
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

    private void startVisit() {
        if(isVisitEnteredOverdue()) {
            writeLastVisitEnteredTime();
            startActivity(new Intent(DashboardActivity.this, ClinicianSelectionActivity.class));
        } else {
            writeLastVisitEnteredTime();
            Visit visit = getStorageManagerInstance().newVisit();
            visit.setVisitDate(new Date());
            visit.setStaffMemberName(currentUserName);
            visit.setDeviceId(StorageManager.getSerialNumber());
            visit.setFacilityName(facilityName);
            visit.setFacility(readLastUsedFacility());
            VisitDiagnosisListAdapter.stiContactsTreated = -1;
            startActivity(new Intent(DashboardActivity.this, ConsultationActivity.class));
        }
    }

    private void updateDeviceRegistration() {
        String serialNumber = StorageManager.getSerialNumber();
//        String description = "Device serial number last created/updated on " + new Date();
//        String jsonBody = JSONManager.getJsonToPutDevice(serialNumber, String.valueOf(versionCode), description);
        DeviceRegistrationObj regObj = new DeviceRegistrationObj();
        regObj.setUuid(serialNumber);
        regObj.setFacility(readLastUsedFacility());
        regObj.setAppVersion(String.valueOf(versionCode));
        String jsonBody = JSON.dumps(regObj);
        new NetworkTask(jsonBody, HttpClient.devicesEndpoint + "/" + serialNumber, HttpClient.put) {

            @Override
            public void getResponseString(String response) {
                Log.d("Registration response", response);
            }

            @Override
            protected void onPostExecute(String r) {
                if (e == null) {
                    getResponseString(r);
                    writeVersionCode();
                } else {
                    alert.showAlert(getString(R.string.error), getString(R.string.request_2register_no_succeed) + ":\n" + e.getMessage());
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
        final AdvProgressDialog localProgress = new AdvProgressDialog(this);
        localProgress.setMessage(getString(R.string.updating_lists));
        localProgress.show();

        new AsyncTask<Void, Void, Void>() {

            Exception e;
            HttpClient client = getHttpClientInstance();

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
                super.onPostExecute(aVoid);
                if(e != null) {
                    Log.e(DEFAULT_LOG_TAG, "Couldn't get constants " + e.getMessage());
                    Toast.makeText(DashboardActivity.this, R.string.failed_to_retrieve_updated_lists, Toast.LENGTH_SHORT).show();
                } else {
                    writeLastServerConstantsSyncTime();
                }
                localProgress.dismiss();
            }

        }.execute();

    }

    private void sendTally() {
        String tallyJson = JSON.dumps(tally.getLimitedUnsynced());
        Log.d("sendTally", tallyJson);
        new NetworkTask(tallyJson, HttpClient.tallyEndpoint, HttpClient.post) {

            @Override
            public void getResponseString(String response) {
                Log.d("Visit response string", response);
                // update the tally - if tally response contains status == 4 then device is disabled
                Tally serverTally = JSON.loads(response, Tally.class);
                int success = 0;
                int successPastVisit = 0;
                int warnings = 0;
                if(serverTally == null) {
                    warnings = tally.size();
                } else {
                    for (Visit serverVisit : serverTally) {
                        if (serverVisit.getStatus() == Visit.statusDisabled) {
                            writeDeviceStatus("D");
                        }
                        if (serverVisit.getStatus() == Visit.statusSuccess || serverVisit.getStatus() == Visit.statusDuplicate) {
                            if (DateUtils.isToday(serverVisit.getVisitDate().getTime())) {
                                success++;
                            } else {
                                successPastVisit++;
                            }
                        } else {
                            warnings++;
                        }
                        for (Visit visit : tally) {
                            if(visit.getVisitDate().equals(serverVisit.getVisitDate())) {
                                visit.setStatus(serverVisit.getStatus());
                            }
                        }
                    }
                }
                StringBuilder resultsMessageBuilder = new StringBuilder();
                resultsMessageBuilder.append("Visits for today successfully recorded: " + success + "\n");
                if(successPastVisit > 0) {
                    resultsMessageBuilder.append("Visits for past days successfully recorded: " + successPastVisit);
                }
                if(warnings > 0) {
                    resultsMessageBuilder.append("Records that failed and will be sent again during next sync: " + warnings);
                }

                alert.showAlert("Sync results", resultsMessageBuilder.toString());

                manageTally();
            }

        }.execute();
    }

    private void manageTally() {
        int syncedToday = 0;
        int unSyncedToday = 0;
        int syncedAll = 0;
        int unSycnedAll = 0;
        Iterator<Visit> iter = tally.iterator();
        Date now = new Date();
        while (iter.hasNext()) {
            Visit visit = iter.next();
            long diff = now.getTime() - visit.getVisitDate().getTime();
            if (getFormattedDate(now).equals(getFormattedDate(visit.getVisitDate()))) { // Show same day.
                switch (visit.getStatus()) {
                    case Visit.statusDisabled:
                        unSyncedToday++;
                        break;
                    case Visit.statusFailure:
                        unSyncedToday++;
                        break;
                    case Visit.statusDuplicate:
                        syncedToday++;
                        break;
                    case Visit.statusSuccess:
                        syncedToday++;
                        break;
                    case Visit.statusUnsent:
                        unSyncedToday++;
                        break;
                }
            }
            switch (visit.getStatus()) {
                case Visit.statusDisabled:
                    unSycnedAll++;
                    break;
                case Visit.statusFailure:
                    unSycnedAll++;
                    break;
                case Visit.statusDuplicate:
                    syncedAll++;
                    break;
                case Visit.statusSuccess:
                    syncedAll++;
                    break;
                case Visit.statusUnsent:
                    unSycnedAll++;
                    break;
            }
        }

        // Delete after 45 days
        long MAX_RETENTION = 45l * 24l * 60l * 60l * 1000l; // 45 days
        for(int i = tally.size() - 1; i >= 0; i--) {
            Visit visit = tally.get(i);
            long diff = now.getTime() - visit.getVisitDate().getTime();
            if (diff > MAX_RETENTION && // 45 days
                    (visit.getStatus() == Visit.statusSuccess || visit.getStatus() == Visit.statusDuplicate)) {
                tally.remove(i);
            }
        }

        if(unSycnedAll > 0) {
            needsSync = true;
            if(isConnected()) {
                manualSync.setVisibility(View.VISIBLE);
            } else {
                manualSync.setVisibility(View.GONE);
            }
        } else {
            manualSync.setVisibility(View.GONE);
            needsSync = false;
        }

        // make tally string
        String tallyJsonOut = JSON.dumps(tally);

        // write to file with new edits
        getStorageManagerInstance().writeTallyJsonToFile(tallyJsonOut, this);

        TextView status = (TextView) findViewById(R.id.tv_tally_status);

        status.setText("");

        if(syncedToday > 0) {
            status.setText(getResources().getQuantityString(R.plurals.sent_visits, syncedToday, syncedToday));
        }

        if(unSyncedToday > 0) {
            status.setTextColor(ContextCompat.getColor(DashboardActivity.this, R.color.colorPrimary));

            status.append(getResources().getQuantityString(R.plurals.failed_visits, unSyncedToday, unSyncedToday));
        }
        if (!readDeviceStatus().matches(deviceActiveCode)) {
            //Remove for now.
            //status.append("\n" + getString(R.string.ur_device_disabled));
        }
    }

    @Override
    public void onBackPressed() {
        backPressCount++;
        Toast.makeText(DashboardActivity.this, getString(R.string.press_back_2_exit), Toast.LENGTH_SHORT).show();
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
     */
    public void writeLastVisitEnteredTime() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(LAST_VISIT_ENTERED_TIME_KEY, Calendar.getInstance().getTimeInMillis()).commit();
    }

    /**
     */
    public Long readLastVisitEnteredTime() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(LAST_VISIT_ENTERED_TIME_KEY, 0L);
    }

    /**
     */
    public boolean isVisitEnteredOverdue() {
        Long diffMillis = Calendar.getInstance().getTimeInMillis() - readLastVisitEnteredTime();
        int diffDays = (int) TimeUnit.MILLISECONDS.toMinutes(diffMillis);
        if (diffDays >= visitEnteredOverdueThresholdMintues) {
            return true;
        }
        return false;
    }

    /**
     * Save the last date/time (as UTC milliseconds from the epoch) at which the tally file was successfully sent up to the server
     */
    public void writeLastTallyFileSyncTime() {
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(LAST_TALLY_FILE_SYNC_TIME_KEY, Calendar.getInstance().getTimeInMillis()).commit();
    }

    /**
     * @return The last date/time (as UTC milliseconds from the epoch) at which the tally file was successfully sent up to the server
     */
    public Long readLastTallyFileSyncTime() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(LAST_TALLY_FILE_SYNC_TIME_KEY, 0L);
    }

    /**
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
        Log.v(DEFAULT_LOG_TAG, "Server constants downloaded and sync time updated");
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putLong(LAST_SERVER_CONSTANTS_SYNC_TIME_KEY, Calendar.getInstance().getTimeInMillis()).commit();
    }

    /**
     * @return The last date/time (as UTC milliseconds from the epoch) at which the constants were successfully downloaded from the server
     */
    public Long readLastServerConstantsSyncTime() {
        return getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getLong(LAST_SERVER_CONSTANTS_SYNC_TIME_KEY, 0L);
    }

    /**
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
