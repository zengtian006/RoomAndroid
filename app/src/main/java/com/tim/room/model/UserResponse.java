package com.tim.room.model;

/**
 * Created by Zeng on 2017/2/10.
 */

public class UserResponse {
    private String status;
    private User user;
    private boolean success;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
