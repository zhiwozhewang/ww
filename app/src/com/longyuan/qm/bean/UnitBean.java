package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitBean {

    private String unitName = "";
    private String unitBaseUrl = "";

    public String getUnitBaseUrl() {
        return unitBaseUrl;
    }

    public void setUnitBaseUrl(String unitBaseUrl) {
        this.unitBaseUrl = unitBaseUrl;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
