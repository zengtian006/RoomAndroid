package com.tim.room.model;

/**
 * Created by Zeng on 2017/2/14.
 */

public class Categories {
    Integer id;
    String cateName;
    Integer parentId;

    public Categories() {

    }

    public Categories(Integer id, String cateName, Integer parentId) {
        this.id = id;
        this.cateName = cateName;
        this.parentId = parentId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCateName() {
        return cateName;
    }

    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}
