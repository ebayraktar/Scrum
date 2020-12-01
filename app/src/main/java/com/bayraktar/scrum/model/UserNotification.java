package com.bayraktar.scrum.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class UserNotification {
    String deviceID;
    String token;
    Date createDate;
    Date changeDate;
    HashMap<String, Boolean> permissionIDList;

    public UserNotification() {
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public HashMap<String, Boolean> getPermissionIDList() {
        return permissionIDList;
    }

    public void setPermissionIDList(HashMap<String, Boolean> permissionIDList) {
        this.permissionIDList = permissionIDList;
    }
}
