package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class AllDataResponseModelWithStatus {


    @SerializedName("status")
    public boolean status = false;

    @SerializedName("msg")
    public String msg = "";

    @SerializedName("data")
    public _data _objData;


    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public _data get_objData() {
        return _objData;
    }

    public void set_objData(_data _objData) {
        this._objData = _objData;
    }

    public static class _data {

        public String data_method = "";

        @Expose
        @SerializedName("data")
        public ArrayList<data> objData = new ArrayList<>();


        @SerializedName("groups")
        public String groups = "Default";

        public String getGroups() {
            return groups;
        }

        public void setGroups(String groups) {
            this.groups = groups;
        }

        @Expose
        @SerializedName("rooms")
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

        public void setObjData(ArrayList<data> objData) {
            this.objData = objData;
        }

        public ArrayList<rooms> getLst_rooms() {
            return lst_rooms;
        }

        public void setLst_rooms(ArrayList<rooms> lst_rooms) {
            this.lst_rooms = lst_rooms;
        }
    }
}
