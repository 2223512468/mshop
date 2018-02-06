package com.mshop.vo;

import com.mshop.po.Session;
import com.mshop.po.User;

public class UserSession {

    private User user;
    private Session session;

    public UserSession() {
    }

    public UserSession(User user, Session session) {
        this.user = user;
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
