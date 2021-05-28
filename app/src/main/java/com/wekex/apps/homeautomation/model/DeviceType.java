package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

public class DeviceType {

    boolean isSelected = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @SerializedName("ID")
    int ID = 0;

    @SerializedName("Name")
    String Name = "";

    public int _icon;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int get_icon() {
        return _icon;
    }

    public void set_icon(int _icon) {
        this._icon = _icon;
    }
}
