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

import com.coolcool.moviecool.common.Constant;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.SubRecyclerAdapter;
import com.coolcool.moviecool.holder.CarouselViewHolder;
import com.coolcool.moviecool.holder.LinearGridViewHolder;
import com.coolcool.moviecool.holder.SingleImageHolder;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.coolcool.moviecool.model.MoviePage;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubPerfectFragment extends Fragment {
    public static final String TAG="SubPerfectFragment";
    public static final String SUB_COMEDY_CRITERIA="SubComedyFragment.SUB_COMEDY_CRITERIA";

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


    public SubPerfectFragment() {
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
        perfectCriteria =perfectBundle.getString(SUB_COMEDY_CRITERIA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sub_perfect, container, false);

        initView(view);

        if (mFeedList.size()>0){
            mAdapter=new SubRecyclerAdapter(mContext,mFeedList);
            mRecyclerView.setAdapter(mAdapter);
        }else {
            initData();
        }

        return view;
    }

    private void initView(View view){
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerPerfectFragment);
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

        carouselList = new ArrayList<>();
        fantasticList = new ArrayList<>();
        hotList = new ArrayList<>();
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
        Log.i(TAG,"热门的个数     "+hotList.size());
        hotBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, hotList);
        hotBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "热门");
        hotBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedHot=new ItemFeed(hotBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedHot);

        mAdapter=new SubRecyclerAdapter(mContext, mFeedList);

        mRecyclerView.setAdapter(mAdapter);
    }

}
