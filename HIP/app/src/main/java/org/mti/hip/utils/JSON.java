package org.mti.hip.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;

public class JSON {
    private static ObjectMapper om = null;

    private static ObjectMapper getOM() {
        if(om == null) {
            om = new ObjectMapper();
            om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
        return om;
    }

    public static <T> T loads(String json, Class<T> valueType) {
        try {
            return getOM().readValue(json, valueType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String dumps(Object obj) {
        try {
            return getOM().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
