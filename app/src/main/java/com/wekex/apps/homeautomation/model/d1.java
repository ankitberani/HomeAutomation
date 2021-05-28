package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class d1 {

    @Expose
    @SerializedName("state")
    public boolean state = false;

    @Expose
    @SerializedName("door")
    public boolean door = false;

    @Expose
    @SerializedName("motion")
    public String motion = "false";


    @Expose
    @SerializedName("r")
    public int r = 0;


    @Expose
    @SerializedName("g")
    public int g = 0;


    @Expose
    @SerializedName("b")
    public int b = 0;


    @Expose
    @SerializedName("w")
    public int w = 0;


    @Expose
    @SerializedName("ww")
    public int ww = 0;


    @Expose
    @SerializedName("br")
    public double br = 0;


    @Expose
    @SerializedName("name")
    public String name;

    @Expose
    @SerializedName("t")
    public String value_t;

    @Expose
    @SerializedName("h")
    public String value_h;

    @Expose
    @SerializedName("hi")
    public String value_hi;


    @Expose
    @SerializedName("v")
    public String value_v;


    public String getValue_t() {
        return value_t;
    }

    public void setValue_t(String value_t) {
        this.value_t = value_t;
    }

    public String getValue_h() {
        return value_h;
    }

    public void setValue_h(String value_h) {
        this.value_h = value_h;
    }

    public String getValue_hi() {
        return value_hi;
    }

    public void setValue_hi(String value_hi) {
        this.value_hi = value_hi;
    }

    public String getValue_v() {
        return value_v;
    }

    public void setValue_v(String value_v) {
        this.value_v = value_v;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getWw() {
        return ww;
    }

    public void setWw(int ww) {
        this.ww = ww;
    }

    public double getBr() {
        return br;
    }

    public void setBr(double br) {
        this.br = br;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDoor() {
        return door;
    }

    public void setDoor(boolean door) {
        this.door = door;
    }

    public String getMotion() {
        return motion;
    }

    public void setMotion(String motion) {
        this.motion = motion;
    }
}
