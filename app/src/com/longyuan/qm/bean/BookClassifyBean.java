package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class BookClassifyBean implements Serializable {
    private String Bookid;
    private String BookName;
    private String BookGuid;
    private String OrderNumber;
    private String PubDate;
    private String Author;
    private String Category;
    private String PublishName;
    private String ISBN;
    private String Note;
    private String BookCover;
    private String BookPath;
    private String DownloadUrl;
    private String BookOpenTime;// 0为默认，1为最近阅读；
    private String BookAddTime;
    private String BookIsHasDumped;// 0为未下载；1为下载中；2为下载完成；
    private String BookBeginPosition;//
    private String UserName;

    public String getBookIsHasDumped() {
        return BookIsHasDumped;
    }

    public void setBookIsHasDumped(String bookIsHasDumped) {
        BookIsHasDumped = bookIsHasDumped;
    }

    public String getBookBeginPosition() {
        return BookBeginPosition;
    }

    public void setBookBeginPosition(String bookBeginPosition) {
        BookBeginPosition = bookBeginPosition;
    }

    public String getBookOpenTime() {
        return BookOpenTime;
    }

    public void setBookOpenTime(String bookOpenTime) {
        BookOpenTime = bookOpenTime;
    }

    public String getBookAddTime() {
        return BookAddTime;
    }

    public void setBookAddTime(String bookAddTime) {
        BookAddTime = bookAddTime;
    }

    public String getBookid() {
        return Bookid;
    }

    public void setBookid(String bookid) {
        Bookid = bookid;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getBookGuid() {
        return BookGuid;
    }

    public void setBookGuid(String bookGuid) {
        BookGuid = bookGuid;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getPubDate() {
        return PubDate;
    }

    public void setPubDate(String pubDate) {
        PubDate = pubDate;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPublishName() {
        return PublishName;
    }

    public void setPublishName(String publishName) {
        PublishName = publishName;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String iSBN) {
        ISBN = iSBN;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }

    public String getBookCover() {
        return BookCover;
    }

    public void setBookCover(String bookCover) {
        BookCover = bookCover;
    }

    public String getDownloadUrl() {
        return DownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        DownloadUrl = downloadUrl;
    }

    public String getBookPath() {
        return BookPath;
    }

    public void setBookPath(String bookPath) {
        BookPath = bookPath;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
