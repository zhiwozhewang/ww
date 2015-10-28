package com.longyuan.qm.bean;

import java.io.Serializable;

public class FavListDataBean implements Serializable{
    private String Title;
    private String TitleId;
    private String Introduction;
    private String MagazineName;
    private String MagazineGuid;
    private String Date;
    private String Author;
    private String ID;
    private String Kind;

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitleId() {
        return TitleId;
    }

    public void setTitleId(String titleId) {
        TitleId = titleId;
    }

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getMagazineName() {
        return MagazineName;
    }

    public void setMagazineName(String magazineName) {
        MagazineName = magazineName;
    }

    public String getMagazineGuid() {
        return MagazineGuid;
    }

    public void setMagazineGuid(String magazineGuid) {
        MagazineGuid = magazineGuid;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getKind() {
        return Kind;
    }

    public void setKind(String kind) {
        Kind = kind;
    }
}
