package com.coolcool.moviecool.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.holder.ViewHolder;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yanshili on 2016/3/29.
 */
public class SavedGridAdapter extends BaseAdapter implements View.OnClickListener{
    public static final String TAG="BmobGridAdapter";

    //与查询条件集合
    List<BmobQuery<MovieInfo>> andQueries=new ArrayList<BmobQuery<MovieInfo>>();
    List<ArrayList<BmobQuery<MovieInfo>>> listArray=new ArrayList<>();
    //每页加载的个数
    public static final int A_PAGE_COUNT=15;

    private static SavedGridAdapter instance;

    //是否为初始化状态
    private boolean initState=true;
    //确认是否加载图片
    private volatile boolean loadingFlag =true;
    //某条件下可得到的电影个数
    public volatile int itemCount =10;
    private ListView mListView =null;
    //    private ArrayList<MovieInfo> mMovieInfoList=new ArrayList<MovieInfo>();
    private ArrayList<MovieInfo[]> mList=new ArrayList<>();
    private Context mContext;
    private LayoutInflater mInflater;
    private DataCacheBase mDataCacheBase;

    private static synchronized void init(ListView gridView){
        if (instance==null){
            instance=new SavedGridAdapter(gridView);
        }
    }

    public static SavedGridAdapter getInstance(ListView listView){
        if (instance==null){
            init(listView);
        }
        return instance;
    }

    private SavedGridAdapter(ListView listView) {
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
        if (convertView==null){
            convertView=mInflater.inflate(R.layout.grid_item_movie, null, false);
        }

        ViewHolder holder=ViewHolder.getHolder(convertView);

        MovieInfo[] mArray=getItem(position);

        if (mArray.length!=3){
            convertView=mInflater.inflate(R.layout.grid_item_movie,null,false);
            convertView.setTag(null);

            holder=ViewHolder.getHolder(convertView);
        }

        FrameLayout.LayoutParams params=new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (20* Constant.dp));
        params.gravity= Gravity.BOTTOM;

        int backgroundColor= Color.WHITE;

        TextView tvTitle0=holder.getView(R.id.tvMovieTitle0);
        SimpleDraweeView imageView0=holder.getView(R.id.ivPoster0);
        if (mArray.length>0&&mArray[0]!=null){
//            tvTitle0.setBackgroundColor(colorWhite);
            imageView0.setBackgroundColor(backgroundColor);
            //为imageView设置默认图片
//            imageView0.setImageResource(R.mipmap.ic_launcher);

            FrameLayout frameLayout= (FrameLayout) imageView0.getParent();
            View bgView=new View(mContext);
            bgView.setLayoutParams(params);
            bgView.setBackgroundResource(R.drawable.bg_gradient_white);
            frameLayout.addView(bgView,-1);

            CardView linearLayout= (CardView) tvTitle0.getParent();
            linearLayout.setTag(mArray[0]);
            linearLayout.setOnClickListener(this);

            fillView(imageView0, tvTitle0, mArray[0]);
        }else {
            CardView linearLayout= (CardView) tvTitle0.getParent();
            linearLayout.setCardElevation(0);
            linearLayout.setRadius(0);
            linearLayout.setCardBackgroundColor(Color.parseColor("#00000000"));
        }

        TextView tvTitle1=holder.getView(R.id.tvMovieTitle1);
        SimpleDraweeView imageView1=holder.getView(R.id.ivPoster1);
        if (mArray.length>1&&mArray[1]!=null){
//            tvTitle1.setBackgroundColor(colorWhite);
            imageView1.setBackgroundColor(backgroundColor);
            //为imageView设置默认图片
//            imageView1.setImageResource(R.mipmap.ic_launcher);

            FrameLayout frameLayout= (FrameLayout) imageView1.getParent();
            View bgView=new View(mContext);
            bgView.setLayoutParams(params);
            bgView.setBackgroundResource(R.drawable.bg_gradient_white);
            frameLayout.addView(bgView, -1);

            CardView linearLayout= (CardView) tvTitle1.getParent();
            linearLayout.setTag(mArray[1]);
            linearLayout.setOnClickListener(this);

            fillView(imageView1,tvTitle1,mArray[1]);
        }else {
            CardView linearLayout= (CardView) tvTitle1.getParent();
            linearLayout.setCardElevation(0);
            linearLayout.setRadius(0);
            linearLayout.setCardBackgroundColor(Color.parseColor("#00000000"));
        }

        TextView tvTitle2=holder.getView(R.id.tvMovieTitle2);
        SimpleDraweeView imageView2=holder.getView(R.id.ivPoster2);
        if (mArray.length==3&&mArray[2]!=null){
//            tvTitle2.setBackgroundColor(colorWhite);
            imageView2.setBackgroundColor(backgroundColor);
            //为imageView设置默认图片
//            imageView2.setImageResource(R.mipmap.ic_launcher);

            FrameLayout frameLayout= (FrameLayout) imageView2.getParent();
            View bgView=new View(mContext);
            bgView.setLayoutParams(params);
            bgView.setBackgroundResource(R.drawable.bg_gradient_white);
            frameLayout.addView(bgView, -1);

            CardView linearLayout= (CardView) tvTitle2.getParent();
            linearLayout.setTag(mArray[2]);
            linearLayout.setOnClickListener(this);

            fillView(imageView2, tvTitle2, mArray[2]);
        }else {
            CardView linearLayout= (CardView) tvTitle2.getParent();
            linearLayout.setCardElevation(0);
            linearLayout.setRadius(0);
            linearLayout.setCardBackgroundColor(Color.parseColor("#00000000"));
        }

