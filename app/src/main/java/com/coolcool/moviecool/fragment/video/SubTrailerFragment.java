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

import com.coolcool.moviecool.utils.Constant;
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
public class SubTrailerFragment extends Fragment {


    public static final String TAG="SubTrailerFragment";
    public static final String SUB_TRAILER_CRITERIA="SubTrailerFragment.SUB_TRAILER_CRITERIA";

    private Context mContext;
    private List<MoviePage> mMovieTrailerList =new ArrayList<>();
    private List<ItemFeed> mFeedList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    Bundle carouselBundle;
    Bundle expectBundle;
    Bundle recentShowBundle;
    Bundle justAddedBundle;
    Bundle specialBundle;

//    String trailerCriteria;

    ArrayList<MovieInfo> carouselList=new ArrayList<>();
    ArrayList<MovieInfo> expectList =new ArrayList<>();
    ArrayList<MovieInfo> justAddedList =new ArrayList<>();
    ArrayList<MovieInfo> recentShowList =new ArrayList<>();
    ArrayList<MovieInfo> specialList=new ArrayList<>();

    SubRecyclerAdapter mAdapter;
    RecyclerView.LayoutManager mLayoutManager;


    public SubTrailerFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sub_trailer, container, false);

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerTrailerFragment);
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
//        if (trailerCriteria ==null) return;

        if (mMovieTrailerList.size()>0){
            processData(mMovieTrailerList);
            return;
        }

        BmobQuery<MoviePage> query=new BmobQuery<>();
        query.addWhereContains("pageName", "预告片");
        query.order("pageOrder");
        query.include("movieInfo");
        query.findObjects(mContext, new FindListener<MoviePage>() {
            @Override
            public void onSuccess(List<MoviePage> list) {
                Log.i(TAG, "成功下载trailer==，个数" + list.size());
                if (list.size() > 0) {
                    mMovieTrailerList = list;
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

        for (int i = 0; i < list.size(); i++) {
            MoviePage trailer = list.get(i);
            MovieInfo movie=trailer.getMovieInfo();
            switch (trailer.getPageArea()) {
                case "轮播":
                    carouselList.add(movie);
                    break;
                case "特别":
                    specialList.add(movie);
                case "高期望值":
                    expectList.add(movie);
                    break;
                case "最新预告":
                    justAddedList.add(movie);
                    break;
                case "即将上映":
                    recentShowList.add(movie);
                    break;
            }
        }

        //轮播图片的数据分类
        carouselBundle=new Bundle();
        carouselBundle.putSerializable(CarouselViewHolder.SUB_CAROUSEL_DATA, carouselList);
        ItemFeed feedCarousel=new ItemFeed(carouselBundle, SubRecyclerAdapter.VIEW_TYPE_CAROUSEL);
        mFeedList.add(feedCarousel);

        //特别预告的数据分类
        specialBundle = new Bundle();
        specialBundle.putSerializable(SingleImageHolder.SUB_GRID_VIEW_DATA, specialList);
        ItemFeed feedSpecial=new ItemFeed(specialBundle, SubRecyclerAdapter.VIEW_TYPE_SINGLE_IMAGE);
        mFeedList.add(feedSpecial);

        //最新预告的数据分类
        justAddedBundle = new Bundle();
        justAddedBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, justAddedList);
        justAddedBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "最新预告");
        justAddedBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedJustAdded=new ItemFeed(justAddedBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedJustAdded);

        //即将上映的数据分类
        recentShowBundle = new Bundle();
        recentShowBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, recentShowList);
        recentShowBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "即将上映");
        recentShowBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedRecentShow=new ItemFeed(recentShowBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedRecentShow);

        //高期待值的数据分类
        expectBundle = new Bundle();
        expectBundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, expectList);
        expectBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_LEFT, "高期望值");
        expectBundle.putString(LinearGridViewHolder.SUB_GRID_VIEW_HEADER_RIGHT, "更多影片");
        ItemFeed feedExpect=new ItemFeed(expectBundle, SubRecyclerAdapter.VIEW_TYPE_GRID);
        mFeedList.add(feedExpect);

        mMovieTrailerList=new ArrayList<>();
        mAdapter=new SubRecyclerAdapter(mContext, mFeedList);
        mRecyclerView.setAdapter(mAdapter);
    }

//    CoordinatorLayout.Behavior behavior;
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        if(behavior != null)
//            return;
//
//        FrameLayout layout =(FrameLayout) getParentFragment().getActivity().findViewById(R.id.mainFragmentContainer);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
//
//        behavior = params.getBehavior();
//        params.setBehavior(null);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if(behavior == null)
//            return;
//
//        FrameLayout layout =(FrameLayout) getActivity().findViewById(R.id.mainFragmentContainer);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) layout.getLayoutParams();
//
//        params.setBehavior(behavior);
//
//        layout.setLayoutParams(params);
//
//        behavior = null;
//    }

}
