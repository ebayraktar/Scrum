package com.bayraktar.scrum.model;

import java.util.Date;

public class TaskComment {
    String commentID;
    String constituentID;
    Date commentDate;
    String comment;
    String attachmentURL;
    boolean isDeleted;

    public TaskComment() {
    }

    public TaskComment(String commentID, String constituentID, Date commentDate, String comment, String attachmentURL, boolean isDeleted) {
        this.commentID = commentID;
        this.constituentID = constituentID;
        this.commentDate = commentDate;
        this.comment = comment;
        this.attachmentURL = attachmentURL;
        this.isDeleted = isDeleted;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getConstituent() {
        return constituentID;
    }

    public void setConstituent(String constituentID) {
        this.constituentID = constituentID;
    }

    public Date getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(Date commentDate) {
        this.commentDate = commentDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAttachmentURL() {
        return attachmentURL;
    }

    public void setAttachmentURL(String attachmentURL) {
        this.attachmentURL = attachmentURL;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
