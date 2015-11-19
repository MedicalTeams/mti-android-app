package org.mti.hip.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.FileUtils;
import org.mti.hip.model.Tally;
import org.mti.hip.model.Visit;

import java.io.File;
import java.io.IOException;

/**
 * Created by r624513 on 11/4/15.
 */
public class StorageManager {

    public static final String TALLY_FILENAME = "tally_file";
    public static final String LOG_TAG = "MTI-HIP";

    private Tally tally;

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

}
