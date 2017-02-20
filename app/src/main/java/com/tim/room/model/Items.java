package com.tim.room.model;

import java.util.UUID;

// Generated 2017-2-10 15:27:36 by Hibernate Tools 4.3.1

/**
 * Items generated by hbm2java
 */
public class Items implements java.io.Serializable {
    private UUID id;
//    private UUID userId;
    private String brand;
    private String title;
    private String imageName;
    private String remark;
    private Character status;
    private Integer cateId;
    private User user;


    public Items() {
    }

    public Items(UUID id, String brand, String title,
                 String imageName, String remark, Character status) {
        this.id = id;
//        this.userId = userId;
        this.brand = brand;
        this.title = title;
        this.imageName = imageName;
        this.remark = remark;
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
//
//    public UUID getUserId() {
//        return this.userId;
//    }
//
//    public void setUserId(UUID userId) {
//        this.userId = userId;
//    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return this.imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Character getStatus() {
        return this.status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

}
