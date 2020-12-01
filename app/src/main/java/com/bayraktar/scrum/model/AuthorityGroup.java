package com.bayraktar.scrum.model;

import java.util.HashMap;
import java.util.List;

public class AuthorityGroup {
    int authorityID;
    String authorityName;
    HashMap<String, Boolean> authorizationIDList;

    public AuthorityGroup() {
    }

    public int getAuthorityID() {
        return authorityID;
    }

    public void setAuthorityID(int authorityID) {
        this.authorityID = authorityID;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public HashMap<String, Boolean> getAuthorizationIDList() {
        return authorizationIDList;
    }

    public void setAuthorizationIDList(HashMap<String, Boolean> authorizationIDList) {
        this.authorizationIDList = authorizationIDList;
    }
}