package com.coolcool.moviecool.model;

import cn.bmob.v3.BmobObject;

/**
 * 电影展示页面类型，一个页面一个类型
 * Created by yanshili on 2016/3/30.
 */
public class PageType extends BmobObject {

    //电影展示页面的名字
    private String pageName;
    //电影展示页面的排列号码
    private Integer pageNumber;

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }
}
