package org.mti.hip.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.mti.hip.R;
import org.mti.hip.SuperActivity;
import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.OtherDiagnosis;
import org.mti.hip.model.SupplementalDiagnosis;
import org.mti.hip.model.Visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by r624513 on 11/5/15.
 */
public class VisitDiagnosisListAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> listHeaders;
    private ArrayList<String> primaryDiagList;
    private ArrayList<String> stiList;
    private ArrayList<String> chronicDiseaseList;
    private ArrayList<String> injuryList;
    private ArrayList<String> mentalIllnessList;
    private ArrayList<String> injuryLocList;
    //private ArrayList<OtherDiagnosis> otherDiags;
    private HashMap<String, ArrayList<String>> children = new HashMap<>();
    private SuperActivity context;
    private int diagId = 0;
    private int stiId = 1;
    private int chronicDiseaseId = 2;
    private int mentalIllnessId = 3;
    private int injuryId = 4;
    private int injuryLocId = 5;
    private HashMap<Integer, RadioButton> buttonMap = new HashMap<>();

    public ExpandableListView.OnChildClickListener listener;


    public VisitDiagnosisListAdapter(SuperActivity context) {
        this.context = context;
        primaryDiagList = getPrimaryDiags();
        stiList = getSTIs();
        chronicDiseaseList = getChronicDiseaseList();
        mentalIllnessList = getMentalIllnesses();
        injuryList = getInjuries();
        injuryLocList = getInjuryLocations();
        listHeaders = getListHeaders();


        children.put(listHeaders.get(diagId), primaryDiagList);
        children.put(listHeaders.get(stiId), stiList);
        children.put(listHeaders.get(chronicDiseaseId), chronicDiseaseList);
        children.put(listHeaders.get(mentalIllnessId), mentalIllnessList);
        children.put(listHeaders.get(injuryId), injuryList);
        children.put(listHeaders.get(injuryLocId), injuryLocList);
        setListener();
    }

    public ExpandableListView.OnChildClickListener getListener() {
        return listener;
    }

    public void setListener() {
        listener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
//                Log.d("int test", "" + groupPosition + childPosition);
//                HashMap<Integer, Boolean> checkMap = new HashMap<>();
//                checkMap.put(groupPosition & childPosition, true);
                String desc = (String) getChild(groupPosition, childPosition);
                if(desc.matches(getString(R.string.other))) {
                    Toast.makeText(context, "Other diag", Toast.LENGTH_SHORT).show();
                }
                if(groupPosition == mentalIllnessId) {
                    for (RadioButton button : buttonMap.values()) {
                        button.setChecked(false);
                    }
                    RadioButton button = (RadioButton) v.findViewById(R.id.rb_single_select);
                    button.setChecked(true);
                    button.setTag(true);

                } else {
                    // this allows the entire view to be clicked on and toggle check boxes rather
                    // than requiring the user to tap the box itself
                    CheckBox cb = (CheckBox) v.findViewById(R.id.cb_multi_select);
                    if(cb.isChecked()) {
                        cb.setChecked(false);
                        cb.setTag(false);
                    } else {
                        cb.setTag(true);
                        cb.setChecked(true);
                    }
                }
                Visit visit = SuperActivity.getStorageManagerInstance().currentVisit();

                return true;
            }
        };
    }

    @Override
    public int getGroupCount() {
        return listHeaders.size();
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return this.children.get(this.listHeaders.get(groupPosition)).size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.children.get(this.listHeaders.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }



    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

//        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView txtListChild;
        Log.d("exadapter", String.valueOf(groupPosition));
            if(groupPosition == mentalIllnessId) {  // sti and injury B are single select
                convertView = inflater.inflate(R.layout.list_item_single_select, null);
               txtListChild = (TextView) convertView
                        .findViewById(R.id.tv_single_select);
                buttonMap.put(childPosition, (RadioButton) convertView.findViewById(R.id.rb_single_select));

            } else {
                convertView = inflater.inflate(R.layout.list_item_multi_select, null);
                txtListChild = (TextView) convertView
                        .findViewById(R.id.tv_multi_select);
                CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_multi_select);
                if(cb.getTag() == true) {
                    cb.setChecked(true);
                }
            }

