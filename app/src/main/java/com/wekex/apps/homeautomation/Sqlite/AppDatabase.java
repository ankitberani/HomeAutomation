package com.wekex.apps.homeautomation.Sqlite;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Scheduler.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract SchedulerDAO userDao();


}
