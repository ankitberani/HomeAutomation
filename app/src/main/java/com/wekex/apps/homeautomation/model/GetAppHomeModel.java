package com.wekex.apps.homeautomation.model;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetAppHomeModel {

    @SerializedName("rooms")
    public ArrayList<rooms> _lst_rooms = new ArrayList<>();

    public static class rooms {

        @SerializedName("ID")
        public String ID = "";

        @SerializedName("name")
        public String name = "";

        public String getID() {
            return ID;
        }
        public void setID(String ID) {
            this.ID = ID;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }

        public Drawable RoomBackground;
        public Drawable getRoomBackground() {
            return RoomBackground;
        }

        public void setRoomBackground(Drawable roomBackground) {
            RoomBackground = roomBackground;
        }
        public String _drawable_name = "";

        public String get_drawable_name() {
            return _drawable_name;
        }
        public void set_drawable_name(String _drawable_name) {
            this._drawable_name = _drawable_name;
        }

    }

    public ArrayList<rooms> get_lst_rooms() {
        return _lst_rooms;
    }
    public void set_lst_rooms(ArrayList<rooms> _lst_rooms) {
        this._lst_rooms = _lst_rooms;
    }
}