//        }



        txtListChild.setText(childText);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private ArrayList<String> getPrimaryDiags() {
        ArrayList<String> diagStrings = new ArrayList<>();

        diagStrings.clear();
        diagStrings.add(getString(R.string.diag_malaria_suspected));
        diagStrings.add(getString(R.string.diag_malaria_confirmed));
        diagStrings.add(getString(R.string.diag_URTI));
        diagStrings.add(getString(R.string.diag_LRTI));
        diagStrings.add(getString(R.string.diag_skin_disease));
        diagStrings.add(getString(R.string.diag_eye_disease));
        diagStrings.add(getString(R.string.diag_dental_conditions));
        diagStrings.add(getString(R.string.diag_intestinal_worms));
        diagStrings.add(getString(R.string.diag_watery_diarrhea));
        diagStrings.add(getString(R.string.diag_bloody_diarrhea));
        diagStrings.add(getString(R.string.diag_tubercolosis));
        diagStrings.add(getString(R.string.diag_acute_flaccid_paralysis_polio));
        diagStrings.add(getString(R.string.diag_measles));
        diagStrings.add(getString(R.string.diag_meningitis));
        diagStrings.add(getString(R.string.diag_hiv_aids));
//        diagStrings.add(getString(R.string.diag_sti_non_hiv_aids));
        diagStrings.add(getString(R.string.diag_acute_malnutrition));
        diagStrings.add(getString(R.string.diag_anemia));
//        diagStrings.add(getString(R.string.diag_chronic_disease));
//        diagStrings.add(getString(R.string.diag_mental_illness));
//        diagStrings.add(getString(R.string.diag_injuries));
        diagStrings.add(getString(R.string.diag_ear_disease));
        diagStrings.add(getString(R.string.diag_urinary_tract_infection));
        diagStrings.add(getString(R.string.diag_pudx));
        diagStrings.add(getString(R.string.other));

        return diagStrings;
    }

    private ArrayList<String> getChronicDiseaseList() {
        ArrayList<String> chronicDiseases = new ArrayList<>();
        chronicDiseases.clear();
        chronicDiseases.add(getString(R.string.chronic_disease_cancer));
        chronicDiseases.add(getString(R.string.chronic_disease_cardiovascular_disease));
        chronicDiseases.add(getString(R.string.chronic_disease_cerebrovascular_disease));
        chronicDiseases.add(getString(R.string.chronic_disease_digestive_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_endocrine_and_metabolic_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_gynaecological_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_haemotological_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_musculoskeletal_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_nervous_system_disorder));
        chronicDiseases.add(getString(R.string.chronic_disease_respiratory_disease));
        chronicDiseases.add(getString(R.string.other));

        return chronicDiseases;
    }

    private ArrayList<String> getInjuryLocations() {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        list.add(getString(R.string.injury_location_home));
        list.add(getString(R.string.injury_location_school));
        list.add(getString(R.string.injury_location_camp));
        list.add(getString(R.string.injury_location_field_or_garden));
        list.add(getString(R.string.injury_location_bush_or_forest));
        list.add(getString(R.string.injury_location_road));

        return list;
    }

    private ArrayList<String> getSTIs() {
        ArrayList<String> stis = new ArrayList<>();

        stis.clear();
        stis.add(getString(R.string.sti_urethral_discharge_syndrome));
        stis.add(getString(R.string.sti_vaginal_discharge_syndrome));
        stis.add(getString(R.string.sti_genital_ulcer_disease));
        stis.add(getString(R.string.sti_opthamalia_neonatorum));
        stis.add(getString(R.string.sti_congenital_syphillis));
        stis.add(getString(R.string.other));

        return stis;
    }

    private ArrayList<String> getMentalIllnesses() {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        list.add(getString(R.string.mental_health_epilepsy_seizures));
        list.add(getString(R.string.mental_health_alcohol_or_substance_abuse));
        list.add(getString(R.string.mental_health_mental_retardation_intellectual_disability));
        list.add(getString(R.string.mental_health_psychotic_disorder));
        list.add(getString(R.string.mental_health_severe_emotional_disorder));
        list.add(getString(R.string.mental_health_medically_unexplained_somatic_complaint));
        list.add(getString(R.string.mental_health_psychotic_disorder));
        list.add(getString(R.string.other));
        return list;

    }

    private ArrayList<String> getListHeaders() {

        ArrayList<String> headers = new ArrayList<>();

        headers.clear();
        headers.add(getString(R.string.header_diagnosis));
        headers.add(getString(R.string.header_sti));
        headers.add(getString(R.string.header_chronic_disease));
        headers.add(getString(R.string.header_mental_illness));
        headers.add(getString(R.string.header_injuries));
        headers.add(getString(R.string.header_injury_location));
        return headers;
    }


    private ArrayList<String> getInjuries() {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        list.add(getString(R.string.injuries_accident));
        list.add(getString(R.string.injuries_self_harm));
        list.add(getString(R.string.injuries_assault_with_weapon));
        list.add(getString(R.string.injuries_assault_no_weapon));
        list.add(getString(R.string.injuries_animal));
        list.add(getString(R.string.injuries_burn));
        list.add(getString(R.string.injuries_unknown));
        list.add(getString(R.string.other));
        return list;
    }

    private String getString(int id) {
        return context.getString(id);
    }


}
