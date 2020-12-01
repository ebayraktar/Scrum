package com.bayraktar.scrum.model;

import java.util.Date;

public class Application {
    String appliedUserID;
    Date applicationDate;
    Integer invitationStatus;

    public Application() {
    }

    public Application(String appliedUserID, Date applicationDate, int invitationStatus) {
        this.appliedUserID = appliedUserID;
        this.applicationDate = applicationDate;
        this.invitationStatus = invitationStatus;
    }

    public String getAppliedUserID() {
        return appliedUserID;
    }

    public void setAppliedUserID(String appliedUserID) {
        this.appliedUserID = appliedUserID;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public Integer getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(Integer invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
