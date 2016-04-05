package com.coolcool.moviecool.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.api.ParseHtml;
import com.coolcool.moviecool.api.ParseMovieList;
import com.coolcool.moviecool.core.AsyncNetUtils;
import com.coolcool.moviecool.holder.ViewHolder;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;

/**
 * 全部视频的线性列表的适配器，暂时不用
 * Created by yanshili on 2016/2/27.
 */
public class MovieAdapter extends BaseAdapter {
    public static final String TAG="MovieAdapter";
    public static final String MOVIE_INFO="MOVIE_INFO";
    public static final String MOVIE_BITMAP="MOVIE_BITMAP";

    private static MovieAdapter instance;

    //确认是否加载图片
    private volatile boolean loadingFlag =true;
    private ListView mListView=null;
    private ArrayList<MovieInfo> mMovieInfoList=new ArrayList<MovieInfo>();
    private Context mContext;
    private LayoutInflater mInflater;
    private DataCacheBase mDataCacheBase;

    private static synchronized void init(ListView listView){
        if (instance==null){
            instance=new MovieAdapter(listView);
        }
    }

    public static MovieAdapter getInstance(ListView listView){
        if (instance==null){
            init(listView);
        }
        return instance;
    }

    private MovieAdapter(ListView listView) {
        mContext = listView.getContext();
        mListView=listView;
        mInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        loadPage(1);
        mDataCacheBase = DataCacheBase.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return mMovieInfoList.size();
    }

    @Override
    public MovieInfo getItem(int position) {
        return mMovieInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.list_item_movie,null,false);
        }

        ViewHolder holder= ViewHolder.getHolder(convertView);
        TextView tvTitle=holder.getView(R.id.tvMovieTitle);
        TextView tvDescription=holder.getView(R.id.tvMovieDescription);
        final ImageView ivImg=holder.getView(R.id.ivPoster);
        //为imageView设置默认图片
        ivImg.setImageResource(R.mipmap.ic_launcher);

        //根据position获取电影信息对象
        MovieInfo movieInfo=getItem(position);
        final String htmlUrl=movieInfo.getHtmlUrl();

        //根据电影页面url，从内存缓存中取得电影信息对象
        movieInfo= mDataCacheBase.getDataFromMemoryCache(htmlUrl);
        if (movieInfo==null){
            movieInfo=getItem(position);
        }

        if (movieInfo!=null){
            tvTitle.setText(movieInfo.getTitle());
            tvDescription.setText(movieInfo.getSummary());
            setBitmap(movieInfo, position, ivImg);
        }
        return convertView;
    }

    public void startLoadBitmap(){

    }
    
    /**
     * 加载图片
     * @param movieInfo
     * @param position
     * @param imageView
     */
    public void setBitmap(MovieInfo movieInfo, final int position, final ImageView imageView){

        final String htmlUrl=movieInfo.getHtmlUrl();
        //从电影信息对象中取得电影海报url
        final String posterUrl=movieInfo.getPosterUrl();
        if (posterUrl!=null){
            mDataCacheBase.addDataToMemoryCache(htmlUrl, movieInfo);
            //优先从缓存中取图片
            loadBitmap(movieInfo, position, imageView);
        }else {
            AsyncNetUtils.getHtmlText(htmlUrl, new AsyncNetUtils.Callback() {
                @Override
                public void onResponse(Object response) {
                    //解析下载的页面信息文本string类型response，并得到电影信息对象
                    final MovieInfo movie = ParseHtml.parseMovie((String) response);
                    if (movie != null) {
                        movie.setHtmlUrl(htmlUrl);
                        //将下载好的完整电影信息对象缓存到内存中
                        mDataCacheBase.addDataToMemoryCache(htmlUrl, movie);
                        String imgUrl = movie.getPosterUrl();
                        if (imgUrl != null) {
                            //优先从缓存中取图片
                            loadBitmap(movie, position, imageView);
                        }
                    }
                }
            }, mContext);

        }
    }

    /**
     * 根据海报url，依次从内存缓存、磁盘缓存、及网络中取图片
     * @param movieInfo     完整的电影信息对象，即可以获得电影海报url
     * @param position
     * @param imageView
     */
    private void loadBitmap(MovieInfo movieInfo,int position,ImageView imageView){
        String imgUrl=movieInfo.getPosterUrl();
        //根据电影海报url，从内存缓存中取得海报位图
        Bitmap bitmap= mDataCacheBase.getBitmapFromMemoryCache(imgUrl);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
        }else {
            loadBitmapFromNet(imgUrl, position,imageView);
        }

    }

    /**
     * 从网络下载图片，并根据条件决定是否填充到视图view内
     * @param imgUrl
     * @param position
     * @param imageView
     */
    private void loadBitmapFromNet(final String imgUrl
            , final int position,final ImageView imageView){

        AsyncNetUtils.loadBitmap(imgUrl, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {

                if (response != null) {
                    Bitmap bitmap = (Bitmap) response;
                    if (shouldSetBitmap(position)) {
                        imageView.setImageBitmap(bitmap);
                    }
                }
            }
        }, mContext,imageView,TAG);
    }

    /**
     * 判断当前位置position是否可见，可见就返回true，不可见就返回false
     * @param position
     * @return
     */
    private boolean shouldSetBitmap(int position){
        //获取当前屏幕可见的第一行
        int first = mListView.getFirstVisiblePosition();
        //获取当前屏幕可见的最后一行
        int last = mListView.getLastVisiblePosition();
        //判断本线程所下载的图片所属的位置position是否可见
        if (position >= first && position <= last&&loadingFlag) {
            return true;
        }else {
            return false;
        }
    }

    public void loadPage(int page){
        AsyncNetUtils.getHtmlText("http://www.lbldy.com/movie/page/" + page, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {
                ArrayList<MovieInfo> list = (ArrayList<MovieInfo>) ParseMovieList
                        .parseMovieList((String) response, mContext);

                mMovieInfoList.addAll(list);

                notifyDataSetChanged();
            }
        }, mContext);
    }

    public void deleteData(int position){
        mMovieInfoList.remove(position);
        notifyDataSetChanged();
    }

    public void setLoadingFlag(boolean loadingFlag) {
        this.loadingFlag = loadingFlag;
    }
}
