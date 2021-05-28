package com.wekex.apps.homeautomation.model;

public class device_model {


    public boolean isTypeCustome = false;
    public String device_name;
    public int device_drawble;
    String iconpath = "";

    public device_model(String device_name, int device_drawble, boolean isTypeCustome, String iconpath) {
        this.device_name = device_name;
        this.device_drawble = device_drawble;
        this.isTypeCustome = isTypeCustome;
        this.iconpath = iconpath;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getDevice_drawble() {
        return device_drawble;
    }

    public void setDevice_drawble(int device_drawble) {
        this.device_drawble = device_drawble;
    }

    public boolean isTypeCustome() {
        return isTypeCustome;
    }

    public void setTypeCustome(boolean typeCustome) {
        isTypeCustome = typeCustome;
    }

    public String getIconpath() {
        return iconpath;
    }

    public void setIconpath(String iconpath) {
        this.iconpath = iconpath;
    }


}
