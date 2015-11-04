package org.mti.hip.model;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by r624513 on 11/4/15.
 */
public class Tally extends ArrayList<Visit> {

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
