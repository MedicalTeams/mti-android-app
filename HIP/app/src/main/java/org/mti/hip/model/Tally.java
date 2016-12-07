package org.mti.hip.model;

import java.util.ArrayList;

public class Tally extends ArrayList<Visit> {
    public Tally getUnsynced() {
        Tally unsynced = new Tally();
        for(Visit visit : this) {
            if(visit.getStatus() != Visit.statusSuccess && visit.getStatus() != Visit.statusDuplicate) {
                unsynced.add(visit);
            }
        }
        return unsynced;
    }
}
