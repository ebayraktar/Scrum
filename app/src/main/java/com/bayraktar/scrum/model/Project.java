package com.bayraktar.scrum.model;

import java.util.Date;
import java.util.HashMap;

public class Project {
    String projectID;
    String projectName;
    String projectImageURL;
    String constituentID;
    Date createDate;
    HashMap<String, Boolean> members;
    HashMap<String, Integer> invitations;
    HashMap<String, Integer> applications;
    boolean privacyMode;
    HashMap<String, Integer> taskList;
    boolean isDeleted;

    public Project() {
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectImageURL() {
        return projectImageURL;
    }

    public void setProjectImageURL(String projectImageURL) {
        this.projectImageURL = projectImageURL;
    }

    public String getConstituentID() {
        return constituentID;
    }

    public void setConstituentID(String constituentID) {
        this.constituentID = constituentID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public HashMap<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(HashMap<String, Boolean> members) {
        this.members = members;
    }

    public HashMap<String, Integer> getInvitations() {
        return invitations;
    }

    public void setInvitations(HashMap<String, Integer> invitations) {
        this.invitations = invitations;
    }

    public HashMap<String, Integer> getApplications() {
        return applications;
    }

    public void setApplications(HashMap<String, Integer> applications) {
        this.applications = applications;
    }

    public boolean isPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(boolean privacyMode) {
        this.privacyMode = privacyMode;
    }

    public HashMap<String, Integer> getTaskList() {
        return taskList;
    }

    public void setTaskList(HashMap<String, Integer> taskList) {
        this.taskList = taskList;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
