package com.mshop.po;

import java.util.Date;

public class Session {
    private Integer sid;

    private String token;

    private Integer userId;

    public String getToken() {
        return token;
    }

    private Date createTime;

    private Date updateTime;

    public Session(Integer sid, String token, Integer userId, Date createTime, Date updateTime) {
        this.sid = sid;
        this.token = token;
        this.userId = userId;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Session() {
        super();
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }


    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }


    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }


    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sid=" + sid +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}