package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Model_UserRemoteList {


    @SerializedName("R_Type")
    public String R_Type = "";

    @SerializedName("Name")
    public String Name = "";

    @SerializedName("R_Brand")
    public String R_Brand = "";

    @SerializedName("Format")
    public int Format = 0;

    @SerializedName("keys")
    public ArrayList<keys> _lst_key = new ArrayList<>();

    public String getR_Type() {
        return R_Type;
    }

    public void setR_Type(String r_Type) {
        R_Type = r_Type;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getR_Brand() {
        return R_Brand;
    }

    public void setR_Brand(String r_Brand) {
        R_Brand = r_Brand;
    }

    public int getFormat() {
        return Format;
    }

    public void setFormat(int format) {
        Format = format;
    }

    public ArrayList<keys> get_lst_key() {
        return _lst_key;
    }

    public void set_lst_key(ArrayList<keys> _lst_key) {
        this._lst_key = _lst_key;
    }


    public static class keys {

        @SerializedName("key")
        public String key = "";

        @SerializedName("value")
        public String value = "";

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }


}
