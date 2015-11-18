package org.mti.hip.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.OtherDiagnosis;
import org.mti.hip.model.SupplementalDiagnosis;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.util.ArrayList;

/**
 * Created by r624513 on 11/4/15.
 */
public class StorageManager {

    private Tally tally;
    private ObjectMapper om = new ObjectMapper();

    public StorageManager() {
        tally = new Tally();
    }

    public Tally getTally() {
        return tally;
    }

    public Visit newVisit() {
        Visit visit = new Visit();
        tally.add(visit);

        return tally.get(tally.size() -1);
    }

    public Visit currentVisit() {
        return tally.get(tally.size() -1);
    }

    public String writeValueAsString(Object obj) {
        String val = null;
        try {
            val = om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return val;
    }
}
