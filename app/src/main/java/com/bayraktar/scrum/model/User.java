package com.bayraktar.scrum.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class User {
    String userID;
    String email;
    String name;
    String jobTitle;
    String about;
    boolean isVerified;
    Date membershipDate;
    String photoURL;
    String location;
    Date birthDate;
    String userNotificationID;
    HashMap<String, Integer> invitationList;

    public User() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public void setVerified(boolean verified) {
        isVerified = verified;
    }

    public Date getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(Date membershipDate) {
        this.membershipDate = membershipDate;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getUserNotification() {
        return userNotificationID;
    }

    public void setUserNotification(String userNotificationID) {
        this.userNotificationID = userNotificationID;
    }

    public HashMap<String, Integer> getInvitationList() {
        return invitationList;
    }

    public void setInvitationList(HashMap<String, Integer> invitationList) {
        this.invitationList = invitationList;
    }
}
