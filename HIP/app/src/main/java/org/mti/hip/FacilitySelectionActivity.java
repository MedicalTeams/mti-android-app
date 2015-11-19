package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.utils.HttpClient;

import java.io.IOException;
import java.util.ArrayList;

public class FacilitySelectionActivity extends SuperActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_selection);
        lv = (ListView) findViewById(android.R.id.list);
        ArrayList<String> list = new ArrayList<>();
        list.add("Nakivale H/C III");
        list.add("Juru H/C II");
        list.add("Kibengo H/C II");
        list.add("Rubondo H/C II");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(FacilitySelectionActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                facilityName = adapter.getItem(position);
                startActivity(new Intent(FacilitySelectionActivity.this, ClinicianSelectionActivity.class));
            }
        });
//        testJson();
        getFacilities();
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
        try {
            String facilitiesJson = getHttpClientInstance().get(HttpClient.facilitiesEndpoint);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
