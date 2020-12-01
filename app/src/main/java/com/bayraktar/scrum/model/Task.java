package com.bayraktar.scrum.model;


import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Task {
    String taskID;
    String taskName;
    String projectID;
    String taskDescription;
    Date startDate;
    Date deadline;
    String constituentID;
    int priorityID;
    int taskStatusID;
    HashMap<String, Boolean> attendants;
    HashMap<String, Boolean> taskHistoryList;
    HashMap<String, Boolean> taskCommentList;
    HashMap<String, Boolean> followerList;

    public Task() {
    }

    public Task(String taskName, String taskDescription, Date startDate, Date deadline, String constituentID, HashMap<String, Boolean> attendants, int priorityID, int taskStatusID, HashMap<String, Boolean> taskHistoryList, HashMap<String, Boolean> taskCommentList, HashMap<String, Boolean> followerList) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.startDate = startDate;
        this.deadline = deadline;
        this.constituentID = constituentID;
        this.attendants = attendants;
        this.priorityID = priorityID;
        this.taskStatusID = taskStatusID;
        this.taskHistoryList = taskHistoryList;
        this.taskCommentList = taskCommentList;
        this.followerList = followerList;
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

    public HashMap<String, Boolean> getAttendants() {
        return attendants;
    }

    public void setAttendants(HashMap<String, Boolean> attendants) {
        this.attendants = attendants;
    }

    public HashMap<String, Boolean> getTaskHistoryList() {
        return taskHistoryList;
    }

    public void setTaskHistoryList(HashMap<String, Boolean> taskHistoryList) {
        this.taskHistoryList = taskHistoryList;
    }

    public HashMap<String, Boolean> getTaskCommentList() {
        return taskCommentList;
    }

    public void setTaskCommentList(HashMap<String, Boolean> taskCommentList) {
        this.taskCommentList = taskCommentList;
    }

    public HashMap<String, Boolean> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(HashMap<String, Boolean> followerList) {
        this.followerList = followerList;
    }
}
