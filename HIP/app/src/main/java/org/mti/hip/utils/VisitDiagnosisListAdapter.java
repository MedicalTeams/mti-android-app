package org.mti.hip.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.mti.hip.R;
import org.mti.hip.SuperActivity;
import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.InjuryLocation;
import org.mti.hip.model.Supplemental;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by r624513 on 11/5/15.
 */
public class VisitDiagnosisListAdapter extends BaseExpandableListAdapter {

    private String customOtherName;
    private HashMap<Integer, Integer> selectableOthers = new HashMap<>();
    private ArrayList<String> listHeaders;
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

    private static final int primaryOtherId = 23;
    private static final int chronicOtherId = 54;

    public static int stiContactsTreated = -1;
    public static int customContactsTreatedId = -1;

    private ArrayList<Integer> removedDiagHeaders = new ArrayList<>();

    private ArrayList<Supplemental> supplementals;

    private static HashMap<Integer, ArrayList<RadioButton>> buttonMap = new HashMap<>();

    public ExpandableListView.OnChildClickListener listener;
    private int customContactsTreatedListPos;

    public VisitDiagnosisListAdapter(SuperActivity context) {
        this.context = context;

        removedDiagHeaders.add(16);
        removedDiagHeaders.add(19);
        removedDiagHeaders.add(20);
        removedDiagHeaders.add(21);

        supplementals = (ArrayList<Supplemental>) context.getObjectFromPrefsKey(SuperActivity.SUPPLEMENTAL_LIST_KEY);
        primaryDiagList = getPrimaryDiags();
        stiList = getSTIs();
        chronicDiseaseList = getChronicDiseaseList();
        mentalIllnessList = getMentalIllnesses();
        injuryList = getInjuries();
        injuryLocList = getInjuryLocations();

        children.put(diagId, primaryDiagList);
        children.put(stiId, stiList);
        children.put(chronicDiseaseId, chronicDiseaseList);
        children.put(mentalIllnessId, mentalIllnessList);
        children.put(injuryId, injuryList);
        children.put(injuryLocId, injuryLocList);


        if (check_states.isEmpty()) {

            setChildrenAndValues();
        }
        setListener();
    }

    public ExpandableListView.OnChildClickListener getListener() {
        return listener;
    }

    //  set checkbox states
    public static ArrayList<ArrayList<Integer>> check_states = new ArrayList<>();
    // TODO refactor to not be static (currently only referenced from a few places)

    public void setChildrenAndValues() {
        for (int i = 0; i < children.size(); i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (int j = 0; j < children.get(i).size(); j++) {
                tmp.add(1);
            }
            check_states.add(tmp);
        }

        buttonMap.put(injuryLocId, new ArrayList<RadioButton>());
        buttonMap.put(mentalIllnessId, new ArrayList<RadioButton>());
    }

    public void updateChildrenAndValues(int groupPosition, int childPosition) {
        check_states.get(groupPosition).add(childPosition - 1, 0);
    }

