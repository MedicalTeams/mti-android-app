package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by r625361 on 11/19/2015.
 */
public class LocationSelectionActivity extends SuperActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
        lv = (ListView) findViewById(R.id.location_list);
        ArrayList<String> locationList = new ArrayList<>();
        locationList.add("Adjumani");
        locationList.add("Ikafe");
        locationList.add("Imvepi");
        locationList.add("Kiryandongo");
        locationList.add("Kyaka II");
        locationList.add("Kyangwali");
        locationList.add("Madi Okollo");
        locationList.add("Nakivale");
        locationList.add("Nyakabande");
        locationList.add("Oruchinga");
        locationList.add("Palorinya");
        locationList.add("Rhino Camp");
        locationList.add("Rwamwanja");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(LocationSelectionActivity.this, android.R.layout.simple_list_item_1, locationList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                locationName = adapter.getItem(position);
                startActivity(new Intent(LocationSelectionActivity.this, FacilitySelectionActivity.class));
            }
        });
    }

    /**
     *
     * @return
     */
    private List<String> getLocations() {
        //// TODO: 11/19/2015 obtain locations from endpoint
        return null;
    }
}
