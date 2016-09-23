package org.mti.hip;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;

public class SettingsActivity extends SuperActivity {

    private RadioButton rbProduction;
    private RadioButton rbTest;
    private TextView tvVersion;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        rbProduction = (RadioButton)findViewById(R.id.rb_production);
        rbTest = (RadioButton)findViewById(R.id.rb_test);
        tvVersion = (TextView)findViewById(R.id.tv_version);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText(pInfo.versionName + " (" + Integer.toString(pInfo.versionCode) + ")");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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