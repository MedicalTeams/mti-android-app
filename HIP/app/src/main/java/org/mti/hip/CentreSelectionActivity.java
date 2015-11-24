package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.model.Centre;
import org.mti.hip.model.CentreWrapper;
import org.mti.hip.utils.HttpClient;

import java.util.ArrayList;

public class CentreSelectionActivity extends SuperActivity {

    private ListView lv;
    private ArrayList<Centre> list;
    private String settlementMatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centre_selection);
        lv = (ListView) findViewById(android.R.id.list);
        settlementMatcher = readLastUsedLocation();

//        testJson();
        getFacilities();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void getFacilities() {

        if (readString(FACILITIES_LIST_KEY).matches("")) {
            runNetworkTask();
        } else {
            list = (ArrayList<Centre>) getJsonManagerInstance().read(readString(FACILITIES_LIST_KEY), CentreWrapper.class);
            showList();
        }


    }

    private void runNetworkTask() {
        new NetworkTask(HttpClient.facilitiesEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                list = (ArrayList<Centre>) getJsonManagerInstance().read(response, CentreWrapper.class);
                showList();
            }
        }.execute();
    }

    private void showList() {
        ArrayList<Centre> innerList = new ArrayList<>();
        for(Centre facility : list) {
            if(facility.getSettlement().contains(locationName)) {
                innerList.add(facility);
            }
        }
        final ArrayAdapter<Centre> adapter = new ArrayAdapter<>(CentreSelectionActivity.this, android.R.layout.simple_list_item_1, innerList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Centre facility = adapter.getItem(position);
                facilityName = facility.getName();
                writeLastUsedFacility(facility.getId());
                // TODO record ID
                startActivity(new Intent(CentreSelectionActivity.this, ClinicianSelectionActivity.class));
            }
        });
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_next, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_next:
//
//                return true;
//            default:
//                return false;
//        }
//    }


}
