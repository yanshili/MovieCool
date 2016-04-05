package com.coolcool.moviecool.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yanshili on 2016/2/20.
 */
public class VideoTag implements Serializable {
    public static final long serialVersionUID=1L;

    private int id;
    //标签名称
    private String tagName;
    //标签的url
    private TagUrl mTagUrl;
    //标签下的分类
    private ArrayList<VideoCategory> mVideoCategories;

    public ArrayList<VideoCategory> getVideoCategories() {
        return mVideoCategories;
    }

    public void setVideoCategories(ArrayList<VideoCategory> videoCategories) {
        mVideoCategories = videoCategories;
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

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
