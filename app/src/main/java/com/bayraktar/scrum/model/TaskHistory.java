package com.bayraktar.scrum.model;

import java.util.Date;

public class TaskHistory {
    String historyID;
    int oldTaskStatus;
    int currentTaskStatus;
    Date changeDate;
    String constituentID;

    public TaskHistory() {
    }

    public String getHistoryID() {
        return historyID;
    }

    public void setHistoryID(String historyID) {
        this.historyID = historyID;
    }

    public int getOldTaskStatus() {
        return oldTaskStatus;
    }

    public void setOldTaskStatus(int oldTaskStatus) {
        this.oldTaskStatus = oldTaskStatus;
    }

    public int getCurrentTaskStatus() {
        return currentTaskStatus;
    }

    public void setCurrentTaskStatus(int currentTaskStatus) {
        this.currentTaskStatus = currentTaskStatus;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getConstituent() {
        return constituentID;
    }

    public void setConstituent(String constituentID) {
        this.constituentID = constituentID;
    }
}
