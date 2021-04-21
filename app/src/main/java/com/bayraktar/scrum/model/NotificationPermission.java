package com.bayraktar.scrum.model;

public class NotificationPermission {
    private int notificationPermissionID;
    private String notificationPermissionName;
    private boolean notificationPermissionValue;

    public NotificationPermission() {
        //
    }

    public NotificationPermission(int notificationPermissionID, String notificationPermissionName, boolean notificationPermissionValue) {
        this.notificationPermissionID = notificationPermissionID;
        this.notificationPermissionName = notificationPermissionName;
        this.notificationPermissionValue = notificationPermissionValue;
    }

    public int getNotificationPermissionID() {
        return notificationPermissionID;
    }

    public void setNotificationPermissionID(int notificationPermissionID) {
        this.notificationPermissionID = notificationPermissionID;
    }

    public String getNotificationPermissionName() {
        return notificationPermissionName;
    }

    public void setNotificationPermissionName(String notificationPermissionName) {
        this.notificationPermissionName = notificationPermissionName;
    }

    public boolean isNotificationPermissionValue() {
        return notificationPermissionValue;
    }

    public void setNotificationPermissionValue(boolean notificationPermissionValue) {
        this.notificationPermissionValue = notificationPermissionValue;
    }
}
