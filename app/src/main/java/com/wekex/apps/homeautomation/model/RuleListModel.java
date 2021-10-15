package com.wekex.apps.homeautomation.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RuleListModel {

    @SerializedName("Rules")
    public ArrayList<Rules> _lst_rules = new ArrayList<>();

    public static class Rules {

        @SerializedName("dno")
        public String dno = "";

        @SerializedName("ID")
        public String ID = "";

        @SerializedName("F_Time")
        public String F_Time = "";

        @SerializedName("T_time")
        public String T_time = "";

        @SerializedName("conditionKey")
        public String conditionKey = "";

        @SerializedName("conditionOperator")
        public String conditionOperator = "";

        @SerializedName("checkTime")
        public boolean checkTime = true;

        @SerializedName("Active")
        public boolean Active = true;

        @SerializedName("conditionVal")
        public int conditionVal = 0;

        @SerializedName("TriggerScene")
        public String TriggerScene = "";

        public String getDno() {
            return dno;
        }

        public String getID() {
            return ID;
        }

        public String getF_Time() {
            return F_Time;
        }

        public String getT_time() {
            return T_time;
        }

        public String getConditionKey() {
            return conditionKey;
        }

        public String getConditionOperator() {
            return conditionOperator;
        }

        public boolean isCheckTime() {
            return checkTime;
        }

        public boolean isActive() {
            return Active;
        }

        public int getConditionVal() {
            return conditionVal;
        }

        public String getTriggerScene() { return TriggerScene; }
    }

    public ArrayList<Rules> getRuleList() { return _lst_rules; }
}
