package com.coolcool.moviecool.api;

import android.util.Log;

import com.coolcool.moviecool.model.MovieInfo;
import com.coolcool.moviecool.model.VideoCategory;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.datatype.BmobDate;

/**
 * 功能：提取电影页面的内容
 * Created by yanshili on 2016/2/19.
 */
public class ParseHtml {
    public static final String TAG="ParseHtml";
    public static final String XUNLEI_URL="XUNLEI_URL";
    public static final String XUNLEI_TEXT="XUNLEI_TEXT";
    public static final String DIAN_LV_URL="DIAN_LV_URL";
    public static final String DIAN_LV_TEXT="DIAN_LV__TEXT";
    public static final String BAIDU_URL="BAIDU_URL";
    public static final String BAIDU_TEXT="BAIDU_TEXT";


    //解析的文本内要提取的日期格式
    private static final String dateFormat="yyyy-MM-dd HH:mm:ss";
    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 5 * 1000;


    public static MovieInfo parseMovie(String htmlText) {
        MovieInfo movie = new MovieInfo();
        if (htmlText==null){
            return null;
        }
        //获取指定URL内容并解析成Document

        Document doc = Jsoup.parse(htmlText);
        //获取body div div div div元素下所有h2元素列表的第一个元素
//            Element element=doc.select(body+" h2").first();
//            //设置电影的页面标题
//            movie.setTitle(element != null ? element.ownText() : null);
        //获取class等于post的所有div标签中的第一个
        Element post = doc.select("div.post").first();
        //利用post标签获取其下tag名称为h2的所有标签中的第一个
        Element title = post.getElementsByTag("h2").first();
        //设置电影的页面标题
        movie.setTitle(title != null ? title.ownText() : null);

        //利用post标签获取其下class等于postmeat的所有div标签中的第一个
        Element postmeat = post.select("div.postmeat").first();
        ArrayList<VideoCategory> videoCategories = new ArrayList<>();
        //获取postmeat标签下属性值为rel="category tag"的所有标签
        Elements areas = postmeat.select("[rel=category tag]");
        StringBuilder builderGenre=new StringBuilder();
        if (areas != null) {
            for (Element areaE : areas) {
                String categoryName = areaE.text();
                builderGenre.append(categoryName+"/");
            }
        }
        String areaText=builderGenre.toString();
        //设置电影的区域
        movie=parseAreas(areaText, movie);

        //获取postmeat标签下其子标签以外的所有内容
        String dateText = postmeat.ownText();
        SimpleDateFormat sdf= new SimpleDateFormat(dateFormat);
        dateText = DateTools.pickTimestamp(dateText, sdf);
        Date date = null;
        BmobDate bmobDate=null;
        try {
            date=sdf.parse(dateText);
        } catch (ParseException e) {
            date=null;
        }
        if (date!=null)
        bmobDate=new BmobDate(date);
        //设置电影页面的更新日期
        if (bmobDate!=null)
        movie.setUpdateDate(bmobDate);

        //利用post标签获取其下class等于entry的所有div标签中的第一个
        Element entry = post.select("div.entry").first();

        //利用entry标签，找其下哪些p标签里包含span元素的标签，并得到第一个p标签
//        Element info = entry.select("p:has(span)").first();
//        if (info!=null){
//            //利用info标签，找其下哪些a标签里包含href元素的标签，并得到第一个a标签
//            Element alias = info.select("a[href]").first();
//            movie.setTranslatedName(alias != null ? alias.text() : null);
//        }


        String entryText = entry.text();
//        Log.i(TAG, "entryText:" + entryText);
        //解析出演员
        Pattern p=Pattern.compile("主\\s{0,3}演\\s*(.+)\\s*下载地址");
        Matcher matcher=p.matcher(entryText);
        String starNames=null;
        if (matcher.find()){
            starNames=matcher.group(1);
        }else {
            p=Pattern.compile("主\\s{0,3}演\\s*(.+)");
            matcher=p.matcher(entryText);
            if (matcher.find()){
                starNames=matcher.group(1);
            }else {
                p=Pattern.compile("演\\s{0,3}员\\s*(.+)");
                matcher=p.matcher(entryText);
                if (matcher.find()){
                    starNames=matcher.group(1);
                }
            }
        }
        if (starNames!=null){
            p=Pattern.compile("([：:])");
            matcher=p.matcher(starNames);
            while (matcher.find()){
                starNames=matcher.replaceAll("\n");
            }
        }
        movie.setStars(starNames);


        //解析出电影页面postId属性
        p = Pattern.compile("http://www.lbldy.com/movie/(\\d{2,6})(\\.html)");
        matcher = p.matcher(htmlText);
        if (matcher.find()) {
            Long postId = Long.parseLong(matcher.group(1));
            movie.setPostId(postId);
        }

        //解析出电影名字
        p = Pattern.compile("《(.+)》");
        matcher = p.matcher(movie.getTitle());
        if (matcher.find()) {
            movie.setName(matcher.group(1));
        }

        //解析出电影清晰度
        String t=movie.getTitle();
        if (t!=null){
            if (t.contains("超清")){
                movie.setResolution("超清");
            }else if (t.contains("高清")){
                movie.setResolution("高清");
            }else if (t.contains("标清")||t.contains("普清")){
                movie.setResolution("标清");
            }else if (t.contains("枪版")){
                movie.setResolution("枪版");
            }
        }

        //提取电影剧情
        String sample=null;
        p=Pattern.compile("<div class=\"entry\"><p>(.+?)</p><p><img.*?>(.*?)</p>");
        matcher=p.matcher(htmlText);
        if (matcher.find()){
            sample=matcher.group(1)+matcher.group(2);

            p=Pattern.compile("(<br />)");
            matcher=p.matcher(sample);
            while (matcher.find()){
                sample=matcher.replaceAll("\n");
            }
            p=Pattern.compile("(<p>)");
            matcher=p.matcher(sample);
            while (matcher.find()){
                sample=matcher.replaceAll("\n");
            }
            p=Pattern.compile("(<.*?>)");
            matcher=p.matcher(sample);
            while (matcher.find()){
                sample=matcher.replaceAll("");
            }
        }
        movie.setSummary(sample);

        //提取entry内的原始文本
        p=Pattern.compile("<div class=\"entry\">(.*?)</div>");
        String rawEntry=null;
        matcher=p.matcher(htmlText);
        if(matcher.find()){
            rawEntry=matcher.group(1);
            Log.i(TAG, "htmlText==" + rawEntry);
        }
        movie = ParseHtml.parseRawEntry(rawEntry, movie);

        movie= parseRawText(rawEntry, movie);

        return movie;
    }


