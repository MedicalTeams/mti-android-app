package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;

import org.mti.hip.model.DeviceInfo;

public class SettingsActivity extends SuperActivity {

    private RadioButton rbProduction;
    private RadioButton rbTest;
    private TextView tvVersion;
    private TextView tvDeviceId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DeviceInfo deviceInfo = getDeviceInfo();
        rbProduction = (RadioButton)findViewById(R.id.rb_production);
        rbTest = (RadioButton)findViewById(R.id.rb_test);
        tvVersion = (TextView)findViewById(R.id.tv_version);
        tvDeviceId = (TextView)findViewById(R.id.tv_device_id);
        tvVersion.setText(deviceInfo.getVersionName() + " (" + Integer.toString(deviceInfo.getVersionCode()) + ")");
        tvDeviceId.setText(deviceInfo.getDeviceId());
        if(getIsProductionMode()) {
            rbProduction.setChecked(true);
        } else {
            rbTest.setChecked(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(rbProduction.isChecked()) {
            setIsProductionMode(true);
        } else {
            setIsProductionMode(false);
        }

        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return true;
    }
}