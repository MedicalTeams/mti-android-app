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
import org.mti.hip.model.DiagnosisWrapper;
import org.mti.hip.model.Supplemental;
import org.mti.hip.model.SupplementalsWrapper;

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

    private ArrayList<Supplemental> supplementals;

    private HashMap<Integer, RadioButton> buttonMap = new HashMap<>();

    public ExpandableListView.OnChildClickListener listener;


    public VisitDiagnosisListAdapter(SuperActivity context) {
        this.context = context;
        supplementals = (ArrayList<Supplemental>)
                context.getJsonManagerInstance().read(context.readString(
                        context.SUPPLEMENTAL_LIST_KEY), SupplementalsWrapper.class);
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
//                Log.d("test", String.valueOf(i));
                tmp.add(1);
            }
            check_states.add(tmp);
        }
    }

    public void updateChildrenAndValues() {
        for(int i = 0; i < children.size(); i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
//            check_states.get(i).clear();
            for(int j = 0; j < children.get(i).size(); j++) {
                try {
                    if (check_states.get(i).get(j) == 1) {
                        tmp.add(1); // not selected
                    } else {
                        tmp.add(0); // selected
                    }
                } catch (Exception ignored) {

                }
            }

//            check_states.add(i, tmp);
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
//                        processIfIsOther(groupPosition, childPosition);
                        check_states.get(groupPosition).set(childPosition, 0);
                        cb.setChecked(true);
                    }
                }
                return true;
            }
        };
    }

    private void processIfIsOther(int groupPosition, int childPosition) {
        if(groupPosition != diagId) {
            // needs to be supplemental with named "parent" based on accordion
        } else {
            // these are primaries
            ArrayList<Diagnosis> list = getList(groupPosition);
            if(list.get(childPosition).getName().matches("Other")) {
                context.alert.showAlert("Enter Diagnosis Name", "name is set as a placeholder for now");
                Diagnosis diag = new Diagnosis();
                diag.setName("Placeholder");
                diag.setId(90210);
                list.add(diag);
                children.put(groupPosition, list);
                updateChildrenAndValues();
                notifyDataSetChanged();
            }

        }
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
        ArrayList<Diagnosis> diags = (ArrayList<Diagnosis>)
                context.getJsonManagerInstance().read(context.readString(
                        context.DIAGNOSIS_LIST_KEY), DiagnosisWrapper.class);

        return diags;
    }

    private ArrayList<Supplemental> getChronicDiseaseList() {
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
           if(supp.getDiagnosis() == 19) {
               parsedList.add(supp);
           }
        }
        return parsedList;
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
//        stis.add("Contacts Treated");
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if(supp.getDiagnosis() == 16) {
                parsedList.add(supp);
            }
        }
        return parsedList;
    }

    private ArrayList<Supplemental> getMentalIllnesses() {
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if(supp.getDiagnosis() == 20) {
                parsedList.add(supp);
            }
        }
        return parsedList;
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
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if(supp.getDiagnosis() == 21) {
                parsedList.add(supp);
            }
        }
        return parsedList;
    }

    private String getString(int id) {
        return context.getString(id);
    }


}
