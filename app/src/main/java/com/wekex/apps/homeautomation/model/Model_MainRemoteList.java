package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Model_MainRemoteList {

    @SerializedName("UserRemote")
    public ArrayList<Model_UserRemoteList> _lst = new ArrayList<>();

    public ArrayList<Model_UserRemoteList> get_lst() {
        return _lst;
    }

    public void set_lst(ArrayList<Model_UserRemoteList> _lst) {
        this._lst = _lst;
    }
}
