package com.wekex.apps.homeautomation.helperclass;

public interface rgb_color_interface {

    public void selectedColor(int color);

    public void selectedScene(int position);

    public void triggerScene(String id);

    public void delScene(String id, int pos);

    public void editScene(String id, int pos);

    public void getSchedules(String scheduleData);

    public void deleteSchedule(int pos);

    public void editSchedule(int pos);

    public void updateSchedule(int pos);




}
