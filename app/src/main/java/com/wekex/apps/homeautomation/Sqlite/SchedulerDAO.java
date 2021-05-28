package com.wekex.apps.homeautomation.Sqlite;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;


@Dao
public interface SchedulerDAO {

    @Insert(onConflict = REPLACE)
    public void InsertScheduler(Scheduler obj_user);

    @Query("select * from Scheduler where dno =:dno")
    public int getScheduler(String dno);

    @Query("update Scheduler set scheduler =:schedulerData where dno=:Dno")
    public int updateSchedulerData(String Dno, String schedulerData);

    /*@Query("select profilePicPath from User where userID =:id")
    public String getProfilePic(String id);

    @Query("select * from User")
    public List<User> getAllUserList();

    @Query("update User set profilePicPath =:imagePath where userID =:userId")
    public void addPic(String imagePath, String userId);

    @Query("select * from User where userID =:userId")
    public User getmyInfo(String userId);

    @Query("update User set profilePicPath =:profilPic,userDescription =:description,userAge=:age,gender=:gender,artFav =:artFav,artAbility=:artAbility,artMedium=:artMedium where userID=:userID")
    public int updateUserInfo(String userID, String profilPic, String description, int age, int gender, String artFav, String artAbility, String artMedium);
*/
}
