package com.wekex.apps.homeautomation.Interfaces;

public interface SchedulerOperation {

    public void getSchedules(String scheduleData);

    public void deleteSchedule(int pos);

    public void editSchedule(int pos);

    public void updateSchedule(int pos);
}
