package org.mti.hip.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.SuperActivity;
import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Supplemental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by r624513 on 11/5/15.
 */
public class VisitDiagnosisListAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> listHeaders;
    //private ArrayList<OtherDiagnosis> otherDiags;
    private HashMap<Integer, ArrayList> children = new HashMap<>();
    private ArrayList<Diagnosis> primaryDiagList;
    private ArrayList<Supplemental> stiList;
    private ArrayList<Supplemental> chronicDiseaseList;
    private ArrayList<Supplemental> injuryList;
    private ArrayList<Supplemental> mentalIllnessList;
    private ArrayList<Supplemental> injuryLocList;
    private SuperActivity context;
    private static final int diagId = 0;
    private static final int stiId = 1;
    private static final int chronicDiseaseId = 2;
    private static final int mentalIllnessId = 3;
    private static final int injuryId = 4;
    private static final int injuryLocId = 5;
   
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



        children.put(diagId, primaryDiagList);
        children.put(stiId, stiList);
        children.put(chronicDiseaseId, chronicDiseaseList);
        children.put(mentalIllnessId, mentalIllnessList);
        children.put(injuryId, injuryList);
        children.put(injuryLocId, injuryLocList);

        setChildrenAndValues();
        setListener();
    }

    public ExpandableListView.OnChildClickListener getListener() {
        return listener;
    }
    //  set checkbox states
    public ArrayList<ArrayList<Integer>> check_states = new ArrayList<>();
    public void setChildrenAndValues() {
        for(int i = 0; i < children.size(); i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int j = 0; j < children.get(i).size(); j++) {
                tmp.add(1);
            }
            check_states.add(tmp);
        }
    }

    public void setListener() {
        listener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if(groupPosition == mentalIllnessId || groupPosition == injuryLocId) {
                    for(int i = 0; i < buttonMap.size();i++) {
                            RadioButton button = buttonMap.get(i);
                            button.setChecked(false);
                    }
                    for(int i = 0; i < check_states.get(groupPosition).size();i++) {
                        check_states.get(groupPosition).set(i, 1);
                }

                RadioButton button = (RadioButton) v.findViewById(R.id.rb_single_select);
                button.setChecked(true);
                check_states.get(groupPosition).set(childPosition, 0);
            } else {

                    // this allows the entire view to be clicked on and toggle check boxes rather
                    // than requiring the user to tap the box itself
                    CheckBox cb = (CheckBox) v.findViewById(R.id.cb_multi_select);
                    if (cb.isChecked()) {
                        check_states.get(groupPosition).set(childPosition, 1);
                        cb.setChecked(false);
                    } else {
                        processIfIsStiContactsTreated(groupPosition, childPosition);
                        check_states.get(groupPosition).set(childPosition, 0);
                        cb.setChecked(true);
                    }
                }


                return true;
            }
        };
    }

    private void processIfIsStiContactsTreated(int group, int child) {
        if(group != diagId) {
            Supplemental supp = (Supplemental) getChild(group, child);
            if (supp.getName().matches("Contacts Treated")) {
                context.alert.showAlert("Soon", "");
            }
        }
    }

    @Override
    public int getGroupCount() {
        return listHeaders.size();
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList child = null;
        switch (groupPosition) {
            case diagId:
                child = (ArrayList<Diagnosis>) children.get(groupPosition);
                default:
                    child = (ArrayList<Supplemental>) children.get(groupPosition);

        }
        return child.size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listHeaders.get(groupPosition);
    }

    public ArrayList getList(int groupPosition) {
        return children.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList child = null;
        switch (groupPosition) {
            case diagId:
                child = (ArrayList<Diagnosis>) children.get(groupPosition);
            default:
                child = (ArrayList<Supplemental>) children.get(groupPosition);

        }
        return child.get(childPosition);
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

        View v = convertView;
        if (v == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView tv = (TextView) v.findViewById(R.id.lblListHeader);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setText(headerTitle);
        return v;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = null;
        boolean selected = check_states.get(groupPosition).get(childPosition) == 0;
        if (groupPosition == diagId) {
            final Diagnosis obj = (Diagnosis) getChild(groupPosition, childPosition);
            childText = obj.getName();
        } else {
            final Supplemental obj = (Supplemental) getChild(groupPosition, childPosition);
            childText = obj.getName();
        }


        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tv = null;
            View selector;

            if (groupPosition == mentalIllnessId || groupPosition == injuryLocId) {  // sti and injury B are single select
                convertView = inflater.inflate(R.layout.list_item_single_select, null);
                tv = (TextView) convertView.findViewById(R.id.tv_single_select);

                selector = convertView.findViewById(R.id.rb_single_select);
                buttonMap.put(childPosition, (RadioButton) selector);
                ((RadioButton) selector).setChecked(selected);

            } else {
                convertView = inflater.inflate(R.layout.list_item_multi_select, null);
                tv = (TextView) convertView
                        .findViewById(R.id.tv_multi_select);
                selector = convertView.findViewById(R.id.cb_multi_select);
                ((CheckBox) selector).setChecked(selected);
            }

        tv.setText(childText);
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private ArrayList<Diagnosis> getPrimaryDiags() {
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

        ArrayList<Diagnosis> diags = new ArrayList<>();
        for (String s : diagStrings) {
            Diagnosis diag = new Diagnosis();
            diag.setName(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<Supplemental> getChronicDiseaseList() {
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


        ArrayList<Supplemental> diags = new ArrayList<>();
        for (String s : chronicDiseases) {
            Supplemental diag = new Supplemental();
            diag.setName(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<Supplemental> getInjuryLocations() {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        list.add(getString(R.string.injury_location_home));
        list.add(getString(R.string.injury_location_school));
        list.add(getString(R.string.injury_location_camp));
        list.add(getString(R.string.injury_location_field_or_garden));
        list.add(getString(R.string.injury_location_bush_or_forest));
        list.add(getString(R.string.injury_location_road));


        ArrayList<Supplemental> diags = new ArrayList<>();
        for (String s : list) {
            Supplemental diag = new Supplemental();
            diag.setId(new Random().nextInt());
            diag.setName(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<Supplemental> getSTIs() {
        ArrayList<String> stis = new ArrayList<>();

        stis.clear();
        stis.add(getString(R.string.sti_urethral_discharge_syndrome));
        stis.add(getString(R.string.sti_vaginal_discharge_syndrome));
        stis.add(getString(R.string.sti_genital_ulcer_disease));
        stis.add(getString(R.string.sti_opthamalia_neonatorum));
        stis.add(getString(R.string.sti_congenital_syphillis));
        stis.add("Contacts Treated");
        stis.add(getString(R.string.other));

        ArrayList<Supplemental> diags = new ArrayList<>();
        for (String s : stis) {
            Supplemental diag = new Supplemental();
            diag.setName(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<Supplemental> getMentalIllnesses() {
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
        ArrayList<Supplemental> diags = new ArrayList<>();
        for (String s : list) {
            Supplemental diag = new Supplemental();
            diag.setName(s);
            diags.add(diag);
        }
        return diags;

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


    private ArrayList<Supplemental> getInjuries() {
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
        ArrayList<Supplemental> diags = new ArrayList<>();
        for (String s : list) {
            Supplemental diag = new Supplemental();
            diag.setName(s);
            diags.add(diag);
        }
        return diags;
    }

    private String getString(int id) {
        return context.getString(id);
    }


}
