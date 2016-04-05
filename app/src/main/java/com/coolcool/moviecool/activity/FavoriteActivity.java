package com.coolcool.moviecool.activity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.SubRecyclerAdapter;
import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.utils.TintDrawableUtil;
import com.coolcool.moviecool.holder.LinearGridViewHolder;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class FavoriteActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG="FavoriteActivity";

    private RecyclerView mRecyclerView;
    //    private TextView textBackground;
    private SubRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<ItemFeed> mFeedList=new ArrayList<>();

    private ImageView ivArrowMark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        initVew();

        fetchFavoriteMovie();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchFavoriteMovie();
    }

    private void initVew() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ivArrowMark=(ImageView) findViewById(R.id.ivArrowMark);

        if (ivArrowMark!=null){
            Drawable drawable= TintDrawableUtil
                    .tintDrawable(this,R.drawable.ic_arrow_back_24dp,R.color.colorWhite);
            ivArrowMark.setImageDrawable(drawable);
            ivArrowMark.setOnClickListener(this);
        }

        mRecyclerView= (RecyclerView) findViewById(R.id.favoriteRecyclerView);

        mLayoutManager=new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        mLayoutManager.setAutoMeasureEnabled(true);

        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    //查询收藏
    private void fetchFavoriteMovie(){
        BmobQuery<MovieInfo> query=new BmobQuery<>();
        query.addWhereRelatedTo("favorites", new BmobPointer(Constant.ordinaryUser));
        query.findObjects(this, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                Log.i(TAG, "当前用户喜欢的电影个数== " + list.size());
                mFeedList=new ArrayList<>();
                mAdapter=new SubRecyclerAdapter(FavoriteActivity.this);
                mRecyclerView.setAdapter(mAdapter);
                if (list.size()>0){
                    for (MovieInfo movie : list) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, movie);
                        ItemFeed feed = new ItemFeed(bundle, SubRecyclerAdapter.VIEW_TYPE_GRID_VIEW);
                        mFeedList.add(feed);
                        mAdapter.addData(feed);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                if (mFeedList.size()>0){
                    mRecyclerView.setBackgroundColor(Color.parseColor("#ff99cc00"));
                }else {
                    mRecyclerView.setBackgroundColor(Color.parseColor("#00000000"));
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivArrowMark:
                super.onBackPressed();
                break;
        }
    }
}
