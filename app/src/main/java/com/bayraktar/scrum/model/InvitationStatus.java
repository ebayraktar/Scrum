package com.bayraktar.scrum.model;

public class InvitationStatus {
    private int invitationID;
    private String invitationName;

    public InvitationStatus() {
        //
    }

    public InvitationStatus(int invitationID, String invitationName) {
        this.invitationID = invitationID;
        this.invitationName = invitationName;
    }

    public int getInvitationID() {
        return invitationID;
    }

    public void setInvitationID(int invitationID) {
        this.invitationID = invitationID;
    }

    public String getInvitationName() {
        return invitationName;
    }

    public void setInvitationName(String invitationName) {
        this.invitationName = invitationName;
    }
}
