package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class schedule_model_type_other {


    @SerializedName("schedule")
    ArrayList<schedule> _lst_schedule;


    public ArrayList<schedule> get_lst_schedule() {
        return _lst_schedule;
    }

    public void set_lst_schedule(ArrayList<schedule> _lst_schedule) {
        this._lst_schedule = _lst_schedule;
    }

    public static class schedule {

        @SerializedName("sn")
        public int serialNumber = 0;

        @SerializedName("Time_H")
        public int Time_H = 0;

        @SerializedName("Time_M")
        public int Time_M = 0;

        @SerializedName("Repeat")
        public int Repeat = 0;

        @SerializedName("Output")
        public int Output = 0;

        @SerializedName("state")
        public boolean state = false;

        @SerializedName("Days")
        public String Days = "";

        @SerializedName("Date")
        public String Date = "";

        @SerializedName("Arm")
        public int Arm = 0;


        public int getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(int serialNumber) {
            this.serialNumber = serialNumber;
        }

        public int getTime_H() {
            return Time_H;
        }

        public void setTime_H(int time_H) {
            Time_H = time_H;
        }

        public int getTime_M() {
            return Time_M;
        }

        public void setTime_M(int time_M) {
            Time_M = time_M;
        }

        public int getRepeat() {
            return Repeat;
        }

        public void setRepeat(int repeat) {
            Repeat = repeat;
        }

        public int getOutput() {
            return Output;
        }

        public void setOutput(int output) {
            Output = output;
        }

        public boolean isState() {
            return state;
        }

        public void setState(boolean state) {
            this.state = state;
        }

        public String getDays() {
            return Days;
        }

        public void setDays(String days) {
            Days = days;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String date) {
            Date = date;
        }

        public int getArm() {
            return Arm;
        }

        public void setArm(int arm) {
            Arm = arm;
        }
    }
}
