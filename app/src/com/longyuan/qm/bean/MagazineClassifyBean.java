package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MagazineClassifyBean implements Serializable {
    private String ItemCount;
    private String MagazineName;
    private String MagazineGUID;
    private String Year;
    private String Issue;
    private String IconList;
    private String CoverPicList;
    private String CodeName;
    private String Introduction;
    private String MagzineType;

    public String getItemCount() {
        return ItemCount;
    }

    public void setItemCount(String itemCount) {
        ItemCount = itemCount;
    }

    public String getMagazineName() {
        return MagazineName;
    }

    public void setMagazineName(String magazineName) {
        MagazineName = magazineName;
    }

    public String getMagazineGUID() {
        return MagazineGUID;
    }

    public void setMagazineGUID(String magazineGUID) {
        MagazineGUID = magazineGUID;
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

    public String getIconList() {
        return IconList;
    }

    public void setIconList(String iconList) {
        IconList = iconList;
    }

    public String getCoverPicList() {
        return CoverPicList;
    }

    public void setCoverPicList(String coverPicList) {
        CoverPicList = coverPicList;
    }

    public String getCodeName() {
        return CodeName;
    }

    public void setCodeName(String codeName) {
        CodeName = codeName;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getMagzineType() {
        return MagzineType;
    }

    public void setMagzineType(String magzineType) {
        MagzineType = magzineType;
    }
}
