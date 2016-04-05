package com.coolcool.moviecool.model;

import java.io.Serializable;

/**
 * 视频的标签url
 * Created by yanshili on 2016/2/20.
 */
public class TagUrl implements Serializable {
    public static final long serialVersionUID=1L;

    private int id;
    //标签的url
    private String tagUrl;
    //所属视频分类
    private VideoCategory mVideoCategory;
    //所属标签
    private VideoTag mVideoTag;

    public VideoTag getVideoTag() {
        return mVideoTag;
    }

    public void setVideoTag(VideoTag videoTag) {
        mVideoTag = videoTag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public VideoCategory getVideoCategory() {
        return mVideoCategory;
    }

    public void setVideoCategory(VideoCategory videoCategory) {
        mVideoCategory = videoCategory;
    }

    public String getTagUrl() {
        return tagUrl;
    }

    public void setTagUrl(String tagUrl) {
        this.tagUrl = tagUrl;
    }
}
