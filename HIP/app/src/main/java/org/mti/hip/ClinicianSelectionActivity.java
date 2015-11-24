package org.mti.hip;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.mti.hip.model.User;
import org.mti.hip.utils.StorageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ClinicianSelectionActivity extends SuperActivity {

    private ListView lv;
    private HashSet<User> userList;
    private AlertDialog alertDialog;
    private Button add;
    private ArrayAdapter<User> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinician_selection);
        lv = (ListView) findViewById(android.R.id.list);
        add = (Button) findViewById(R.id.bt_add_new_staff_member);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ClinicianSelectionActivity.this);
                alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = adapter.getItem(position);
                        userList.remove(user);
                        adapter.remove(user);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setTitle("Delete Staff Member?");
                alert.setMessage("Do you want to remove this staff member from the list?");
                alert.show();

                return true;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                User user = adapter.getItem(position);
                currentUserName = user.getName();
                Intent i = new Intent(ClinicianSelectionActivity.this, DashboardActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getObjectFromPrefsKey(USER_LIST_KEY) != null) {
            userList = (HashSet<User>) getObjectFromPrefsKey(USER_LIST_KEY);
        } else {
            userList = new HashSet<>();
        }

        ArrayList<User> uiList = new ArrayList<>(userList);
        adapter = new ArrayAdapter<>(ClinicianSelectionActivity.this, android.R.layout.simple_list_item_1, uiList);
        lv.setAdapter(adapter);


    }

    @Override
    protected void onPause() {
        super.onPause();
        String obj = getJsonManagerInstance().writeValueAsString(userList);
        Log.d("list", obj);
        writeString(USER_LIST_KEY, obj);
    }

    private void showDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(ClinicianSelectionActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialog = inflater.inflate(R.layout.dialog_edittext, null);
        TextView tv = (TextView) dialog.findViewById(R.id.dialog_message_text);
        tv.setText(getString(R.string.enter_your_name));
        final EditText et = (EditText) dialog.findViewById(R.id.et_dialog);
        et.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et.setHint("Your name");

        alert.setView(dialog);
        alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editTextHasContent(et)) {
                    String input = et.getText().toString();
                    User user = new User();
                    user.setName(input);
                    userList.add(user);
                    adapter.add(user);
                    adapter.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        });
        alertDialog = alert.create();
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        alertDialog.show();
    }
}
