package org.mti.hip.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
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
import org.mti.hip.model.SupplementalDiagnosis;
import org.mti.hip.model.Visit;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by r624513 on 11/5/15.
 */
public class VisitDiagnosisListAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> listHeaders;
    private ArrayList<Diagnosis> primaryDiagList;
    private ArrayList<SupplementalDiagnosis> stiList;
    private ArrayList<SupplementalDiagnosis> chronicDiseaseList;
    private ArrayList<SupplementalDiagnosis> injuryList;
    private ArrayList<SupplementalDiagnosis> mentalIllnessList;
    private ArrayList<SupplementalDiagnosis> injuryLocList;
    //private ArrayList<OtherDiagnosis> otherDiags;
    private HashMap<String, Object> children = new HashMap<>();
    private SuperActivity context;
    private static final int diagId = 0;
    private static final int stiId = 1;
    private static final int chronicDiseaseId = 2;
    private static final int mentalIllnessId = 3;
    private static final int injuryId = 4;
    private static final int injuryLocId = 5;
    private HashMap<Integer, RadioButton> buttonMap = new HashMap<>();

    public ExpandableListView.OnChildClickListener listener;


    public class ViewHolder
    {
        private HashMap<Integer, View> storedViews = new HashMap<Integer, View>();

        public ViewHolder()
        {
        }

        /**
         *
         * @param view
         *            The view to add; to reference this view later, simply refer to its id.
         * @return This instance to allow for chaining.
         */
        public ViewHolder addView(View view)
        {
            int id = view.getId();
            storedViews.put(id, view);
            return this;
        }

        public View getView(int id)
        {
            return storedViews.get(id);
        }
    }

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
                Visit visit = SuperActivity.getStorageManagerInstance().currentVisit();
//                String desc = (String) getChild(groupPosition, childPosition);
                String header = listHeaders.get(groupPosition);



