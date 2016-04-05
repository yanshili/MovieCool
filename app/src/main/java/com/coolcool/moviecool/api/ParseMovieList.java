package com.coolcool.moviecool.api;

import android.content.Context;

import com.coolcool.moviecool.model.MovieInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanshili on 2016/2/26.
 */
public class ParseMovieList {
    //解析的文本内要提取的日期格式
    private static final String dateFormat="yyyy年MM月dd日";
    private static final String userAgent = "Mozilla/5.0 (jsoup)";
    private static final int timeout = 5 * 1000;


    public static List<MovieInfo> parseMovieList(String htmlText, final Context context) {
        List<MovieInfo> movies=new ArrayList<MovieInfo>();

        if (htmlText==null){
            return null;
        }
        Document doc = Jsoup.parse(htmlText);
        Elements postList=doc.select("div.postlist");
        for (Element post:postList){
            final MovieInfo movie=new MovieInfo();
            Element a=post.getElementsByTag("a").first();
            movie.setHtmlUrl(a != null ? a.attr("href") : null);
            movie.setTitle(a != null ? a.attr("title") : null);

            if (movie.getTitle()!=null){
                Pattern p=Pattern.compile("《(.+)》");
                Matcher m=p.matcher(movie.getTitle());
                if (m.find()){
                    movie.setName(m.group(1));
                }
            }

//            movie.setSummary(post.select("div.postcontent").first().text());

//            String html=NetUtils.getMethod(movie.getHtmlUrl());
//            movie=ParseHtml.parseMovie(html,context);

//            AsyncNetUtils.getHtmlText(movie.getHtmlUrl(), new AsyncNetUtils.Callback() {
//                @Override
//                public void onResponse(Object response) {
//                    MovieInfo movieInfo = ParseHtml.parseMovie((String) response,context);
//                    movieInfo.setHtmlUrl(movie.getHtmlUrl());
//
//                    movie.setPosterUrl(movieInfo.getPosterUrl());
//                    Log.e(NetUtils.TAG,"poster-->"+movie.getPosterUrl());
//                }
//            },context);

            movies.add(movie);
        }

        return movies;
    }
}
