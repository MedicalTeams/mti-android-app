package org.mti.hip.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.mti.hip.SuperActivity;

import java.util.HashMap;

/**
 * Created by r624513 on 11/4/15.
 */
public class User {

    private String name;
    // other fields might include "hasSeenVisitInstructions" or such things

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return !(name != null ? !name.equals(user.name) : user.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
