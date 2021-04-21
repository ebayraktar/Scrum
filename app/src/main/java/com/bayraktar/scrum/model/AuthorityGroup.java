package com.bayraktar.scrum.model;

import java.util.Map;

public class AuthorityGroup {
    private int authorityID;
    private String authorityName;
    private Map<String, Boolean> authorizationIDList;

    public AuthorityGroup() {
        //
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

    public Map<String, Boolean> getAuthorizationIDList() {
        return authorizationIDList;
    }

    public void setAuthorizationIDList(Map<String, Boolean> authorizationIDList) {
        this.authorizationIDList = authorizationIDList;
    }
}