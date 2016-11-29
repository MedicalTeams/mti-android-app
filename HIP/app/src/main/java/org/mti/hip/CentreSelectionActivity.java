package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.model.Facility;
import org.mti.hip.model.Facilities;
import org.mti.hip.utils.FacilityListAdapter;
import org.mti.hip.utils.HttpClient;
import org.mti.hip.utils.JSON;

import java.util.ArrayList;

public class CentreSelectionActivity extends SuperActivity {

    private ListView lv;
    private Facilities list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_selection);
        displayMode();
        lv = (ListView) findViewById(android.R.id.list);
        getFacilities();
    }
    private void getFacilities() {
        if (readString(FACILITIES_LIST_KEY).matches("")) {
            runNetworkTask();
        } else {
            list = JSON.loads(readString(FACILITIES_LIST_KEY), Facilities.class);
            showList();
        }
    }

    private void runNetworkTask() {
        new NetworkTask(HttpClient.facilitiesEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                list = JSON.loads(response, Facilities.class);
                showList();
            }
        }.execute();
    }

    private void showList() {
        Facilities innerList = new Facilities();
        for(Facility facility : list) {
            if(facility.getSettlement().contains(locationName)) {
                innerList.add(facility);
            }
        }
        final ArrayAdapter<Facility> adapter = new FacilityListAdapter(CentreSelectionActivity.this, innerList, readLastUsedFacility());
                lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Facility facility = adapter.getItem(position);
                writeLastUsedFacility(facility.getId());
                writeLastUsedFacilityName(facility.getName());
                startActivity(new Intent(CentreSelectionActivity.this, ClinicianSelectionActivity.class));
            }
        });
    }

}
