package com.wekex.apps.homeautomation.Sqlite;

import java.io.Serializable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Scheduler implements Serializable {


    @PrimaryKey(autoGenerate = true)
    public int Id = 0;

    @ColumnInfo(name = "dno")
    public String dno = "";

    @ColumnInfo(name = "type")
    public String type = "";

    @ColumnInfo(name = "nextschedule")
    public String next_schedule_time = "";


    @ColumnInfo(name = "scheduler")
    public String scheduler = "";

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }

    public String getScheduler() {
        return scheduler;
    }

    public void setScheduler(String scheduler) {
        this.scheduler = scheduler;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNext_schedule_time() {
        return next_schedule_time;
    }

    public void setNext_schedule_time(String next_schedule_time) {
        this.next_schedule_time = next_schedule_time;
    }
}
