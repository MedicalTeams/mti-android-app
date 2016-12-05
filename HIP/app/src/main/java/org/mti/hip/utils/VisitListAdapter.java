package org.mti.hip.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.util.Iterator;

public class VisitListAdapter extends ArrayAdapter<Visit> {

    public VisitListAdapter(Context context, Tally visits) {
        super(context, 0, visits);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_visit, parent, false);
        }
        // Get the data item for this position
        Visit visit = getItem(position);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
        tvStatus.setText("" + visit.getStatus());

        TextView tvAge = (TextView) convertView.findViewById(R.id.tv_age);
        tvAge.setText("" + visit.getPatientAgeYears());

        String diagnosesString = "";
        visit.getPatientDiagnosis();
        Iterator<Diagnosis> iter = visit.getPatientDiagnosis().iterator();
        while(iter.hasNext()) {
            Diagnosis diagnosis = iter.next();
            diagnosesString += diagnosis.getName() + ",";
        }
        TextView tvDiagnosis = (TextView) convertView.findViewById(R.id.tv_diagnosis);
        tvDiagnosis.setText(diagnosesString);

        TextView tvVisitDate = (TextView) convertView.findViewById(R.id.tv_visit_date);
        tvVisitDate.setText(visit.getVisitDate().toString());
        return convertView;
    }
}
