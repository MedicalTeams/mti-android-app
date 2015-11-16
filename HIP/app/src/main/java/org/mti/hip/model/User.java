package org.mti.hip.model;

import org.mti.hip.SuperActivity;

import java.util.HashMap;

/**
 * Created by r624513 on 11/4/15.
 */
public class User {

    public static HashMap<String, User> userMap = new HashMap<>();
    private String name;
    // other fields might include "hasSeenVisitInstructions" or such things


    public User(String name) {
        userMap.put(name, this);
        this.name = name;
        SuperActivity.currentUserName = name;
    }

    public String getName() {
        return name;
    }
}
