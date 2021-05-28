package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class remote_model_codes_add_request {

    @SerializedName("remotedata")
    public remotedata _object_remote_data;

    public remotedata get_object_remote_data() {
        return _object_remote_data;
    }

    public void set_object_remote_data(remotedata _object_remote_data) {
        this._object_remote_data = _object_remote_data;
    }

    public static class remotedata {

        @SerializedName("Name")
        public String Name = "";

        @SerializedName("R_Type")
        public String R_Type = "";

        @SerializedName("R_Brand")
        public String R_Brand = "";

        @SerializedName("Format")
        public int Format = 0;

        @SerializedName("keys")
        public ArrayList<keys> _objKey = new ArrayList<>();

        public static class keys {

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

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            Name = name;
        }

        public String getR_Type() {
            return R_Type;
        }

        public void setR_Type(String r_Type) {
            R_Type = r_Type;
        }

        public String getR_Brand() {
            return R_Brand;
        }

        public void setR_Brand(String r_Brand) {
            R_Brand = r_Brand;
        }

        public int getFormat() {
            return Format;
        }

        public void setFormat(int format) {
            Format = format;
        }

        public ArrayList<keys> get_objKey() {
            return _objKey;
        }

        public void set_objKey(ArrayList<keys> _objKey) {
            this._objKey = _objKey;
        }
    }
}
