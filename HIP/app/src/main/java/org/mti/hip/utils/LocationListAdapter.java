package org.mti.hip.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.model.Settlement;

import java.util.ArrayList;

/**
 * Created by r625361 on 11/30/2015.
 */
public class LocationListAdapter extends ArrayAdapter<Settlement> {

    private String lastUsedLocation;

    public LocationListAdapter(Context context, ArrayList<Settlement> locations, String lastUsedLocation) {
        super(context, 0, locations);
        this.lastUsedLocation = lastUsedLocation;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Settlement settlement = getItem(position);
        String settlementName = settlement.getName();
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_location, parent, false);
        }
        // Lookup view for data population
        TextView tvLastUsed = (TextView) convertView.findViewById(R.id.tv_last_used_location);
        TextView tvLocationName = (TextView) convertView.findViewById(R.id.tv_location_name);
        // Populate the data into the template view using the data object
        if (null == lastUsedLocation) {
            tvLastUsed.setText("");
        } else {
            if (settlementName.equalsIgnoreCase(lastUsedLocation)) {
                tvLastUsed.setText(R.string.last_used);
                tvLastUsed.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            } else {
                tvLastUsed.setText("");
            }
        }
        tvLocationName.setText(settlementName);

        return convertView;
    }

}
