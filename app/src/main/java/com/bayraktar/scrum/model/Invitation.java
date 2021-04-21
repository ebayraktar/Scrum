package com.bayraktar.scrum.model;

import java.util.Date;

public class Invitation {
    private String projectID;
    private String invitedUserID;
    private String invitingUser;
    private Date invitationDate;
    private String authorityGroupID;
    private int invitationStatus;

    public Invitation() {
        //
    }

    public Invitation(String projectID, String invitedUserID, String invitingUser, Date invitationDate, String authorityGroupID, int invitationStatus) {
        this.projectID = projectID;
        this.invitedUserID = invitedUserID;
        this.invitingUser = invitingUser;
        this.invitationDate = invitationDate;
        this.authorityGroupID = authorityGroupID;
        this.invitationStatus = invitationStatus;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getInvitedUserID() {
        return invitedUserID;
    }

    public void setInvitedUserID(String invitedUserID) {
        this.invitedUserID = invitedUserID;
    }

    public String getInvitingUser() {
        return invitingUser;
    }

    public void setInvitingUser(String invitingUser) {
        this.invitingUser = invitingUser;
    }

    public Date getInvitationDate() {
        return invitationDate;
    }

    public void setInvitationDate(Date invitationDate) {
        this.invitationDate = invitationDate;
    }

    public String getAuthorityGroup() {
        return authorityGroupID;
    }

    public void setAuthorityGroup(String authorityGroupID) {
        this.authorityGroupID = authorityGroupID;
    }

    public int getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(int invitationStatus) {
        this.invitationStatus = invitationStatus;
    }
}
