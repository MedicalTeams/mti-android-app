package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.mti.hip.model.Country;
import org.mti.hip.model.DeviceInfo;

import java.util.ArrayList;

public class SettingsActivity extends SuperActivity {

    private RadioButton rbProduction;
    private RadioButton rbTest;
    private TextView tvVersion;
    private TextView tvDeviceId;
    private Spinner spCountry;
    private ArrayList<Country> countryList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Find elements in layout.
        rbProduction = (RadioButton)findViewById(R.id.rb_production);
        rbTest = (RadioButton)findViewById(R.id.rb_test);
        tvVersion = (TextView)findViewById(R.id.tv_version);
        tvDeviceId = (TextView)findViewById(R.id.tv_device_id);
        spCountry = (Spinner) findViewById(R.id.sp_country);

        // Update text on page.
        String countryCode = getCountryCode();
        String defaultCountryCode = getResources().getString(R.string.country_default);
        String[] countryKeyVal = getResources().getStringArray(R.array.country_array);
        Country selectedCountry = new Country(countryCode, "");
        countryList = new ArrayList<>();
        for(int i = 0; i < countryKeyVal.length; i++) {
            String[] pair = countryKeyVal[i].split(":");
            countryList.add(new Country(pair[0], pair[1]));
        }
        ArrayAdapter<Country> adapter = new ArrayAdapter<Country>(this,
                android.R.layout.simple_spinner_item, countryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCountry.setAdapter(adapter);
        int pos = adapter.getPosition(selectedCountry);
        if (pos == -1) {
            spCountry.setSelection(adapter.getPosition(new Country(defaultCountryCode, "")));
        } else {
            spCountry.setSelection(pos);
        }

        Log.d("Settings", spCountry.getSelectedItem().toString());

        DeviceInfo deviceInfo = getDeviceInfo();
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
        String countryCode = ((Country)spCountry.getSelectedItem()).getId();
        setCountryCode(countryCode);
        // Reset http client so it gets new status and country.
        httpClient = null;

        Intent i = new Intent(SettingsActivity.this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
        return true;
    }
}