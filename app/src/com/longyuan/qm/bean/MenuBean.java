/**
 * @Title: MenuBean.java
 * @Package com.longyuan.zgg.bean
 * @author imhzwen@gmail.com   
 * @date 2014-8-14 上午11:24:38 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.longyuan.qm.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * @author imhzwen@gmail.com
 * @ClassName: MenuBean
 * @Description: 菜单bean
 * @date 2014-8-14 上午11:24:38
 */
public class MenuBean implements Parcelable {

    private String name, menuValue;
    private Fragment fragment;
    private int normalResource, checkedResource, currentResource;

    public MenuBean(String name, int resource, Fragment mFragment,
                    int checkedResource, int currentResource) {
        this.name = name;
        this.normalResource = resource;
        this.fragment = mFragment;
        this.checkedResource = checkedResource;
        this.currentResource = currentResource;
    }

    public MenuBean() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public int getNormalResource() {
        return normalResource;
    }

    public void setNormalResource(int normalResource) {
        this.normalResource = normalResource;
    }

    public int getCheckedResource() {
        return checkedResource;
    }

    public void setCheckedResource(int checkedResource) {
        this.checkedResource = checkedResource;
    }

    public int getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(int currentResource) {
        this.currentResource = currentResource;
    }

    public String getMenuValue() {
        return menuValue;
    }

    public void setMenuValue(String menuValue) {
        this.menuValue = menuValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}