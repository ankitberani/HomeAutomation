package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserRemoteListModel {


    @SerializedName("dno")
    public String dno = "";

    @SerializedName("ir")
    public ArrayList<ir_remotes> _ir_remotes = new ArrayList<>();

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public ArrayList<ir_remotes> get_ir_remotes() {
        return _ir_remotes;
    }

    public void set_ir_remotes(ArrayList<ir_remotes> _ir_remotes) {
        this._ir_remotes = _ir_remotes;
    }



    public class keys {

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
