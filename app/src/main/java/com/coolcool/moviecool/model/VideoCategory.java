package com.coolcool.moviecool.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 视频的分类
 * Created by yanshili on 2016/2/20.
 */
public class VideoCategory implements Serializable {
    public static final long serialVersionUID=1L;

    private int id;
    //类的名称
    private String categoryName;
    //类的url
    private TagUrl mTagUrl;
    //该类下的电影信息类
    private ArrayList<MovieInfo> mMovieInfos;
    //该类下的标签信息
    private ArrayList<VideoTag> mVideoTags;

    public ArrayList<VideoTag> getVideoTags() {
        return mVideoTags;
    }

    public void setVideoTags(ArrayList<VideoTag> videoTags) {
        mVideoTags = videoTags;
    }

    public ArrayList<MovieInfo> getMovieInfos() {
        return mMovieInfos;
    }

    public void setMovieInfos(ArrayList<MovieInfo> movieInfos) {
        mMovieInfos = movieInfos;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TagUrl getTagUrl() {
        return mTagUrl;
    }

    public void setTagUrl(TagUrl tagUrl) {
        mTagUrl = tagUrl;
    }

    @Override
    public String toString() {
        return "categoryName='" + categoryName+"'\n";
    }
}
