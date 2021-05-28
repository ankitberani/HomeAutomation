package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

public class whethermodel {

    @SerializedName("_objcord")
    public coord _objcord;

    public class coord {

        @SerializedName("lon")
        public String lon = "";

        @SerializedName("lat")
        public String lat = "";

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }
    }

    public coord get_objcord() {
        return _objcord;
    }

    public void set_objcord(coord _objcord) {
        this._objcord = _objcord;
    }
}
