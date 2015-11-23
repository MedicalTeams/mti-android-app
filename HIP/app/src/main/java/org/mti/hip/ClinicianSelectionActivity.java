package org.mti.hip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.mti.hip.model.User;

import java.util.ArrayList;

public class ClinicianSelectionActivity extends SuperActivity {

    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinician_selection);
        lv = (ListView) findViewById(android.R.id.list);
        ArrayList<String> list = new ArrayList<>();
        list.add("007");
        list.add("Testy McTesterson");
        list.add("Businge Robert");
        list.add("Aguma Godfrey");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(ClinicianSelectionActivity.this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String userName = adapter.getItem(position);
                currentUserName = userName;
                new User(userName);
                Intent i = new Intent(ClinicianSelectionActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();

            }
        });

    }
}
