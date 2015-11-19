package org.mti.hip.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.mti.hip.model.Diagnosis;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;

/**
 * Created by r624513 on 11/4/15.
 */
public class JSONManager {

    public JSONManager() {
        om = new ObjectMapper();
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private ObjectMapper om;



    public Object read(String json, Class clazz) {
        try {
            return om.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeToJsonString(Object obj) {
        try {
            om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public String getTestJsonString() {
        Tally tally = new Tally();
        Visit visit = new Visit();
        visit.setPatientAgeMonths(40);
        visit.setStaffMemberName("Dr. Robert Bobert");
        visit.setFacility(1234);
        visit.setGender('M');
        visit.setBeneficiaryType(0);
        visit.setIsRevisit(false);
        visit.setOPD(123);
        visit.setVisitDate(new Date());
        HashSet<Diagnosis> diags = new HashSet<>();
        Diagnosis diag = new Diagnosis();
        diag.setName("Diabetes");
        diags.add(diag);
        visit.setPatientDiagnosis(diags);
        tally.add(visit);

        try {
           return om.writeValueAsString(tally);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String writeValueAsString(Object obj) {
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
