package com.wekex.apps.homeautomation.model;

import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class data {

    @Expose
    @SerializedName("dno")
    public String dno = "";

    @Expose
    @SerializedName("key")
    public String key = "";

    @Expose
    @SerializedName("dtype")
    public int dtype = 0;

    @Expose
    @SerializedName("duser")
    public String duser = "";

    @Expose
    @SerializedName("name")
    public String name = "";

    @Expose
    @SerializedName("room")
    public String room = "";

    @Expose
    @SerializedName("enable")
    public boolean enable = false;

    @Expose
    @SerializedName("isOnline")
    public boolean isOnline = false;

    @Ignore
    @SerializedName("version")
    public double version = 0;

    @Expose
    @SerializedName("ip")
    public String ip = "";

    @Expose
    @SerializedName("time")
    public String time = "";

    boolean isVisible = false;

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Expose
    @SerializedName("signal")
    public int signal = 0;

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public String d_typeName = "";

    public String getD_typeName() {
        return d_typeName;
    }

    public void setD_typeName(String d_typeName) {
        this.d_typeName = d_typeName;
    }

    boolean isChecked = false;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Expose
    @SerializedName("d1")
    public d1 objd1;

    @Expose
    @SerializedName("d2")
    public d2 objd2;

    @Expose
    @SerializedName("d3")
    public d3 objd3;

    @Expose
    @SerializedName("d4")
    public d4 objd4;


    public int drawable;

    public int signalDrawable;

    public String power;

    public String dNum;


    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public int getSignalDrawable() {
        return signalDrawable;
    }

    public void setSignalDrawable(int signalDrawable) {
        this.signalDrawable = signalDrawable;
    }

    public d3 getObjd3() {
        return objd3;
    }

    public void setObjd3(d3 objd3) {
        this.objd3 = objd3;
    }

    public d4 getObjd4() {
        return objd4;
    }

    public void setObjd4(d4 objd4) {
        this.objd4 = objd4;
    }

    public d2 getObjd2() {
        return objd2;
    }

    public void setObjd2(d2 objd2) {
        this.objd2 = objd2;
    }

    public d1 getObjd1() {
        return objd1;
    }

    public void setObjd1(d1 objd1) {
        this.objd1 = objd1;
    }

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getDtype() {
        return dtype;
    }

    public void setDtype(int dtype) {
        this.dtype = dtype;
    }

    public String getDuser() {
        return duser;
    }

    public void setDuser(String duser) {
        this.duser = duser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

 /*   public ArrayList<String> getIr() {
        return ir;
    }

    public void setIr(ArrayList<String> ir) {
        this.ir = ir;
    }*/

    public String getPowerPuff() {
        return this.power;
    }

    public void setPowerPuff(String power) {
        this.power = power;
    }

    public String getdNum() {
        return dNum;
    }

    public void setdNum(String dNum) {
        this.dNum = dNum;
    }
}