//                if(desc.matches(getString(R.string.other))) {
//                    Toast.makeText(context, "Other diag", Toast.LENGTH_SHORT).show();
//                }
                if(groupPosition == mentalIllnessId) {
                    for (RadioButton button : buttonMap.values()) {
                        button.setChecked(false);
                    }
                    ArrayList<SupplementalDiagnosis> list = (ArrayList<SupplementalDiagnosis>) children.get(header);
                    RadioButton button = (RadioButton) v.findViewById(R.id.rb_single_select);
                    button.setChecked(true);
                    button.setTag(true);
                    Diagnosis diagnosis = new Diagnosis();
                    diagnosis.setDescription(header);
                    diagnosis.getSupplementalDiags().add(list.get(childPosition));
                    // what needs to happen here is the diag of mental illness needs to be added
                    // to the master visit diags
                    // with a child supplemental diag of whatever illness was chosen

                    visit.getDiags().add(diagnosis);
                    // TODO avoid adding dupes (either through HashMap or extending ArrayList add)
                    Log.d("Visit test", context.getStorageManagerInstance().writeValueAsString(visit));
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
        ArrayList child = null;
        switch (groupPosition) {
            case diagId:
                child = (ArrayList<Diagnosis>) children.get(listHeaders.get(groupPosition));
                default:
                    child = (ArrayList<SupplementalDiagnosis>) children.get(listHeaders.get(groupPosition));

        }
        return child.size();
    }


    @Override
    public Object getGroup(int groupPosition) {
        return listHeaders.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList child = null;
        switch (groupPosition) {
            case diagId:
                child = (ArrayList<Diagnosis>) children.get(listHeaders.get(groupPosition));
            default:
                child = (ArrayList<SupplementalDiagnosis>) children.get(listHeaders.get(groupPosition));

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
            ViewHolder holder = new ViewHolder();
            holder.addView(v.findViewById(R.id.lblListHeader));
            v.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) v.getTag();
        TextView tv = (TextView) holder.getView(R.id.lblListHeader);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setText(headerTitle);
        return v;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final SupplementalDiagnosis obj = (SupplementalDiagnosis) getChild(groupPosition, childPosition);

        String childText = obj.getDescription();
        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tv = null;
        if (convertView == null) {
            View selector;
            ViewHolder holder = new ViewHolder();

            if (groupPosition == mentalIllnessId || groupPosition == injuryLocId) {  // sti and injury B are single select
                convertView = inflater.inflate(R.layout.list_item_single_select, null);
                tv = (TextView) convertView.findViewById(R.id.tv_single_select);

                selector = convertView.findViewById(R.id.rb_single_select);
                buttonMap.put(childPosition, (RadioButton) selector);

            } else {
                convertView = inflater.inflate(R.layout.list_item_multi_select, null);
                tv = (TextView) convertView
                        .findViewById(R.id.tv_multi_select);
                selector = (CheckBox) convertView.findViewById(R.id.cb_multi_select);
                if (selector.getTag() == true) {
//                    selector.setChecked(true);
                }

//                holder.addView(tv);
            }
            convertView.setTag(holder);
            holder.addView(tv);
            holder.addView(selector);
        }

        // Get the stored ViewHolder that also contains our views
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (groupPosition == mentalIllnessId || groupPosition == injuryLocId) {
            tv = (TextView) holder.getView(R.id.tv_single_select);
        } else {
            tv = (TextView) holder.getView(R.id.tv_multi_select);
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
            diag.setDescription(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<SupplementalDiagnosis> getChronicDiseaseList() {
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


        ArrayList<SupplementalDiagnosis> diags = new ArrayList<>();
        for (String s : chronicDiseases) {
            SupplementalDiagnosis diag = new SupplementalDiagnosis();
            diag.setDescription(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<SupplementalDiagnosis> getInjuryLocations() {
        ArrayList<String> list = new ArrayList<>();
        list.clear();
        list.add(getString(R.string.injury_location_home));
        list.add(getString(R.string.injury_location_school));
        list.add(getString(R.string.injury_location_camp));
        list.add(getString(R.string.injury_location_field_or_garden));
        list.add(getString(R.string.injury_location_bush_or_forest));
        list.add(getString(R.string.injury_location_road));


        ArrayList<SupplementalDiagnosis> diags = new ArrayList<>();
        for (String s : list) {
            SupplementalDiagnosis diag = new SupplementalDiagnosis();
            diag.setDescription(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<SupplementalDiagnosis> getSTIs() {
        ArrayList<String> stis = new ArrayList<>();

        stis.clear();
        stis.add(getString(R.string.sti_urethral_discharge_syndrome));
        stis.add(getString(R.string.sti_vaginal_discharge_syndrome));
        stis.add(getString(R.string.sti_genital_ulcer_disease));
        stis.add(getString(R.string.sti_opthamalia_neonatorum));
        stis.add(getString(R.string.sti_congenital_syphillis));
        stis.add(getString(R.string.other));

        ArrayList<SupplementalDiagnosis> diags = new ArrayList<>();
        for (String s : stis) {
            SupplementalDiagnosis diag = new SupplementalDiagnosis();
            diag.setDescription(s);
            diags.add(diag);
        }
        return diags;
    }

    private ArrayList<SupplementalDiagnosis> getMentalIllnesses() {
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
        ArrayList<SupplementalDiagnosis> diags = new ArrayList<>();
        for (String s : list) {
            SupplementalDiagnosis diag = new SupplementalDiagnosis();
            diag.setDescription(s);
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


    private ArrayList<SupplementalDiagnosis> getInjuries() {
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
        ArrayList<SupplementalDiagnosis> diags = new ArrayList<>();
        for (String s : list) {
            SupplementalDiagnosis diag = new SupplementalDiagnosis();
            diag.setDescription(s);
            diags.add(diag);
        }
        return diags;
    }

    private String getString(int id) {
        return context.getString(id);
    }


}
