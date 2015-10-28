/**
 * @Title: MySearchListItemBean.java
 * @Package com.longyuan.qm.bean
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Android
 * @date 2014-10-14 下午2:09:02
 * @version V1.0
 */
package com.longyuan.qm.bean;

/**
 * @author Android
 * @ClassName: MySearchListItemBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-10-14 下午2:09:02
 */
public class SearchListItemBean {

    private String PageCount;
    private String MagazineName;
    private String MagazineGUID;
    private String Year;
    private String Issue;
    private String TitleID;
    private String Title;
    private String Author;
    private String Abstract;
    private String KeyWord;

    public String getPageCount() {
        return PageCount;
    }

    public void setPageCount(String pageCount) {
        PageCount = pageCount;
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

    public String getTitleID() {
        return TitleID;
    }

    public void setTitleID(String titleID) {
        TitleID = titleID;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getAbstract() {
        return Abstract;
    }

    public void setAbstract(String abstract1) {
        Abstract = abstract1;
    }

    public String getKeyWord() {
        return KeyWord;
    }

    public void setKeyWord(String keyWord) {
        KeyWord = keyWord;
    }
}
