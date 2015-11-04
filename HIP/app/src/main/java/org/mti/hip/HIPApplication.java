package org.mti.hip;

import android.app.Application;

import org.mti.hip.model.Diagnosis;

import java.util.ArrayList;

/**
 * Created by r624513 on 11/4/15.
 */
public class HIPApplication extends Application {

    public ArrayList<Diagnosis> masterDiagnoses = new ArrayList<>();
    private ArrayList<String> diagStrings = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        populateMasterDiagnoses();

    }

    private void populateMasterDiagnoses() {
        diagStrings.clear();
        diagStrings.add(getString(R.string.action_settings));
    }
}
