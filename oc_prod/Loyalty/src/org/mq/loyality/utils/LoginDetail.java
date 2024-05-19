package org.mq.loyality.utils;

import java.util.List;

public class LoginDetail {
	 
    private boolean loggedIn;
    private String principal;
    private List roles;
 
    public LoginDetail success() {
        loggedIn = true;
        return this;
    }
 
    public LoginDetail principal(String principal) {
        this.principal = principal;
        return this;
    }
 
    public LoginDetail failed() {
        loggedIn = false;
        return this;
    }
 
    public boolean isLoggedIn() {
        return loggedIn;
    }
 
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
 
    public String getPrincipal() {
        return principal;
    }
 
    public void setPrincipal(String principal) {
        this.principal = principal;
    }
 
    public List getRoles() {
        return roles;
    }
 
    public void setRoles(List roles) {
        this.roles = roles;
    }
}
