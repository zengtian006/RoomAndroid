package com.tim.room.model;

import java.util.List;

/**
 * Created by zengtim on 17/3/2017.
 */

public class ItemsResponse {
    private String status;
    private List<Items> items;
    private boolean success;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
