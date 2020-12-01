package com.bayraktar.scrum.model;

import java.util.Date;

public class Member {
    String userID;
    Date membershipDate;
    int authorityGroupID;

    public Member() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(Date membershipDate) {
        this.membershipDate = membershipDate;
    }

    public int getAuthorityGroupID() {
        return authorityGroupID;
    }

    public void setAuthorityGroupID(int authorityGroupID) {
        this.authorityGroupID = authorityGroupID;
    }
}