    public static MovieInfo parseAreas(String areaText,MovieInfo movie){
        StringBuilder stringBuilder=new StringBuilder();
        boolean others=true;
        if (areaText.contains("大陆")||areaText.contains("内地")){
            stringBuilder.append("内地");
            others=false;
        }
        if (areaText.contains("欧美")
                ||areaText.contains("美国")
                ||areaText.contains("英国")
                ||areaText.contains("澳大利亚")
                ||areaText.contains("北美")
                ||areaText.contains("南美")){
            stringBuilder.append("欧美");
            others=false;
        }
        if (areaText.contains("港台")||areaText.contains("香港")
                ||areaText.contains("台湾")){
            stringBuilder.append("港台");
            others=false;
        }
        if (areaText.contains("日韩")||areaText.contains("日本")
                ||areaText.contains("韩国")){
            stringBuilder.append("日韩");
            others=false;
        }
        if (areaText.contains("泰国")){
            stringBuilder.append("泰国");
            others=false;
        }
        if (areaText.contains("印度")){
            stringBuilder.append("印度");
            others=false;
        }
//        if (others){
//            stringBuilder.append("其他");
//        }
        movie.setArea(stringBuilder.toString());
        return movie;
    }

    public static MovieInfo parseRawEntry(String entry,MovieInfo movie){

        //解析出语言属性
        Pattern p=Pattern.compile("语\\s{0,3}言[：:]?\\s*(.{0,6}?)<br />");
        Matcher matcher=p.matcher(entry);
        if (matcher.find()){
            String languageText=matcher.group(1);
            movie.setLanguage(languageText);
        }

        //解析出评分
        p=Pattern.compile("<br />(.*?评\\s{0,3}分[：:]?\\s*\\d{0,6}?)<br />");
        matcher=p.matcher(entry);
        while (matcher.find()){
            String gradeText=matcher.group(1);
            movie.setGradeDetail(gradeText + "/");
        }

        //解析出原始片名
        p=Pattern.compile("[片又原]\\s{0,3}名\\s*(.+?)\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String originNameText=matcher.group(1);
            p=Pattern.compile("<.*?>");
            matcher=p.matcher(originNameText);
            if (matcher.find()){
                originNameText=matcher.replaceAll("");
            }

            p=Pattern.compile("([：:])");
            matcher=p.matcher(originNameText);
            while (matcher.find()){
                originNameText=matcher.replaceAll("\n");
            }
            movie.setIntrinsicName(originNameText);
        }

        //解析出流派
        p=Pattern.compile("分\\s{0,3}类\\s*(.+?)\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String genre=matcher.group(1);
            p=Pattern.compile("<.*?>");
            matcher=p.matcher(genre);
            if (matcher.find()){
                genre=matcher.replaceAll("");
            }

            p=Pattern.compile("([：:])");
            matcher=p.matcher(genre);
            while (matcher.find()){
                genre=matcher.replaceAll("\n");
            }
            movie.setGenre(genre);

        }

        //解析出年代
        p=Pattern.compile("[年|时]\\s{0,3}代[：:]?\\s*(\\d{4})\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            int year=Integer.parseInt(matcher.group(1));
            movie.setYear(year);
        }

        //解析出片长
        p=Pattern.compile("[片|时]\\s{0,2}长[：:]?\\s*(\\d+)?.{1,3}?(?=\\d)(\\d{1,3})\\s*.+?<br />");
        matcher=p.matcher(entry);
        if (matcher.matches()){
            int hour=Integer.parseInt(matcher.group(1));
            int minute=Integer.parseInt(matcher.group(2));
            movie.setMovieLength(hour * 60 + minute);
        }else {
            p=Pattern.compile("[片|时]\\s{0,3}长[：:]?\\s*(\\d{1,3})\\s*.{1,6}\\s*<br />");
            matcher=p.matcher(entry);
            if (matcher.find()){
                int minute=Integer.parseInt(matcher.group(1));
                movie.setMovieLength(minute);
            }
        }

        //解析出国家
        p=Pattern.compile("国\\s{0,3}家[：:]?\\s*(.+?)\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String country=matcher.group(1);
            p=Pattern.compile("<.*?>");
            matcher=p.matcher(country);
            if (matcher.find()){
                country=matcher.replaceAll("");
            }

            p=Pattern.compile("([：:])");
            matcher=p.matcher(country);
            while (matcher.find()){
                country=matcher.replaceAll("\n");
            }
            movie.setCountry(country);
        }

        //解析出字幕类型
        p=Pattern.compile("字\\s{0,3}幕\\s*(.*?)\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String languageText=matcher.group(1);

            p=Pattern.compile("([：:])");
            matcher=p.matcher(languageText);
            while (matcher.find()){
                languageText=matcher.replaceAll("\n");
            }
            movie.setCategoryCaption(languageText);
        }

        //解析出导演
        p=Pattern.compile("导\\s{0,3}演\\s*(.+?)\\s*<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String director=matcher.group(1);
            p=Pattern.compile("<.*?>");
            matcher=p.matcher(director);
            if (matcher.find()){
                director=matcher.replaceAll("");
            }

            p=Pattern.compile("([：:])");
            matcher=p.matcher(director);
            while (matcher.find()){
                director=matcher.replaceAll("\n");
            }
            movie.setDirector(director);

        }

        //解析上映日期
        p=Pattern.compile("上映日期[：:]?\\s*(\\d{4})\\s*[-年]\\s*(\\d{1,2})" +
                "\\s*[-月]\\s*(\\d{1,2})\\s*日?.*<br />");
        matcher=p.matcher(entry);
        DateFormat format=new SimpleDateFormat(dateFormat);
        if (matcher.find()){
            int year=Integer.parseInt(matcher.group(1));
            int month=Integer.parseInt(matcher.group(2));
            int day=Integer.parseInt(matcher.group(3));
            String dateText=year+"-"+month+"-"+day+" 12:00:00";
            Date date=DateTools.encodeDate(dateText,format);
            BmobDate bmobDate=null;
            if (date!=null)
                bmobDate=new BmobDate(date);

            movie.setReleaseDate(bmobDate);
        }


        //解析上映上映国家
        p=Pattern.compile("上映日期[：:]?\\s*(\\d{4})\\s*[-年]\\s*(\\d{1,2})" +
                "\\s*[-月]\\s*(\\d{1,2})\\s*日?(.*?)<br />");
        matcher=p.matcher(entry);
        if (matcher.find()){
            String place=matcher.group(4);
            movie.setCountry(place);
        }

        return movie;
    }

