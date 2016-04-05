package com.coolcool.moviecool.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by yanshili on 2016/3/25.
 */
public class MoviePage extends BmobObject {

    //电影页面名称
    private String pageName;
    //电影页面展示区域
    private String pageArea;
    //电影展示顺序
    private Integer pageOrder;
    //电影详细信息类
    private MovieInfo movieInfo;

    public MovieInfo getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(MovieInfo movieInfo) {
        this.movieInfo = movieInfo;
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

    public Integer getPageOrder() {
        return pageOrder;
    }

    public void setPageOrder(Integer pageOrder) {
        this.pageOrder = pageOrder;
    }
}
