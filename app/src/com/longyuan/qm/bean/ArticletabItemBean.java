/**
 * @Title: ArticletabItemBean.java
 * @Package com.longyuan.qm.bean
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-28 上午11:51:21
 * @version V1.0
 */
package com.longyuan.qm.bean;

import java.io.Serializable;

/**
 * @author dragonsource
 * @ClassName: ArticletabItemBean
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-28 上午11:51:21
 */
public class ArticletabItemBean implements Serializable {
    private String CategoryCode;
    private String CategoryName;
    private String parentCategoryCode;

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
        return parentCategoryCode;
    }

    public void setParentCategoryCode(String parentCategoryCode) {
        this.parentCategoryCode = parentCategoryCode;
    }

}
