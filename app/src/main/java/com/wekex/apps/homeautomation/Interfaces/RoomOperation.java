package com.wekex.apps.homeautomation.Interfaces;

import com.wekex.apps.homeautomation.model.AllDataResponseModel;

public interface RoomOperation {

    void SelectGroup(int pos);

    void DeleteGroup(int pos);

    void RenameGroup(int pos);

    void triggerGroup(int pos, boolean state);


    void MoveTo(String roomId, String dno);

    void Scheduled(int pos);

    void selectType(int type);

    void click_device(int pos, int type);

    void updateStatus(String deviceInfo);

    void RenameDevice(int pos, String type);

    void TurnOnOffDevice(int pos, String type, boolean state, String dno, String key, String dtype);

    AllDataResponseModel getAllData();

    void scheduleDevice(int pos, int type);

    void editGroup(int pos);

    void publishSeekbar(int pos, int br);

    void publishSeekbarType16(int pos, double br);

    void view_logs(int pos, String logs);
}
