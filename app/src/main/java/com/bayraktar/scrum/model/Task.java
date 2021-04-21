package com.bayraktar.scrum.model;


import java.util.Date;
import java.util.Map;

public class Task {
    private String taskID;
    private String taskName;
    private String projectID;
    private String taskDescription;
    private Date startDate;
    private Date deadline;
    private String constituentID;
    private int priorityID;
    private int taskStatusID;
    private Map<String, Boolean> attendants;
    private Map<String, Boolean> taskHistoryList;
    private Map<String, Boolean> taskCommentList;
    private Map<String, Boolean> followerList;

    public Task() {
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public String getConstituent() {
        return constituentID;
    }

    public void setConstituent(String constituentID) {
        this.constituentID = constituentID;
    }

    public int getPriorityID() {
        return priorityID;
    }

    public void setPriorityID(int priorityID) {
        this.priorityID = priorityID;
    }

    public int getTaskStatusID() {
        return taskStatusID;
    }

    public void setTaskStatusID(int taskStatusID) {
        this.taskStatusID = taskStatusID;
    }

    public Map<String, Boolean> getAttendants() {
        return attendants;
    }

    public void setAttendants(Map<String, Boolean> attendants) {
        this.attendants = attendants;
    }

    public Map<String, Boolean> getTaskHistoryList() {
        return taskHistoryList;
    }

    public void setTaskHistoryList(Map<String, Boolean> taskHistoryList) {
        this.taskHistoryList = taskHistoryList;
    }

    public Map<String, Boolean> getTaskCommentList() {
        return taskCommentList;
    }

    public void setTaskCommentList(Map<String, Boolean> taskCommentList) {
        this.taskCommentList = taskCommentList;
    }

    public Map<String, Boolean> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(Map<String, Boolean> followerList) {
        this.followerList = followerList;
    }
}
