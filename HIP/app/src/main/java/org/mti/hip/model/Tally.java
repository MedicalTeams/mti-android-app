package org.mti.hip.model;

import java.util.ArrayList;

public class Tally extends ArrayList<Visit> {

    final int MAX_UNSYNCED_VISITS = 150;

    /**
     * Get unsynced tally but only return enough that can be
     * synced.
     * @return Tally list.
     */
    public Tally getLimitedUnsynced() {
        Tally unsynced = new Tally();
        int unSyncedVisitCount = 0;
        for(Visit visit : this) {
            if(visit.getStatus() != Visit.statusSuccess &&
                    visit.getStatus() != Visit.statusDuplicate &&
                    unSyncedVisitCount < MAX_UNSYNCED_VISITS) {
                unsynced.add(visit);
                unSyncedVisitCount++;
            }
        }
        return unsynced;
    }
}
