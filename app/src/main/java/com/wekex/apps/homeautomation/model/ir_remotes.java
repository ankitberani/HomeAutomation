package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ir_remotes {
    @SerializedName("Name")
    public String Name = "";

    @SerializedName("R_Type")
    public String R_Type = "";

    @SerializedName("R_Brand")
    public String R_Brand = "";

    @SerializedName("R_MOdel")
    public String R_MOdel = "";


    @SerializedName("Format")
    public String Format = "";

    @SerializedName("keys")
    public ArrayList<Model_UserRemoteList.keys> _lst_key = new ArrayList<>();

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getR_Type() {
        return R_Type;
    }

    public void setR_Type(String r_Type) {
        R_Type = r_Type;
    }

    public String getR_Brand() {
        return R_Brand;
    }

    public void setR_Brand(String r_Brand) {
        R_Brand = r_Brand;
    }

    public String getR_MOdel() {
        return R_MOdel;
    }

    public void setR_MOdel(String r_MOdel) {
        R_MOdel = r_MOdel;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public ArrayList<Model_UserRemoteList.keys> get_lst_key() {
        return _lst_key;
    }

    public void set_lst_key(ArrayList<Model_UserRemoteList.keys> _lst_key) {
        this._lst_key = _lst_key;
    }
}
