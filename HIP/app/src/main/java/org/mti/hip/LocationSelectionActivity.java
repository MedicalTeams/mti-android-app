package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.model.Settlement;
import org.mti.hip.utils.LocationListAdapter;

import java.util.ArrayList;

/**
 * Created by r625361 on 11/19/2015.
 */
public class LocationSelectionActivity extends SuperActivity {

    private ListView lv;
    private ArrayList<Settlement> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_selection);
        displayMode();
        lv = (ListView) findViewById(R.id.location_list);
        getList();


    }

    private void getList() {
//        if (readString(FACILITIES_LIST_KEY).matches("")) {
//            runNetworkTask();
//        } else {
            list = (ArrayList<Settlement>) getObjectFromPrefsKey(SETTLEMENT_LIST_KEY);
            showList();
//        }
    }

    private void showList() {
//        final ArrayAdapter<Settlement> adapter = new ArrayAdapter<>(LocationSelectionActivity.this, android.R.layout.simple_list_item_1, list);
        final ArrayAdapter<Settlement> adapter = new LocationListAdapter(LocationSelectionActivity.this, list, readLastUsedLocation());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = adapter.getItem(position).getName();
                writeLastUsedLocation(name);
                locationName = name;
                startActivity(new Intent(LocationSelectionActivity.this, CentreSelectionActivity.class));
            }
        });
    }


}
