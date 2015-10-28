package com.longyuan.qm.bean;

import java.io.Serializable;

public class ChildListData implements Serializable {
    private String TitleID;
    private String Introduction;
    private String Title;
    private String Column;
    private String Author;
    private String MagazineName;
    private String Year;
    private String Issue;
    private String CategoryCode;
    private String ArticleImgList;
    private String PubStartDate;
    private String ArticleImgWidth;
    private String ArticleImgHeight;
    private String MagazineLogo;

    public String getIntroduction() {
        return Introduction;
    }

    public void setIntroduction(String introduction) {
        Introduction = introduction;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getTitleID() {
        return TitleID;
    }

    public void setTitleID(String titleID) {
        TitleID = titleID;
    }

    public String getColumn() {
        return Column;
    }

    public void setColumn(String column) {
        Column = column;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

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

    public String getCategoryCode() {
        return CategoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
    }

    public String getArticleImgList() {
        return ArticleImgList;
    }

    public void setArticleImgList(String articleImgList) {
        ArticleImgList = articleImgList;
    }

    public String getPubStartDate() {
        return PubStartDate;
    }

    public void setPubStartDate(String pubStartDate) {
        PubStartDate = pubStartDate;
    }

    public String getArticleImgWidth() {
        return ArticleImgWidth;
    }

    public void setArticleImgWidth(String articleImgWidth) {
        ArticleImgWidth = articleImgWidth;
    }

    public String getArticleImgHeight() {
        return ArticleImgHeight;
    }

    public void setArticleImgHeight(String articleImgHeight) {
        ArticleImgHeight = articleImgHeight;
    }

    public String getMagazineLogo() {
        return MagazineLogo;
    }

    public void setMagazineLogo(String magazineLogo) {
        MagazineLogo = magazineLogo;
    }
}
