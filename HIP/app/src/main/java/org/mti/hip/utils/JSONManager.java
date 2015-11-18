package org.mti.hip.utils;

import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.SupplementalDiagnosis;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class JSONManager {

    public JSONManager() {
        om = new ObjectMapper();
        om.disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);
    }

    private ObjectMapper om;



    public void read(String json) {
    }

    public void writeToJsonString(Object obj) {
        try {
            om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getTestJsonString() {
        om = new ObjectMapper();
        Tally tally = new Tally();
        Visit visit = new Visit();
        visit.setAgeMonths(40);
        visit.setClinician("Dr. Robert Bobert");
        visit.setFacility("Nakivale HC");
        visit.setGender('M');
        visit.setIsNational(true);
        visit.setIsRevisit(false);
        visit.setOpId(123);
        visit.setDate(new Date());
        HashSet<Diagnosis> diags = new HashSet<>();
        Diagnosis diag = new Diagnosis();
        diag.setDescription("Diabetes");
        diags.add(diag);
        visit.setDiags(diags);
        tally.add(visit);

        try {
           return om.writeValueAsString(tally);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
