package com.tim.room.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Zeng on 2017/2/13.
 */

public class ItemSeries implements Serializable {
    private String title;
    private List<Items> items;
    private Integer cate_id;
    private TagMap allTagsMap;

    public TagMap getAllTagsMap() {
        return allTagsMap;
    }

    public void setAllTagsMap(TagMap allTagsMap) {
        this.allTagsMap = allTagsMap;
    }

    public Integer getCate_id() {
        return cate_id;
    }

    public void setCate_id(Integer cate_id) {
        this.cate_id = cate_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }
}
