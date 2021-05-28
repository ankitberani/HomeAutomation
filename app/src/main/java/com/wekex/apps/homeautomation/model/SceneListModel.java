package com.wekex.apps.homeautomation.model;

import java.io.Serializable;

public class SceneListModel implements Serializable {

    private String name;
    private String id;
    private String roomId;
    private int dType;
    private boolean isChecked = false;
    private boolean status = false;

    private int r = 0 , g = 0 , b = 0 , w = 0 , ww = 0 , br = 0;

    public boolean getStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    ///////////////////////////

    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }

    ///////////////////////////

    public String getName() { return name; }
    public void setName(String name) {
        this.name = name;
    }

    ///////////////////////////

    public String getId() { return id; }
    public void setId(String id) {
        this.id = id;
    }

    ///////////////////////////

    public int getdType() { return dType; }
    public void setdType(int dType) {
        this.dType = dType;
    }

    ///////////////////////////

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    ///////////////////////////

    public int getR() { return r; }
    public void setR(int r) { this.r = r; }

    ///////////////////////////

    public int getG() { return g; }
    public void setG(int g) { this.g = g; }

    ///////////////////////////

    public int getB() { return b; }
    public void setB(int b) { this.b = b; }

    ///////////////////////////

    public int getW() { return w; }
    public void setW(int w) { this.w = w; }

    ///////////////////////////

    public int getWw() { return ww; }
    public void setWw(int ww) { this.ww = ww; }

    ///////////////////////////

    public int getBr() { return br; }
    public void setBr(int br) { this.br = br; }

}
