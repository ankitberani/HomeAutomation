package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class d2 {

    @Expose
    @SerializedName("state")
    public boolean state = false;

    @Expose
    @SerializedName("name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
