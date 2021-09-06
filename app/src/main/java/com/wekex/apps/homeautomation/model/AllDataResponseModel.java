package com.wekex.apps.homeautomation.model;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllDataResponseModel {


    public String data_method = "";
    public String power = "";
    public String dnum = "";

    @Ignore
    @Expose
    @SerializedName("data")
    public ArrayList<data> objData = new ArrayList<>();

//    @SerializedName("groups")
    public String groups = "Default";

    public String getGroups() {
        return groups;
    }
    public void setGroups(String groups) {
        this.groups = groups;
    }

    /*@Expose
    @SerializedName("rooms")*/
    public ArrayList<rooms> lst_rooms = new ArrayList<>();

    public String getData_method() {
        return data_method;
    }
    public void setData_method(String data_method) {
        this.data_method = data_method;
    }

    public ArrayList<data> getObjData() {
        return objData;
    }
    public void setObjData(ArrayList<data> objData) { this.objData = objData; }

    public ArrayList<rooms> getLst_rooms() {
        return lst_rooms;
    }
    public void setLst_rooms(ArrayList<rooms> lst_rooms) {
        this.lst_rooms = lst_rooms;
    }

    public String getPower() { return power; }
    public void setPower(String power) { this.power = power; }

    public String getDnum() { return dnum; }
    public void setDnum(String dnum) { this.dnum = dnum; }
}
