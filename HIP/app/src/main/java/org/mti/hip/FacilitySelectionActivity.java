package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.model.Facility;
import org.mti.hip.model.FacilityWrapper;
import org.mti.hip.utils.HttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class FacilitySelectionActivity extends SuperActivity {

    private ListView lv;
    private ArrayList<Facility> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_selection);
        lv = (ListView) findViewById(android.R.id.list);


//        testJson();
        getFacilities();
    }

    @Override
    protected void onResume() {

        super.onResume();
    }

    private void testJson() {
        String json = getJsonManagerInstance().getTestJsonString();
        try {
            getHttpClientInstance().post("/Visit", json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFacilities() {

        if (readString(FACILITIES_LIST_KEY).matches("")) {
            runNetworkTask();
        } else {
            list = (ArrayList<Facility>) getJsonManagerInstance().read(readString(FACILITIES_LIST_KEY), FacilityWrapper.class);
            showList();
        }


    }

    private void runNetworkTask() {
        new NetworkTask(HttpClient.facilitiesEndpoint, HttpClient.get) {

            @Override
            public void getResponseString(String response) {
                list = (ArrayList<Facility>) getJsonManagerInstance().read(response, FacilityWrapper.class);
                showList();
            }
        }.execute();
    }

    private void showList() {
        ArrayList<Facility> innerList = new ArrayList<>();
        for(Facility facility : list) {
            if(facility.getSettlement().contains(locationName)) {
                innerList.add(facility);
            }
        }
        final ArrayAdapter<Facility> adapter = new ArrayAdapter<>(FacilitySelectionActivity.this, android.R.layout.simple_list_item_1, innerList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                facilityName = adapter.getItem(position).getName();

                // TODO record ID
                startActivity(new Intent(FacilitySelectionActivity.this, ClinicianSelectionActivity.class));
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