    public static MovieInfo parseRawText(String htmlText, MovieInfo movie){

        //提取海报url地址
        String posterUrl=null;
        Pattern p=Pattern.compile("<img style\\s*.*?src=" +
                "\"(http://www.lbldy.com/image.+\\.[jpg|png]{3})");
        Matcher m=p.matcher(htmlText);
        if (m.find()){
            posterUrl=m.group(1);
        }
        movie.setPosterUrl(posterUrl);

        //提取下载url地址
        ArrayList<HashMap<String,String>> downloads=new ArrayList<HashMap<String,String>>();
        p=Pattern.compile("<p><a href=\"(thunder://.+?)\".*?>(.+?)</a></p>");
        m=p.matcher(htmlText);
        while (m.find()){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put(XUNLEI_URL,m.group(1));
            map.put(XUNLEI_TEXT,m.group(2));
            downloads.add(map);
        }

        p=Pattern.compile("<p><a href=\"(ed2k://.+?)\".*?>(.+?)</a></p>");
        m=p.matcher(htmlText);
        while (m.find()){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put(DIAN_LV_URL,m.group(1));
            map.put(DIAN_LV_TEXT,m.group(2));
            downloads.add(map);
        }

        p=Pattern.compile("<p>百度网盘：<a href=\"(.+?)\".*?>.+?</a>\\s*密码：\\s*(.+?)</p>");
        m=p.matcher(htmlText);
        while (m.find()){
            HashMap<String,String> map=new HashMap<String,String>();
            map.put(BAIDU_URL,m.group(1));
            map.put(BAIDU_TEXT,m.group(2));
            downloads.add(map);
        }

        movie.setDownloadUrls(downloads);

        return movie;
    }


}