    public void setListener() {
        listener = new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == mentalIllnessId || groupPosition == injuryLocId) {
                    RadioButton button = (RadioButton) v.findViewById(R.id.rb_single_select);
                    boolean check = false;

                    // TODO refactor. Works but is messy.


                    if (check_states.get(groupPosition).get(childPosition) == 0) {
                        // we've touched the same one twice
                        button.setChecked(false);
                    } else {
                        check = true; // this currently has to "jump over" the next block of IFs
                        // but should be refactored
                    }

                    //clear all selections and state for radio buttons
                    for (int i = 0; i < buttonMap.get(groupPosition).size(); i++) {
                        RadioButton allButtons = buttonMap.get(groupPosition).get(i);
                        allButtons.setChecked(false);
                    }
                    for (int i = 0; i < check_states.get(groupPosition).size(); i++) {
                        check_states.get(groupPosition).set(i, 1);
                    }

                    // conditionally add new selection and state

                    button.setChecked(check);
                    if (check) {
                        check_states.get(groupPosition).set(childPosition, 0);
                    } else {
                        // mark as unchecked and don't allow mental health other entry
                        // (this allows the "other" to be unchecked instead of triggering
                        // the dialog again on the uncheck)
                        return true;
                    }
                    if (groupPosition == mentalIllnessId) {
                        Supplemental supp = (Supplemental) getChild(groupPosition, childPosition);
                        if (supp.getId() == 62) {
                            // mental illness other
                            setMentalHealthOther(supp);
                        }
                    }
                } else {

                    CheckBox cb = (CheckBox) v.findViewById(R.id.cb_multi_select);

                    // this prevents others and sti contacts from being added to checked state
                    boolean isOther = processIfIsOther(groupPosition, childPosition);
                    boolean isStiContacts = false;
                    isStiContacts = processIfIsStiContactsTreated(groupPosition, childPosition);
                    if (stiContactsTreated == -1) {
                        processIfIsSti(groupPosition, childPosition);
                    }
                    if (isOther || isStiContacts) {
                        return true;
                    }
                    // this allows the entire view to be clicked on and toggle check boxes rather
                    // than requiring the user to tap the box itself

                    if (cb.isChecked()) {
                        check_states.get(groupPosition).set(childPosition, 1);
                        cb.setChecked(false);
                    } else {
                        check_states.get(groupPosition).set(childPosition, 0);
                        cb.setChecked(true);
                    }
                }
                return true;
            }
        };
    }

    private void processIfIsSti(int groupPosition, int childPosition) {
        if (groupPosition == stiId) {
            Supplemental supp = (Supplemental) getChild(groupPosition, childPosition);
            ArrayList<Integer> blockedIds = new ArrayList<>();
            blockedIds.add(40);
            blockedIds.add(41);
            blockedIds.add(customContactsTreatedId);
            if (blockedIds.contains(supp.getId())) {
                return;
            } else {
                showStiContactsDialog();
            }
        }


    }

    private boolean processIfIsStiContactsTreated(int group, int child) {
        if (group == stiId) {
            Supplemental supp = (Supplemental) getChild(group, child);
            if (supp.getId() == customContactsTreatedId) {
                showStiContactsDialog();
                return true;
            }
        }
        return false;
    }

    private boolean processIfIsOther(final int groupPosition, int childPosition) {
        if (!selectableOthers.containsValue(childPosition)) {
            return false;
        }
        Object obj = getList(groupPosition).get(childPosition);
        Diagnosis diag;
        Supplemental supp;
        if (obj instanceof Diagnosis) {
            diag = (Diagnosis) obj;
            if (diag.getId() == primaryOtherId) {
                addOtherDiag(false, primaryOtherId, groupPosition);
                return true;
            }
        } else if (obj instanceof Supplemental) {
            supp = (Supplemental) obj;
            if (supp.getId() == chronicOtherId) {
                addOtherDiag(true, chronicOtherId, groupPosition);
                return true;
            }
        }

        return false;

    }

    public void showStiContactsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        TextView tv = (TextView) view.findViewById(R.id.dialog_message_text);
        tv.setText(context.getString(R.string.tooltip_morbidity_sti_contacts));
        final EditText et = (EditText) view.findViewById(R.id.et_dialog);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setHint(getString(R.string.no_of_contacts_treated));
        alert.setView(view);
        alert.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (context.editTextHasContent(et)) {
                    String input = et.getText().toString();
                    stiContactsTreated = Integer.valueOf(input);
                    Supplemental supp = (Supplemental) getChild(stiId, customContactsTreatedListPos);
                    supp.setName(context.parseStiContactsTreated(stiContactsTreated));
                    notifyDataSetChanged();

                }

                dialog.dismiss();
            }
        });
        final AlertDialog dialog = alert.create();
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void addOtherDiag(final boolean isSupplemental, final int id, final int groupPosition) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        TextView tv = (TextView) view.findViewById(R.id.dialog_message_text);
        tv.setText(getString(R.string.plz_enter_dx_name));
        final EditText et = (EditText) view.findViewById(R.id.et_dialog);
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et.setHint(getString(R.string.dx_name));
        customOtherName = "";
        alert.setView(view);
        alert.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customOtherName = String.valueOf(et.getText().toString().trim());
                dialog.dismiss();
            }
        });
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (customOtherName.matches("")) return;
                if (isSupplemental) {
                    ArrayList<Supplemental> list = getList(groupPosition);
                    Supplemental supp = new Supplemental();
                    supp.setId(id);
                    supp.setDiagnosis(19); // would need to be 20 for mental health
                    supp.setName(customOtherName);
                    list.add(supp);

                } else {
                    ArrayList<Diagnosis> list = getList(groupPosition);
                    Diagnosis diag = new Diagnosis();
                    diag.setName(customOtherName);
                    diag.setId(id);
                    list.add(diag);
                }


                updateChildrenAndValues(groupPosition, children.get(groupPosition).size());
                notifyDataSetChanged();
            }
        });
        final AlertDialog dialog = alert.create();
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void setMentalHealthOther(final Supplemental other) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edittext, null);
        TextView tv = (TextView) view.findViewById(R.id.dialog_message_text);
        tv.setText(getString(R.string.plz_enter_dx_name));
        final EditText et = (EditText) view.findViewById(R.id.et_dialog);
        et.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et.setHint(getString(R.string.dx_name));
        customOtherName = "";
        alert.setView(view);
        alert.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                customOtherName = String.valueOf(et.getText().toString().trim());
                dialog.dismiss();
            }
        });
        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (customOtherName.matches("")) return;
                other.setName(customOtherName);
                notifyDataSetChanged();
            }
        });
        final AlertDialog dialog = alert.create();
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
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
                break;
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
                break;
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
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_group, parent, false);
        }

        TextView tv = (TextView) v.findViewById(R.id.lblListHeader);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setText(headerTitle);
        return v;

    }

    public HashMap<Integer, Integer> getSelectableOthers() {
        return selectableOthers;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = null;
        int id;
        boolean selected = check_states.get(groupPosition).get(childPosition) == 0;
        if (groupPosition == diagId) {
            final Diagnosis obj = (Diagnosis) getChild(groupPosition, childPosition);
            childText = obj.getName();
            id = obj.getId();
        } else {
            final Supplemental obj = (Supplemental) getChild(groupPosition, childPosition);
            childText = obj.getName();
            id = obj.getId();
        }


        LayoutInflater inflater = (LayoutInflater) this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView tv = null;
        View selector;

        if (groupPosition == mentalIllnessId || groupPosition == injuryLocId) {  // sti and injury B are single select
            convertView = inflater.inflate(R.layout.list_item_single_select, null);
            tv = (TextView) convertView.findViewById(R.id.tv_single_select);

            selector = convertView.findViewById(R.id.rb_single_select);
            buttonMap.get(groupPosition).add((RadioButton) selector);
            ((RadioButton) selector).setChecked(selected);

        } else {
            convertView = inflater.inflate(R.layout.list_item_multi_select, null);
            tv = (TextView) convertView
                    .findViewById(R.id.tv_multi_select);
            selector = convertView.findViewById(R.id.cb_multi_select);

            ((CheckBox) selector).setChecked(selected);
            if (groupPosition == chronicDiseaseId && id == 47) {
                setupEndochrineTooltip(convertView);
            }

        }
        if (id == chronicOtherId || id == primaryOtherId || id == customContactsTreatedId) {

            // IMPORTANT

            /* All "others" have the same id, but we only want the
                first entry of "other" to trigger the dialog. Without
                the following code, a user could tap on the new custom
                entries and that would make the dialog appear. The code
                prevents new entries from being added into the selectableOthers
                HashMap if they have already been entered when the list is
                shown by getChildView
            */

            if (!selectableOthers.containsKey(groupPosition))
                selectableOthers.put(groupPosition, childPosition);

            // this code hides the CheckBox widget
            if (selectableOthers.containsValue(childPosition))
                selector.setVisibility(View.INVISIBLE);

        }


        tv.setText(childText);
        return convertView;

    }

    private void setupEndochrineTooltip(View convertView) {
        View tooltip = convertView.findViewById(R.id.tooltip_multi_select);
        tooltip.setVisibility(View.VISIBLE);
        tooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.alert.showAlert(getString(R.string.info), context.getString(R.string.tooltip_morbidity_endocrine_metabolic));
            }
        });
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private ArrayList<Diagnosis> getPrimaryDiags() {
        ArrayList<Diagnosis> diags = (ArrayList<Diagnosis>) context.getObjectFromPrefsKey(SuperActivity.DIAGNOSIS_LIST_KEY);

        ArrayList<Diagnosis> displayList = new ArrayList<>();

        listHeaders = new ArrayList<>();
        listHeaders.clear();
        listHeaders.add(getString(R.string.header_diagnosis));

        for (Diagnosis diagnosis : diags) {
            if (removedDiagHeaders.contains(diagnosis.getId())) {
                // the diagnosis is not allowed
                listHeaders.add(diagnosis.getName());
            } else if (!displayList.contains(diagnosis)) {
                displayList.add(diagnosis);
            }
        }

        listHeaders.add(getString(R.string.header_injury_location));


        return displayList;
    }

    private ArrayList<Supplemental> getChronicDiseaseList() {
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if (supp.getDiagnosis() == 19) {
                parsedList.add(supp);
            }
        }
        return parsedList;
    }

    private ArrayList<Supplemental> getSTIs() {
//        stis.add("Contacts Treated");
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        int count = 0;
        for (Supplemental supp : supplementals) {
            if (supp.getDiagnosis() == 16) {
                count++;
                parsedList.add(supp);
            }
        }
        Supplemental supp = new Supplemental();
        supp.setName(context.getString(R.string.contacts_treated));
        supp.setId(customContactsTreatedId);
        customContactsTreatedListPos = count++;

        parsedList.add(supp);

        return parsedList;
    }

    private ArrayList<Supplemental> getMentalIllnesses() {
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if (supp.getDiagnosis() == 20) {
                parsedList.add(supp);
            }
        }
        return parsedList;
    }

    private ArrayList<Supplemental> getInjuries() {
        ArrayList<Supplemental> parsedList = new ArrayList<>();
        for (Supplemental supp : supplementals) {
            if (supp.getDiagnosis() == 21) {
                parsedList.add(supp);
            }
        }
        return parsedList;
    }


    private ArrayList<Supplemental> getInjuryLocations() {
        ArrayList<InjuryLocation> list = (ArrayList<InjuryLocation>) context.getObjectFromPrefsKey(SuperActivity.INJURY_LOCATIONS_KEY);


        ArrayList<Supplemental> diags = new ArrayList<>();
        for (InjuryLocation injuryLocation : list) {
            Supplemental diag = new Supplemental();
            diag.setId(injuryLocation.getId());
            diag.setName(injuryLocation.getName());
            diag.setDiagnosis(injuryLocation.getDiagnosis());
            diags.add(diag);
        }
        return diags;
    }

    private String getString(int id) {
        return context.getString(id);
    }


}
