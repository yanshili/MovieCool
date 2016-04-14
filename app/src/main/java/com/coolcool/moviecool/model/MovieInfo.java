package com.coolcool.moviecool.model;

import java.util.ArrayList;
import java.util.HashMap;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 本类是电影信息类
 * Created by yanshili on 2016/2/19.
 */
public class MovieInfo extends BmobObject{
    //序列化ID，利用此ID，可以在其他地方（如：服务器）反序列化得到该类
    //例如：可以在服务器端定义一个同样的类，并保证序列化ID相同，
    //这样服务器端和客户端就可以互相反序列化得到相同的对象
//    public static final long serialVersionUID=1L;

    //电影名称
    private String name;
    //评分
    private Integer grade;
    //评分说明
    private String gradeDetail;
    //电影的年代
    private Integer year;
    //片长，单位：分钟
    private Integer movieLength;
    //导演
    private String director;
    //主演
    private String stars;
    //区域分类
    private String area;
    //流派分类
    private String genre;
    //概要
    private String summary;

    //页面内的post id，可能是服务器内部的数据
    private Long postId;
    //电影页面URL
    private String htmlUrl;
    //完整的页面信息
    private String htmlText;
    //电影页面标题
    private String title;
    //国家
    private String country;
    //清晰度
    private String resolution;
    //电影上传更新日期
    private BmobDate updateDate;
    //海报
    private String posterUrl;
    //海报在云服务器地址
    private String imageUrl;
    //原名
    private String intrinsicName;
    //语言
    private String language;
    //字幕语言
    private String categoryCaption;
    //上映日期
    private BmobDate releaseDate;
    //电影下载地址
    private ArrayList<HashMap<String ,String>> downloadUrls;
    //百度网盘提取密码
    private String keyword;
    //被收藏的次数
    private Long favoriteCount;

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCategoryCaption() {
        return categoryCaption;
    }

    public void setCategoryCaption(String categoryCaption) {
        this.categoryCaption = categoryCaption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public ArrayList<HashMap<String, String>> getDownloadUrls() {
        return downloadUrls;
    }

    public void setDownloadUrls(ArrayList<HashMap<String, String>> downloadUrls) {
        this.downloadUrls = downloadUrls;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getGradeDetail() {
        return gradeDetail;
    }

    public void setGradeDetail(String gradeDetail) {
        this.gradeDetail = gradeDetail;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getIntrinsicName() {
        return intrinsicName;
    }

    public void setIntrinsicName(String intrinsicName) {
        this.intrinsicName = intrinsicName;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(Integer movieLength) {
        this.movieLength = movieLength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public BmobDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(BmobDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getStars() {
        return stars;
    }

    public void setStars(String stars) {
        this.stars = stars;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public BmobDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(BmobDate updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        String date=null;
        if (releaseDate!=null){
            String releaseDateText=releaseDate.getDate();
            if (releaseDateText.contains(" 12:00:00"))
            date=releaseDateText.replace(" 12:00:00","");
            if (releaseDateText.contains(" 00:00:00"))
            date=releaseDateText.replace(" 00:00:00","");
        }

        if (summary!=null){
            summary=summary.replace("\n","\n    ");
        }

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(name!=null?"◎ 片  名："+name+"\n":"");
        stringBuilder.append(intrinsicName!=null?"◎ 原  名："+intrinsicName+"\n":"");
        stringBuilder.append(year!=null?"◎ 年  代："+year+"\n":"");
        stringBuilder.append(country!=null?"◎ 国  家："+country+"\n":"");
        stringBuilder.append(genre!=null?"◎ 类  别："+genre+"\n":"");
        stringBuilder.append(language!=null?"◎ 语  言："+language+"\n":"");
        stringBuilder.append(categoryCaption!=null?"◎ 字  幕："+categoryCaption+"\n":"");
        stringBuilder.append(gradeDetail!=null?"◎ 评  分："+gradeDetail+"\n":"");
        stringBuilder.append(movieLength!=null?"◎ 片  长："+movieLength+" 分钟\n":"");
        stringBuilder.append(resolution!=null?"◎ 清晰度："+resolution+"\n":"");
        stringBuilder.append(releaseDate!=null?"◎ 上映日期："+date+"\n":"");
        stringBuilder.append(director!=null?"\n◎ 导  演："+director+"\n":"");
        stringBuilder.append(stars!=null?"\n◎ 主  演："+stars+"\n":"");
        stringBuilder.append(summary!=null?"\n◎ 剧  情：\n    "+summary+"\n\n":"\n\n");

        return  stringBuilder.toString();
    }
}
