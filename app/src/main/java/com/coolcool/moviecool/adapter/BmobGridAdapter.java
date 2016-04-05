package com.coolcool.moviecool.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.custom.ItemGridLayout;
import com.coolcool.moviecool.holder.ViewHolder;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * 全部视频的网格列表适配器
 * Created by yanshili on 2016/3/18.
 */
public class BmobGridAdapter extends BaseAdapter implements View.OnClickListener{
    public static final String TAG="BmobGridAdapter";

    //与查询条件集合
    List<BmobQuery<MovieInfo>> andQueries=new ArrayList<BmobQuery<MovieInfo>>();
    List<ArrayList<BmobQuery<MovieInfo>>> listArray=new ArrayList<>();
    //每页加载的个数
    public static final int A_PAGE_COUNT=15;

    private static BmobGridAdapter instance;

    //是否为初始化状态
    private boolean initState=true;
    //确认是否加载图片
    private volatile boolean loadingFlag =true;
    //某条件下可得到的电影个数
    public volatile int itemCount =10;
    private ListView mListView =null;
    private ArrayList<MovieInfo[]> mList=new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private DataCacheBase mDataCacheBase;

    private static synchronized void init(ListView gridView){
        if (instance==null){
            instance=new BmobGridAdapter(gridView);
        }
    }

    public static BmobGridAdapter getInstance(ListView listView){
        if (instance==null){
            init(listView);
        }
        return instance;
    }

    private BmobGridAdapter(ListView listView) {
        mContext = listView.getContext();
        mListView = listView;
        mInflater= (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //初始化查询条件
        initCriteria();
        //初始化加载筛选后的第一页内容
        fetchNextPage(1);
        mDataCacheBase = DataCacheBase.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public MovieInfo[] getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position)[0].getPostId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MovieInfo[] mArray=getItem(position);
        Log.i(TAG, "数组的长度=== " + mArray.length);

        if (mArray.length<ItemGridLayout.columnCount||convertView==null){
            convertView=mInflater.inflate(R.layout.grid_item_movie_container, null, false);
        }

        ViewHolder holder= ViewHolder.getHolder(convertView);

        ItemGridLayout gridLayout=holder.getView(R.id.gridItemMovie);
        for (int i=0;i<mArray.length;i++){
            MovieInfo movie=mArray[i];
            initItemView(gridLayout,movie,i);
        }

        return convertView;
    }

    private void initItemView(ItemGridLayout gridLayout,MovieInfo movie,int position){
        View itemImageView=null;
        if (movie!=null) {
            itemImageView = gridLayout.getChildAt(position);
        }
        if (itemImageView != null) {
            CardView cardView = (CardView) itemImageView.findViewById(R.id.cardViewGridCommon);
            SimpleDraweeView imageView = (SimpleDraweeView) itemImageView.findViewById(R.id.imageViewGridCommon);
            TextView textView = (TextView) itemImageView.findViewById(R.id.textViewGridCommon);
            View bottomShadow = itemImageView.findViewById(R.id.bottomShadowGrid);

            cardView.setCardBackgroundColor(Color.WHITE);
            cardView.setCardElevation(4 * Constant.dp);
            cardView.setRadius(2 * Constant.dp);
            cardView.setTag(movie);
            cardView.setOnClickListener(this);

            textView.setText(movie.getName());
            bottomShadow.setEnabled(true);

            String posterUrl = movie.getPosterUrl();
            if (posterUrl != null) {
                imageView.setTag(movie.getPosterUrl());
                imageView.setImageURI(Uri.parse(posterUrl));
            }else {
                imageView.setImageURI(null);
            }
        }
    }

    int fetchPageTimes =0;
    /**
     * 下载第page页的内容
     * @param page
     */
    public void fetchNextPage(int page){
        final int p=page;
        if (mList.size()==itemCount) return;
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
                if (list.size()>0){
                    addList(list);
                    notifyDataSetChanged();
                    fetchPageTimes =0;
                }else if (fetchPageTimes >2){
                    fetchPageTimes =0;
                    notifyDataSetChanged();
                }else if (fetchPageTimes <3){
                    fetchNextPage(p);
                    fetchPageTimes++;
                }
            }

            @Override
            public void onError(int i, String s) {
                if (fetchPageTimes >2){
                    fetchPageTimes =0;
                    notifyDataSetChanged();
                }else {
                    fetchNextPage(p);
                    fetchPageTimes++;
                }
                Log.i(TAG, "下载失败" + s);
            }
        });
    }

    public void addList(List<MovieInfo> list){
        int columnCount=ItemGridLayout.columnCount;

        if (mList!=null&&mList.size()>0&&list.size()>0){
            MovieInfo[] last=mList.get(mList.size()-1);

            if (last.length<columnCount){
                mList.remove(mList.size()-1);
                MovieInfo[] m=new MovieInfo[columnCount];
                for (int i=0;i<last.length;i++){
                    m[i]=last[i];
                }

                for (int i=last.length;i<columnCount;i++){
                    if (list.size()>=i-1){
                        m[i]=list.get(i-last.length);
                        list.remove(i-last.length);
                    }
                }
                mList.add(m);
            }
        }

        int num = list.size();
        for (int i = 0; i+columnCount-1 <num-(num%columnCount); i = i + columnCount) {
            MovieInfo[] movieInfo = new MovieInfo[columnCount];
            for (int j=0;j<columnCount;j++){
                movieInfo[j] = list.get(i+j);
            }
            mList.add(movieInfo);
        }

        int lastLength=num%columnCount;
        if (lastLength>0){
            MovieInfo[] movieArray = new MovieInfo[lastLength];
            for (int i=0;i<lastLength;i++){
                movieArray[i]=list.get(num-1-lastLength+i);
            }
            mList.add(movieArray);
        }

    }

    int fetchMovieTimes =0;
    public void fetchMovies(int page){
        final int p=page;
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
                if (list.size()>0){
                    mList.clear();
                    addList(list);
                    notifyDataSetChanged();
                    fetchMovieTimes =0;
                }else if (fetchMovieTimes >2){
                    mList.clear();
                    notifyDataSetChanged();
                    fetchMovieTimes =0;
                }else if (fetchMovieTimes <3){
                    fetchMovies(p);
                    fetchMovieTimes++;
                }

            }
            @Override
            public void onError(int i, String s) {
                if (fetchMovieTimes >2){
                    mList.clear();
                    notifyDataSetChanged();
                    fetchMovieTimes =0;
                }else {
                    fetchMovies(p);
                    fetchMovieTimes++;
                }

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
        mList.remove(position);
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

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, DetailActivity.class);
        MovieInfo movieInfo = (MovieInfo) v.getTag();
        intent.putExtra(DetailActivity.INTENT_DETAIL, movieInfo);
        mContext.startActivity(intent);
    }

}
