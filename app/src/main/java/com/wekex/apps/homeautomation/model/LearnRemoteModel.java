package com.wekex.apps.homeautomation.model;

public class LearnRemoteModel {

    int key_icon;
    String key_name;
    boolean isAlreadyLearn = false;
    String learn_text = "";
    boolean isCustomKey = false;

    public LearnRemoteModel(int key_icon, String key_name, boolean isAlreadyLearn, String learn_text, boolean isCustomKey) {
        this.key_icon = key_icon;
        this.key_name = key_name;
        this.isAlreadyLearn = isAlreadyLearn;
        this.learn_text = learn_text;
        this.isCustomKey = isCustomKey;
    }

    public int getKey_icon() {
        return key_icon;
    }

    public void setKey_icon(int key_icon) {
        this.key_icon = key_icon;
    }

    public String getKey_name() {
        return key_name;
    }

    public void setKey_name(String key_name) {
        this.key_name = key_name;
    }

    public boolean isAlreadyLearn() {
        return isAlreadyLearn;
    }

    public void setAlreadyLearn(boolean alreadyLearn) {
        isAlreadyLearn = alreadyLearn;
    }

    public String getLearn_text() {
        return learn_text;
    }

    public void setLearn_text(String learn_text) {
        this.learn_text = learn_text;
    }

    public boolean isCustomKey() {
        return isCustomKey;
    }

    public void setCustomKey(boolean customKey) {
        isCustomKey = customKey;
    }
}
