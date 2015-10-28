package com.longyuan.qm.bean;

import java.io.Serializable;

public class MagazineDetailListBean implements Serializable{
    private String MagazineName;
    private String Year;
    private String Issue;
    private String Cover;
    private String magazineId;
    private String offlineDataPath;
    private String MagzineType;

    public String getMagazineName() {
        return MagazineName;
    }

    public void setMagazineName(String magazineName) {
        MagazineName = magazineName;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getIssue() {
        return Issue;
    }

    public void setIssue(String issue) {
        Issue = issue;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public String getMagazineId() {
        return magazineId;
    }

    public void setMagazineId(String magazineId) {
        this.magazineId = magazineId;
    }

    public String getOfflineDataPath() {
        return offlineDataPath;
    }

    public void setOfflineDataPath(String offlineDataPath) {
        this.offlineDataPath = offlineDataPath;
    }

    public String getMagzineType() {
        return MagzineType;
    }

    public void setMagzineType(String magzineType) {
        MagzineType = magzineType;
    }
}
