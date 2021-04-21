package com.bayraktar.scrum.model;

import java.util.Date;
import java.util.Map;

public class Project {
    private String projectID;
    private String projectName;
    private String projectImageURL;
    private String constituentID;
    private Date createDate;
    private Map<String, Boolean> members;
    private Map<String, Integer> invitations;
    private Map<String, Integer> applications;
    private boolean privacyMode;
    private Map<String, Integer> taskList;
    private boolean deleted;

    public Project() {
        //
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

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        //
        this.members = members;
    }

    public Map<String, Integer> getInvitations() {
        return invitations;
    }

    public void setInvitations(Map<String, Integer> invitations) {
        this.invitations = invitations;
    }

    public Map<String, Integer> getApplications() {
        return applications;
    }

    public void setApplications(Map<String, Integer> applications) {
        this.applications = applications;
    }

    public boolean isPrivacyMode() {
        return privacyMode;
    }

    public void setPrivacyMode(boolean privacyMode) {
        this.privacyMode = privacyMode;
    }

    public Map<String, Integer> getTaskList() {
        return taskList;
    }

    public void setTaskList(Map<String, Integer> taskList) {
        this.taskList = taskList;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
