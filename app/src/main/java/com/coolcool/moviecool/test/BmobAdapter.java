package com.coolcool.moviecool.test;

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

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.core.AsyncNetUtils;
import com.coolcool.moviecool.holder.ViewHolder;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yanshili on 2016/3/5.
 */
public class BmobAdapter extends BaseAdapter {
    public static final String TAG="BmobAdapter";

    //与查询条件集合
    List<BmobQuery<MovieInfo>> andQueries=new ArrayList<BmobQuery<MovieInfo>>();
    List<ArrayList<BmobQuery<MovieInfo>>> listArray=new ArrayList<>();
    //每页加载的个数
    public static final int A_PAGE_COUNT=10;

    private static BmobAdapter instance;

    //是否为初始化状态
    private boolean initState=true;
    //确认是否加载图片
    private volatile boolean loadingFlag =true;
    //确认总页数
    public volatile int itemCount =10;
    private ListView mListView=null;
    private ArrayList<MovieInfo> mMovieInfoList=new ArrayList<MovieInfo>();
    private Context mContext;
    private LayoutInflater mInflater;
    private DataCacheBase mDataCacheBase;

    private static synchronized void init(ListView listView){
        if (instance==null){
            instance=new BmobAdapter(listView);
        }
    }

    public static BmobAdapter getInstance(ListView listView){
        if (instance==null){
            init(listView);
        }
        return instance;
    }

