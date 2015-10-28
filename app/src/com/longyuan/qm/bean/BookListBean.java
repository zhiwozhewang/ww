package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class BookListBean implements Parcelable {
    private String Code;
    private String Message;
    private String CategoryID;
    private String CategoryName;
    private String CategoryNameEN;
    private String OrderNumber;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(String categoryID) {
        CategoryID = categoryID;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getCategoryNameEN() {
        return CategoryNameEN;
    }

    public void setCategoryNameEN(String categoryNameEN) {
        CategoryNameEN = categoryNameEN;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
