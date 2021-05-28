package com.wekex.apps.homeautomation.model;

import java.io.Serializable;

public class RoomModel implements Serializable {

    private String name;
    private String id;
    private boolean isChecked = false;


    public boolean isChecked() {
        return isChecked;
    }
    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    ///////////////////////////

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    ///////////////////////////

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
