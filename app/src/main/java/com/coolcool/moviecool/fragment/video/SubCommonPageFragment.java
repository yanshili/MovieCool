package com.coolcool.moviecool.fragment.video;


import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.SubRecyclerAdapter;
import com.coolcool.moviecool.holder.CarouselViewHolder;
import com.coolcool.moviecool.holder.LinearGridViewHolder;
import com.coolcool.moviecool.holder.SingleImageHolder;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.coolcool.moviecool.model.MoviePage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubCommonPageFragment extends Fragment {
    public static final String TAG="SubCommonPageFragment";
    public static final String SUB_COMMON_CRITERIA ="SubCommonPageFragment.SUB_COMMON_CRITERIA";

    private Context mContext;
    private List<MoviePage> mMoviePageList=new ArrayList<>();
    private List<ItemFeed> mFeedList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    Bundle carouselBundle;
    Bundle specialBundle;
    Bundle hotBundle;
    Bundle fantasticBundle;

    String perfectCriteria;

    ArrayList<MovieInfo> carouselList=new ArrayList<>();
    ArrayList<MovieInfo> fantasticList=new ArrayList<>();
    ArrayList<MovieInfo> hotList=new ArrayList<>();
    ArrayList<MovieInfo> specialList=new ArrayList<>();

    SubRecyclerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;


    public SubCommonPageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle perfectBundle=getArguments();
        if (perfectBundle!=null)
            perfectCriteria =perfectBundle.getString(SUB_COMMON_CRITERIA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sub_common_page, container, false);

        initView(view);

        if (mAdapter!=null){
            mRecyclerView.setAdapter(mAdapter);
        }else {
            initData();
        }

        return view;
    }

    private void initView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerCommonFragment);
        mLayoutManager=new LinearLayoutManager(mContext);
        mLayoutManager.setAutoMeasureEnabled(true);

        RecyclerView.ItemDecoration decoration=new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom=(int) (16* Constant.dp);
            }
        };

        mRecyclerView.addItemDecoration(decoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    //抓取数据
    int fetchCount=0;
    private void initData() {
        if (perfectCriteria ==null) return;

        if (mMoviePageList.size()>0){
            processData(mMoviePageList);
            return;
        }

        BmobQuery<MoviePage> query=new BmobQuery<>();
        query.addWhereContains("pageName", perfectCriteria);
        query.order("pageOrder");
        query.include("movieInfo");
        query.findObjects(mContext, new FindListener<MoviePage>() {
            @Override
            public void onSuccess(List<MoviePage> list) {
                Log.i(TAG, "成功下载page==，个数" + list.size());
                if (list.size() > 0) {
                    mMoviePageList = list;
                    processData(list);

                    fetchCount = 0;
                } else if (fetchCount > 2) {
                    fetchCount = 0;
                } else if (fetchCount < 3) {
                    initData();
                    fetchCount++;
                }
            }

            @Override
            public void onError(int i, String s) {
                if (fetchCount > 2) {
                    fetchCount = 0;
                } else {
                    initData();
                    fetchCount++;
                }
                Log.i(TAG, "下载失败 i==" + i + "   " + s);
            }
        });
    }

    //处理数据，将数据分类
    private void processData(List<MoviePage> list){
        if (list.size()>0) mFeedList.clear();

        carouselList = new ArrayList<MovieInfo>();
        fantasticList = new ArrayList<MovieInfo>();
        hotList = new ArrayList<MovieInfo>();
        specialList=new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            MoviePage page = list.get(i);
            MovieInfo movie = page.getMovieInfo();
            switch (page.getPageArea()) {
                case "轮播":
                    carouselList.add(movie);
                    break;
                case "精彩推荐":
                    fantasticList.add(movie);
                    break;
                case "热门":
                    hotList.add(movie);
                    break;
                case "特别":
                    specialList.add(movie);
                    break;
            }
        }

        //轮播图片的数据分类
        carouselBundle=new Bundle();
        carouselBundle.putSerializable(CarouselViewHolder.SUB_CAROUSEL_DATA, carouselList);
        ItemFeed feedCarousel=new ItemFeed(carouselBundle, SubRecyclerAdapter.VIEW_TYPE_CAROUSEL);
        mFeedList.add(feedCarousel);

        //特别推荐的数据分类
        specialBundle = new Bundle();
        specialBundle.putSerializable(SingleImageHolder.SUB_GRID_VIEW_DATA, specialList);
        ItemFeed feedSpecial=new ItemFeed(specialBundle, SubRecyclerAdapter.VIEW_TYPE_SINGLE_IMAGE);
        mFeedList.add(feedSpecial);

        //精彩推荐的数据分类
        fantasticBundle = new Bundle();
        fantasticBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, fantasticList);
        fantasticBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "精彩推荐");
        fantasticBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedFantastic=new ItemFeed(fantasticBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedFantastic);

        //热门的数据分类
        hotBundle = new Bundle();
        hotBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, hotList);
        hotBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "热门");
        hotBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedHot=new ItemFeed(hotBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedHot);

        mAdapter=new SubRecyclerAdapter(mContext, mFeedList);

        mRecyclerView.setAdapter(mAdapter);
    }

    //根据不同页面建立不同的加载条件
    private BmobQuery<MovieInfo> processQuery(String pageName,BmobQuery<MovieInfo> query){
        if (pageName.contains("经典老片")){
            query.addWhereLessThanOrEqualTo("year", 2010);
            query.order("-updateDate");

            return query;
        }

        if (pageName.contains("片")){
            pageName=pageName.replace("片","");
            query.addWhereContains("genre", pageName);
            query.order("-year");

            return query;
        }

        if (pageName.contains("好莱坞")){
            List<BmobQuery<MovieInfo>> list=new ArrayList<>();
            BmobQuery<MovieInfo> query1=new BmobQuery<>();
            query1.addWhereContains("country","美国");
            list.add(query1);
            BmobQuery<MovieInfo> query2=new BmobQuery<>();
            query2.addWhereContains("genre",pageName);
            list.add(query2);
            query.or(list);
            query.order("-year");

            return query;
        }

        if (pageName.contains("最近更新")){
            long t=System.currentTimeMillis()-(long)30*24*60*60*1000;
            Date date=new Date(t);
            BmobDate bmobDate=new BmobDate(date);
            query.addWhereGreaterThanOrEqualTo("updateDate", bmobDate);
            query.order("-updateDate");

            return query;
        }

        return query;
    }

    //加载的页数
    int pageCount=0;
    //加载更多数据
    private void fetchNextData(){
        BmobQuery<MovieInfo> query=new BmobQuery<>();
        query=processQuery(perfectCriteria,query);
        query.setLimit(7);
        query.setSkip(7 * pageCount);
        query.findObjects(mContext, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                if (list.size()>0){
                    pageCount++;
                    Bundle bundle=new Bundle();
                    bundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, (Serializable) list);
                    ItemFeed feed=new ItemFeed(bundle,SubRecyclerAdapter.VIEW_TYPE_GRID);
                    mAdapter.addData(feed);
                    mAdapter.notifyDataSetChanged();

                    if (list.size()<7){
                        Toast.makeText(mContext, "已经到最后一项了", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    RecyclerView.OnScrollListener mScrollListener=new RecyclerView.OnScrollListener() {
        int lastPosition =0;
        int totalCount =0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager){
                lastPosition =((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                totalCount =layoutManager.getItemCount();
            }

            switch (newState){
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (lastPosition == totalCount -1){
                        //已经到最后一项了
                        fetchNextData();
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }
    };
}
