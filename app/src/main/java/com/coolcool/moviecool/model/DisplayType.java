package com.coolcool.moviecool.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by yanshili on 2016/3/30.
 */
public class DisplayType extends BmobObject {
    //电影页面名称
    private String pageName;
    //电影页面展示区域
    private String pageArea;
    //电影展示区域的布局顺序
    private Integer displayOrder;
    //电影展示页面的排列顺序
    private Integer pageOder;
    //布局类型，与SubRecyclerAdapter内的布局类型一一对应
    private Integer layoutType;
    //true代表数据源从指定的MoviePage表中找，false代表从MovieInfo表中搜索
    private boolean specificMovies;
    //电影展示页面类型
    private PageType pageType;

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Integer getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(Integer layoutType) {
        this.layoutType = layoutType;
    }

    public String getPageArea() {
        return pageArea;
    }

    public void setPageArea(String pageArea) {
        this.pageArea = pageArea;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public Integer getPageOder() {
        return pageOder;
    }

    public void setPageOder(Integer pageOder) {
        this.pageOder = pageOder;
    }

    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public boolean isSpecificMovies() {
        return specificMovies;
    }

    public void setSpecificMovies(boolean specificMovies) {
        this.specificMovies = specificMovies;
    }
}
