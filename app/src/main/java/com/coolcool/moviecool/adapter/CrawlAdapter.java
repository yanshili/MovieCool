package com.coolcool.moviecool.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
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
import com.coolcool.moviecool.api.ThreadUtils;
import com.coolcool.moviecool.core.AsyncNetUtils;
import com.coolcool.moviecool.holder.ViewHolder;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;

import cn.bmob.v3.listener.SaveListener;

/**
 * 全部视频的线性列表适配器，用来抓取数据，暂时不用
 * Created by yanshili on 2016/3/4.
 */
public class CrawlAdapter extends BaseAdapter{
    public static final String TAG="CrawlAdapter";

    private static CrawlAdapter instance;

    //确认是否加载图片
    private volatile boolean loadingFlag =true;
    private ListView mListView=null;
    private ArrayList<MovieInfo> mMovieInfoList=new ArrayList<MovieInfo>();
    private Context mContext;
    private LayoutInflater mInflater;
    private DataCacheBase mDataCacheBase;

    private static synchronized void init(ListView listView){
        if (instance==null){
            instance=new CrawlAdapter(listView);
        }
    }

    public static CrawlAdapter getInstance(ListView listView){
        if (instance==null){
            init(listView);
        }
        return instance;
    }

    private CrawlAdapter(ListView listView) {
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
            ivImg.setTag(htmlUrl);
            tvTitle.setText(movieInfo.getTitle());
            tvDescription.setText(movieInfo.getSummary());

            setBitmap(movieInfo,ivImg);
        }
        return convertView;
    }

    public void startLoadBitmap(){

    }

    /**
     * 加载图片
     * @param movieInfo
     *
     */
    public void setBitmap(MovieInfo movieInfo, final ImageView view){

        final String htmlUrl=movieInfo.getHtmlUrl();
        //从电影信息对象中取得电影海报url
        final String posterUrl=movieInfo.getPosterUrl();
        if (posterUrl!=null){
            long postId=movieInfo.getPostId();
            if (postId != 0) {
                loadBitmapFromNet(movieInfo,view);
            }
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
                        ThreadUtils.getInstance().execute(new Runnable() {
                            @Override
                            public void run() {
                                movie.save(mContext, new SaveListener() {
                                    @Override
                                    public void onSuccess() {
                                        Log.i(TAG, "保存成功" + movie.getObjectId());
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        Log.i(TAG, "保存失败" + movie.getObjectId());
                                    }
                                });
                            }
                        });
                        long postId = movie.getPostId();
                        if (postId != 0) {

                            loadBitmapFromNet(movie, view);
                        }
                    }
                }
            }, mContext);

        }
    }

    public void loadBitmapFromNet(final MovieInfo movieInfo,final ImageView imageView){

        final String imgUrl=movieInfo.getPosterUrl();
        final String htmlUrl=movieInfo.getHtmlUrl();

        //根据电影海报url，从内存缓存中取得海报位图
        Bitmap bitmap= mDataCacheBase.getBitmapFromMemoryCache(imgUrl);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }

        AsyncNetUtils.loadBitmap(imgUrl, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {

                if (response != null) {
                    Bitmap bitmap=(Bitmap) response;
                    ImageView imageView= (ImageView) mListView.findViewWithTag(htmlUrl);
                    if (imageView!=null&&ThreadUtils.getInstance().isExecute()){
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
        if (position >= first && position <= last) {
            return true;
        }else {
            return false;
        }
    }

    public void loadPage(int page){
        AsyncNetUtils.getHtmlText("http://www.lbldy.com/movie/page/" + page, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {
                if (response==null) return;
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
