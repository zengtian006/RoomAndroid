package com.tim.room.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zeng on 2017/3/6.
 */

public class TagMap implements Serializable {

    private List<TagEntry> entry;

    public List<TagEntry> getEntry() {
        return entry;
    }

    public void setEntry(List<TagEntry> entry) {
        this.entry = entry;
    }

}
