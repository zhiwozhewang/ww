package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MagazineListBean implements Parcelable {
    private String CategoryCode;
    private String CategoryName;
    private String ParentCategoryCode;
    private String Icon;
    private String BackGround;

    public String getCategoryCode() {
        return CategoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        CategoryCode = categoryCode;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setCategoryName(String categoryName) {
        CategoryName = categoryName;
    }

    public String getParentCategoryCode() {
        return ParentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        ParentCategoryCode = parentCategoryCode;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getBackGround() {
        return BackGround;
    }

    public void setBackGround(String backGround) {
        BackGround = backGround;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
