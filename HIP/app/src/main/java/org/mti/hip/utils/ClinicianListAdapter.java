package org.mti.hip.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.model.User;

import java.util.ArrayList;

/**
 * Created by r625361 on 11/30/2015.
 */
public class ClinicianListAdapter extends ArrayAdapter<User> {

    private String lastUsedClinicianName;

    public ClinicianListAdapter(Context context, ArrayList<User> clinicians, String lastUsedClinicianName) {
        super(context, 0, clinicians);
        this.lastUsedClinicianName = lastUsedClinicianName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User clinician = getItem(position);
        String clinicianName = clinician.getName();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_clinician, parent, false);
        }
        // Lookup view for data population
        TextView tvLastUsed = (TextView) convertView.findViewById(R.id.tv_last_used_clinician);
        TextView tvClinicianName = (TextView) convertView.findViewById(R.id.tv_clinician_name);
        // Populate the data into the template view using the data object
        if (null == lastUsedClinicianName) {
            tvLastUsed.setText("");
        }
        else {
            if (clinicianName.equalsIgnoreCase(lastUsedClinicianName)) {
                tvLastUsed.setText("Last Used");
            } else {
                tvLastUsed.setText("");
            }
        }

        tvClinicianName.setText(clinicianName);

        return convertView;
    }

}