        return convertView;
    }

    public void fillView(SimpleDraweeView imageView,TextView textView,MovieInfo movie){
        if (imageView==null||textView==null||movie==null) return;
        //根据position获取电影url
        final String htmlUrl= convertIdToUrl(movie.getPostId());
        imageView.setTag(htmlUrl);
        //根据电影页面url，从内存缓存中取得电影信息对象
        MovieInfo movieInfo= mDataCacheBase.getDataFromMemoryCache(htmlUrl);
        if (movieInfo==null){
            movieInfo=movie;
        }
        if (movieInfo!=null){

            String imageUrl=movieInfo.getPosterUrl();
            String movieName=movieInfo.getName()!=null?movieInfo.getName():movieInfo.getTitle();
            textView.setText(movieName);
            if (imageUrl!=null)
                imageView.setImageURI(Uri.parse(imageUrl));

//            Bitmap bitmap=mDataCacheBase.getBitmapFromMemoryCache(movieInfo.getPosterUrl());
//            if (bitmap!=null){
//                imageView.setImageBitmap(bitmap);
//            }else if (loadingFlag){
//                loadBitmapFromNet(movieInfo);
//            }
        }
    }

//    public void startLoadBitmap(){
//        int first= mListView.getFirstVisiblePosition();
//        int last= mListView.getLastVisiblePosition();
//
//        if (last-first<=0) return;
////        if (first>0){
////            first=first-1;
////            last=last-1;
////        }
//        for (int i=first;i<last+1;i++){
//            //因为添加了headerView所以尽量不要用adapter.getItem()方法
//            //listView.getItemAtPosition偶尔也会出错（即：index out size原因暂时不知）
//            if (getCount()>last){
//                MovieInfo[] movies= (MovieInfo[]) mListView.getItemAtPosition(i);
//                if (movies!=null&&movies.length>0){
//                    for (MovieInfo move:movies){
//                        loadBitmapFromNet(move);
//                    }
//                }
//            }
//        }
//    }

//    public void loadBitmapFromMemory(MovieInfo movieInfo){
//        if (movieInfo==null) return;
//        String htmlUrl=movieInfo.getHtmlUrl();
//        String imageUrl=movieInfo.getPosterUrl();
//        ImageView imageView= (ImageView) mListView.findViewWithTag(htmlUrl);
//        if (imageView!=null){
//            Bitmap bitmap=mDataCacheBase.getBitmapFromMemoryCache(imageUrl,TAG);
//            if (bitmap!=null){
//                imageView.setImageBitmap(bitmap);
//            }else if (loadingFlag){
//                loadBitmapFromNet(movieInfo);
//            }
//        }
//    }


    /**
     * 从网络下载图片，并根据条件决定是否填充到视图view内
     *
     */
//    public void loadBitmapFromNet(MovieInfo movieInfo){
//        final String htmlUrl=movieInfo.getHtmlUrl();
//        final String imgUrl=movieInfo.getPosterUrl();
//        ImageView view=(ImageView) mListView.findViewWithTag(htmlUrl);
//
//        AsyncNetUtils.loadBitmap(imgUrl, new AsyncNetUtils.Callback() {
//            @Override
//            public void onResponse(Object response) {
//
//                ImageView imageView = (ImageView) mListView.findViewWithTag(htmlUrl);
//
//                if (response != null) {
//                    Bitmap bitmap = (Bitmap) response;
//                    if (imageView != null) {
//                        imageView.setImageBitmap(bitmap);
//                    }
//                }
//            }
//        }, mContext,view,TAG);
//    }

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
        if (mList!=null&&mList.size()>0&&list.size()>0){
            MovieInfo[] last=mList.get(mList.size()-1);
            if (last.length==1&&list.size()>1){

                MovieInfo[] m=new MovieInfo[3];
                mList.remove(mList.size()-1);
                m[0]=last[0];
                m[1]=list.get(0);
                m[2]=list.get(1);

                mList.add(m);

                list.remove(m[1]);
                list.remove(m[2]);
            }else if (last.length==2){
                MovieInfo[] m=new MovieInfo[3];
                mList.remove(mList.size()-1);
                m[0]=last[0];
                m[1]=last[1];
                m[2]=list.get(0);

                mList.add(m);

                list.remove(m[2]);
            }
        }

        int num = list.size();
        for (int i = 0; i+2 <num-(num%3); i = i + 3) {
            MovieInfo[] movieInfos = new MovieInfo[3];
            movieInfos[0] = list.get(i);
            movieInfos[1] = list.get(i + 1);
            movieInfos[2] = list.get(i + 2);
            mList.add(movieInfos);
        }

        switch (num % 3) {
            case 0:
                break;
            case 1:
                MovieInfo[] m1 = new MovieInfo[]{list.get(num - 1)};
                mList.add(m1);
                break;
            case 2:
                MovieInfo[] m2 = new MovieInfo[2];
                m2[0] = list.get(num - 2);
                m2[1] = list.get(num - 1);
                mList.add(m2);
                break;
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
