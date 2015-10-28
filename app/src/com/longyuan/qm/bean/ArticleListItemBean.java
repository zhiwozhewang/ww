/**
 * @Title: ArticleListItemBean.java
 * @Package com.longyuan.qm.bean
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-10-8 上午11:15:14
 * @version V1.0
 */
package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * @author dragonsource
 * @ClassName: ArticleListItemBean
 * @Description: ArticleList的实体Bean(这里用一句话描述这个类的作用)
 * @date 2014-10-8 上午11:15:14
 */
public class ArticleListItemBean implements Serializable{
    private String magazineName;
    private String magazineGUID;
    private String Year;
    private String issue;
    private String titleID;
    private String title;
    private String author;
    private String introduction;
    private String categoryCode;
    private String pubStartDate;
    private String articleImgList;
    private String articleImgWidth;
    private String articleImgHeight;
    private String magazineLogo;
    private String source;

    public String getMagazineName() {
        return magazineName;
    }

    public void setMagazineName(String magazineName) {
        this.magazineName = magazineName;
    }

    public String getMagazineGUID() {
        return magazineGUID;
    }

    public void setMagazineGUID(String magazineGUID) {
        this.magazineGUID = magazineGUID;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getTitleID() {
        return titleID;
    }

    public void setTitleID(String titleID) {
        this.titleID = titleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getPubStartDate() {
        return pubStartDate;
    }

    public void setPubStartDate(String pubStartDate) {
        this.pubStartDate = pubStartDate;
    }

    public String getArticleImgList() {
        return articleImgList;
    }

    public void setArticleImgList(String articleImgList) {
        this.articleImgList = articleImgList;
    }

    public String getArticleImgWidth() {
        return articleImgWidth;
    }

    public void setArticleImgWidth(String articleImgWidth) {
        this.articleImgWidth = articleImgWidth;
    }

    public String getArticleImgHeight() {
        return articleImgHeight;
    }

    public void setArticleImgHeight(String articleImgHeight) {
        this.articleImgHeight = articleImgHeight;
    }

    public String getMagazineLogo() {
        return magazineLogo;
    }

    public void setMagazineLogo(String magazineLogo) {
        this.magazineLogo = magazineLogo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
