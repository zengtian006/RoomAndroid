package com.tim.room.model;

import java.io.Serializable;

/**
 * Created by Zeng on 2017/3/6.
 */

public class TagEntry implements Serializable {
    private String key;
    private Long value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}
