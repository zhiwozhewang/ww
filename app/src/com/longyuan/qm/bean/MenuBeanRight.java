/**
 * @Title: MenuBean.java
 * @Package com.longyuan.zgg.bean
 * @author imhzwen@gmail.com   
 * @date 2014-8-14 上午11:24:38 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.longyuan.qm.bean;

import android.app.Activity;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

/**
 * @author imhzwen@gmail.com
 * @ClassName: MenuBean
 * @Description: 菜单bean
 * @date 2014-8-14 上午11:24:38
 */
public class MenuBeanRight implements Parcelable {

    private String name, menuValue;
    private Activity mActivity;
    private int normalResource, checkedResource, currentResource;

    public MenuBeanRight(String name, int resource, Activity activity,
                         int checkedResource, int currentResource) {
        this.name = name;
        this.normalResource = resource;
        this.mActivity = activity;
        this.checkedResource = checkedResource;
        this.currentResource = currentResource;
    }

    public MenuBeanRight() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(Activity activity) {
        this.mActivity = activity;
    }

    /*public Fragment getmFragment() {
        return mFragment;
    }

    public void setmFragment(Fragment mFragment) {
        this.mFragment = mFragment;
    }*/

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