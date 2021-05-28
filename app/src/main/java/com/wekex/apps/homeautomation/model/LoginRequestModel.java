package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel {


    @SerializedName("user")
    public String user;

    @SerializedName("password")
    public String Password;

    public LoginRequestModel(String user, String password) {
        this.user = user;
        Password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
