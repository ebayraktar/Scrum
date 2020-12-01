package com.bayraktar.scrum.model;

public class Authorization {
    int authorizationID;
    String authorizationName;

    public Authorization() {
    }

    public int getAuthorizationID() {
        return authorizationID;
    }

    public void setAuthorizationID(int authorizationID) {
        this.authorizationID = authorizationID;
    }

    public String getAuthorizationName() {
        return authorizationName;
    }

    public void setAuthorizationName(String authorizationName) {
        this.authorizationName = authorizationName;
    }
}
