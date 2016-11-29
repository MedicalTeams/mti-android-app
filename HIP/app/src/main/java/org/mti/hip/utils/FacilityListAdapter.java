package org.mti.hip.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.model.Facilities;
import org.mti.hip.model.Facility;

public class FacilityListAdapter extends ArrayAdapter<Facility> {

    private int lastUsedCentreId;

    public FacilityListAdapter(Context context, Facilities facilities, int lastUsedFacilityId) {
        super(context, 0, facilities);
        this.lastUsedCentreId = lastUsedFacilityId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Facility facility = getItem(position);
        int facilityId = facility.getId();
        String facilityName = facility.getName();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_facility, parent, false);
        }
        // Lookup view for data population
        TextView tvLastUsed = (TextView) convertView.findViewById(R.id.tv_last_used_facility);
        TextView tvFacilityName = (TextView) convertView.findViewById(R.id.tv_facility_name);
        // Populate the data into the template view using the data object
        if (0 == lastUsedCentreId) {
            tvLastUsed.setText("");
        }
        else {
            if (facilityId == lastUsedCentreId) {
                tvLastUsed.setText(R.string.last_used);
                tvLastUsed.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                tvLastUsed.setText("");
            }
        }
        tvFacilityName.setText(facilityName);

        return convertView;
    }
}
