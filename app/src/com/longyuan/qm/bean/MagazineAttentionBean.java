package com.longyuan.qm.bean;

import java.io.Serializable;

public class MagazineAttentionBean implements Serializable{
    private String mag_id;
    private String mag_name;
    private String mag_guid;
    private String mag_categoryName;
    private String mag_cover;
    private String mag_detail;
    private int mag_isHasDumped;
    private String mag_begin_position;
    private String cycle;
    private String username;
    private String id;

    public String getMag_id() {
        return mag_id;
    }

    public void setMag_id(String mag_id) {
        this.mag_id = mag_id;
    }

    public String getMag_name() {
        return mag_name;
    }

    public void setMag_name(String mag_name) {
        this.mag_name = mag_name;
    }

    public String getMag_guid() {
        return mag_guid;
    }

    public void setMag_guid(String mag_guid) {
        this.mag_guid = mag_guid;
    }

    public String getMag_categoryName() {
        return mag_categoryName;
    }

    public void setMag_categoryName(String mag_categoryName) {
        this.mag_categoryName = mag_categoryName;
    }

    public String getMag_cover() {
        return mag_cover;
    }

    public void setMag_cover(String mag_cover) {
        this.mag_cover = mag_cover;
    }

    public String getMag_detail() {
        return mag_detail;
    }

    public void setMag_detail(String mag_detail) {
        this.mag_detail = mag_detail;
    }

    public int getMag_isHasDumped() {
        return mag_isHasDumped;
    }

    public void setMag_isHasDumped(int mag_isHasDumped) {
        this.mag_isHasDumped = mag_isHasDumped;
    }

    public String getMag_begin_position() {
        return mag_begin_position;
    }

    public void setMag_begin_position(String mag_begin_position) {
        this.mag_begin_position = mag_begin_position;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
