package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

public class SuccessResponse {


    @SerializedName("success")
    public Boolean success = false;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
