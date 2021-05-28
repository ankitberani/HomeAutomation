package com.wekex.apps.homeautomation.model;

public class RemoteCounts {

    public String Types = "";
    public int type_counts = 0;
    public int icon = 0;


    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String _remote_name = "";

    public String getTypes() {
        return Types;
    }

    public void setTypes(String types) {
        Types = types;
    }

    public int getType_counts() {
        return type_counts;
    }

    public void setType_counts(int type_counts) {
        this.type_counts = type_counts;
    }

    public String get_remote_name() {
        return _remote_name;
    }

    public void set_remote_name(String _remote_name) {
        this._remote_name = _remote_name;
    }
}