    private BmobAdapter(ListView listView) {
        mContext = listView.getContext();
        mListView=listView;
        mInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //初始化查询条件
        initCriteria();
        //初始化加载筛选后的第一页内容
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
        return getItem(position).getPostId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.list_item_movie,null,false);
        }

        ViewHolder holder=ViewHolder.getHolder(convertView);
        TextView tvTitle=holder.getView(R.id.tvMovieTitle);
        TextView tvDescription=holder.getView(R.id.tvMovieDescription);
        final ImageView ivImg=holder.getView(R.id.ivPoster);
        //为imageView设置默认图片
        ivImg.setImageResource(R.mipmap.ic_launcher);
        //根据position获取电影url
        final String htmlUrl= convertIdToUrl(getItemId(position));
        ivImg.setTag(htmlUrl);
        //根据电影页面url，从内存缓存中取得电影信息对象
        MovieInfo movieInfo= mDataCacheBase.getDataFromMemoryCache(htmlUrl);
        if (movieInfo==null){
            movieInfo=getItem(position);
        }
        if (movieInfo!=null){

            tvTitle.setText(movieInfo.getTitle());
            tvDescription.setText(movieInfo.getSummary());

            Bitmap bitmap=mDataCacheBase.getBitmapFromMemoryCache(movieInfo.getPosterUrl());
            if (bitmap!=null){
                ivImg.setImageBitmap(bitmap);
            }else if (loadingFlag){
                loadBitmapFromNet(movieInfo);
            }
        }
        return convertView;
    }

    public void startLoadBitmap(){
        int first=mListView.getFirstVisiblePosition();
        int last=mListView.getLastVisiblePosition();

        int count=last-first;
        if (count<=0) return;
        for (int i=first;i<last+1;i++){
            //因为添加了headerView所以尽量不要用adapter.getItem()方法
            MovieInfo movieInfo= (MovieInfo) mListView.getItemAtPosition(i);
            loadBitmapFromMemory(movieInfo);
        }
    }

    public void loadBitmapFromMemory(MovieInfo movieInfo){
        if (movieInfo==null) return;
        String htmlUrl=movieInfo.getHtmlUrl();
        String imageUrl=movieInfo.getPosterUrl();
        ImageView imageView= (ImageView) mListView.findViewWithTag(htmlUrl);
        if (imageView!=null){
            Bitmap bitmap=mDataCacheBase.getBitmapFromMemoryCache(imageUrl);
            if (bitmap!=null){
                imageView.setImageBitmap(bitmap);
            }else if (loadingFlag){
                loadBitmapFromNet(movieInfo);
            }
        }
    }

    /**
     * 从网络下载图片，并根据条件决定是否填充到视图view内
     *
     */
    public void loadBitmapFromNet(MovieInfo movieInfo){
        final String htmlUrl=movieInfo.getHtmlUrl();
        final String imgUrl=movieInfo.getPosterUrl();
        ImageView view = (ImageView) mListView.findViewWithTag(htmlUrl);

        AsyncNetUtils.loadBitmap(imgUrl, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {

                ImageView imageView = (ImageView) mListView.findViewWithTag(htmlUrl);

                if (response != null) {
                    Bitmap bitmap = (Bitmap) response;
                    if (imageView != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                    //将下载好的bitmap缩略图缓存到内存
                    mDataCacheBase.addBitmapToMemoryCache(imgUrl, bitmap);
                }
            }
        }, mContext,view,TAG);
    }

    /**
     * 下载第page页的内容
     * @param page
     */
    public void loadPage(int page){
        //主查询条件
        BmobQuery<MovieInfo> mainQuery=new BmobQuery<MovieInfo>();
        //主查询条件同“与”查询条件集合相与即：and
        //同“或”查询条件集合相或即：or
        mainQuery.and(andQueries);
        //限制每次加载的个数最多为A_PAGE_COUNT个
        mainQuery.setLimit(A_PAGE_COUNT);
        //page页数，每页个数A_PAGE_COUNT
        mainQuery.setSkip((page - 1) * A_PAGE_COUNT);
        //获取总页数，存放在volatile修饰的pageCount里
        getPageSum(mainQuery);
        mainQuery.findObjects(mContext, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                mMovieInfoList.addAll(list);
                notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "下载失败" + s);
            }
        });
    }

    public void selectContent(int page){
        //主查询条件
        BmobQuery<MovieInfo> mainQuery=new BmobQuery<MovieInfo>();
        //主查询条件同“与”查询条件集合相与即：and
        //同“或”查询条件集合相或即：or
        mainQuery.and(andQueries);
        //限制每次加载的个数最多为A_PAGE_COUNT个
        mainQuery.setLimit(A_PAGE_COUNT);
        //page页数，每页个数A_PAGE_COUNT
        mainQuery.setSkip((page - 1) * A_PAGE_COUNT);
        //获取总页数，存放在volatile修饰的pageCount里
        getPageSum(mainQuery);

        mainQuery.findObjects(mContext, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                Log.i(TAG, "下载成功个数-->" + list.size());
                mMovieInfoList.clear();
//                notifyDataSetChanged();
                mMovieInfoList.addAll(list);
                notifyDataSetChanged();
            }
            @Override
            public void onError(int i, String s) {
                Log.i(TAG, "下载失败" + s);
            }
        });
    }

    public void initCriteria(){
        for (int i=0;i< Constant.DBColumnName.length;i++){
            ArrayList<BmobQuery<MovieInfo>> query=new ArrayList<BmobQuery<MovieInfo>>();
            //查询条件
            BmobQuery<MovieInfo> columnQuery=new BmobQuery<MovieInfo>();
            //对应数据库columnNumber列的列名
            String columnName= Constant.DBColumnName[i];
            //对应数据库，我们要查找的rowNumber行的内容
            String rowContent= Constant.criteria[i][0];

            //查询不包含rowContent的行，即所有行
            columnQuery.addWhereNotEqualTo(columnName, rowContent);
            //用来存放一列的所有筛选条件
            query.add(columnQuery);
            //用来将所有的筛选条件，按列存储
            listArray.add(query);
            //将条件添加到集合列表
            andQueries.add(columnQuery);
        }
    }

    public List<BmobQuery<MovieInfo>> addCriteria(int columnNumber,int rowNumber){
        ArrayList<BmobQuery<MovieInfo>> query=new ArrayList<BmobQuery<MovieInfo>>();
        //查询条件
        BmobQuery<MovieInfo> columnQuery=new BmobQuery<MovieInfo>();
        //对应数据库columnNumber列的列名
        String columnName= Constant.DBColumnName[columnNumber];
        //对应数据库，我们要查找的rowNumber行的内容
        String rowContent= Constant.criteria[columnNumber][rowNumber];

        //判断如果是第三列，即数字类型的内容
        if (columnNumber==2){
            int year=2016;
            try{
                year=Integer.parseInt(rowContent);
                columnQuery.addWhereEqualTo(columnName, year);
            }catch (NumberFormatException nfe){
                BmobQuery<MovieInfo> columnQuery1=new BmobQuery<MovieInfo>();
                switch (rowContent){
                    case "00年代":
                        columnQuery.addWhereGreaterThanOrEqualTo(columnName, 2000);
                        columnQuery1.addWhereLessThan(columnName, 2010);
                        query.add(columnQuery1);
                        break;
                    case "90年代":
                        columnQuery.addWhereGreaterThanOrEqualTo(columnName, 1990);
                        columnQuery1.addWhereLessThan(columnName, 2000);
                        query.add(columnQuery1);
                        break;
                    case "80年代":
                        columnQuery.addWhereGreaterThanOrEqualTo(columnName, 1980);
                        columnQuery1.addWhereLessThan(columnName, 1990);
                        query.add(columnQuery1);
                        break;
                    case "更早":
                        columnQuery.addWhereLessThan(columnName, 1980);
                        break;
                }
            }
        }else {
            columnQuery.addWhereContains(columnName, rowContent);
        }

        if (rowNumber == 0) {
            columnQuery=new BmobQuery<MovieInfo>();
            columnQuery.addWhereNotEqualTo(columnName, Constant.criteria[columnNumber][0]);
        }
        query.add(columnQuery);
        listArray.set(columnNumber,query);
        //将条件替换到集合列表
        andQueries=new ArrayList<>();
        for (int i=0;i<listArray.size();i++){
            andQueries.addAll(listArray.get(i));
        }
        return andQueries;
    }

    //获取总页数
    private void getPageSum(BmobQuery<MovieInfo> query){
        query.count(mContext, MovieInfo.class, new CountListener() {
            @Override
            public void onSuccess(int i) {
                itemCount =i;
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    public void deleteData(int position){
        mMovieInfoList.remove(position);
        notifyDataSetChanged();
    }

    public void setLoadingFlag(boolean loadingFlag) {
        this.loadingFlag = loadingFlag;
    }

    public String convertIdToUrl(Long postId){
        return "http://www.lbldy.com/movie/"+postId+".html";
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
