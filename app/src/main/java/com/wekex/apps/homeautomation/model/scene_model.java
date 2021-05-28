package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class scene_model {


    @SerializedName("Scene")
    public ArrayList<Scene> lst_scene;


    public ArrayList<Scene> getLst_scene() {
        return lst_scene;
    }

    public void setLst_scene(ArrayList<Scene> lst_scene) {
        this.lst_scene = lst_scene;
    }

    public static class Scene {

        int drawable, value_r, value_g, value_b;
        String scene_name;
        boolean selected;
        boolean isStaticScene = false;
        int groupType = 0;

        @SerializedName("ID")
        String ID = "";

        @SerializedName("UID")
        String UID = "";

        @SerializedName("Name")
        String Name;

        @SerializedName("Devices")
        ArrayList<String> Devices;


        public int getGroupType() {
            return groupType;
        }

        public void setGroupType(int groupType) {
            this.groupType = groupType;
        }

        public boolean isOnline = false;

        public boolean isOnline() {
            return isOnline;
        }

        public void setOnline(boolean online) {
            isOnline = online;
        }

        public ArrayList<String> getDevices() {
            return Devices;
        }

        public void setDevices(ArrayList<String> devices) {
            Devices = devices;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getUID() {
            return UID;
        }

        public void setUID(String UID) {
            this.UID = UID;
        }

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public int getDrawable() {
            return drawable;
        }

        public void setDrawable(int drawable) {
            this.drawable = drawable;
        }

        public int getValue_r() {
            return value_r;
        }

        public void setValue_r(int value_r) {
            this.value_r = value_r;
        }

        public int getValue_g() {
            return value_g;
        }

        public void setValue_g(int value_g) {
            this.value_g = value_g;
        }

        public int getValue_b() {
            return value_b;
        }

        public void setValue_b(int value_b) {
            this.value_b = value_b;
        }

        public String getScene_name() {
            return scene_name;
        }

        public void setScene_name(String scene_name) {
            this.scene_name = scene_name;
        }


        public boolean isStaticScene() {
            return isStaticScene;
        }

        public void setStaticScene(boolean staticScene) {
            isStaticScene = staticScene;
        }
    }
}
