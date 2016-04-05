package com.coolcool.moviecool.holder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.custom.ItemGridSingle;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanshili on 2016/3/30.
 */
public class SingleImageHolder extends RecyclerBaseViewHolder implements View.OnClickListener{
    public static final String TAG="SingleImageHolder";
    public static final String SUB_GRID_VIEW_DATA ="SUB_GRID_VIEW_DATA";
    public static final String SUB_GRID_VIEW_HEADER_LEFT ="SUB_GRID_VIEW_HEADER_LEFT";
    public static final String SUB_GRID_VIEW_HEADER_RIGHT ="SUB_GRID_VIEW_HEADER_RIGHT";
    public static final String SUB_GRID_VIEW_HEADER_IMAGE ="SUB_GRID_VIEW_HEADER_IMAGE";

    private Context mContext;
    private ArrayList<MovieInfo> mMovieInfoList;
    private ArrayList<View> viewList;
    private Bundle mData;

    private LayoutInflater mInflater;

    private GridLayout mGridLayout;
    private int columnCount=0;
    private float WHRatio=0.6f;

    public SingleImageHolder(View itemView, Context context,int columnCount,float WHRatio) {
        super(itemView,context);
        mContext=context;
        this.WHRatio=WHRatio;
        this.columnCount=columnCount;

        mInflater=LayoutInflater.from(context);

        initView(itemView);
    }

    public SingleImageHolder(View itemView, Context context) {
        super(itemView,context);
        mContext=context;
        mInflater=LayoutInflater.from(context);

        initView(itemView);
    }

    private List<CardView> cardViews;
    private List<SimpleDraweeView> imageViews;
    private List<TextView> textViews;
    private View parent;
    FrameLayout frameLayout;
    @Override
    protected void initView(View view) {
        parent=view;

        frameLayout= (FrameLayout) view.findViewById(R.id.singleImageGrid);
        if (columnCount==0) return;
        mGridLayout= new ItemGridSingle(mContext,columnCount,WHRatio);
        frameLayout.addView(mGridLayout);

        cardViews=new ArrayList<>();
        imageViews=new ArrayList<>();
        textViews=new ArrayList<>();

        for (int i=0;i<columnCount;i++){

            View itemImageView = mGridLayout.getChildAt(i);

            if (itemImageView != null) {
                CardView cardView = (CardView) itemImageView.findViewById(R.id.cardViewGridCommon);
                SimpleDraweeView imageView = (SimpleDraweeView) itemImageView.findViewById(R.id.imageViewGridCommon);
                TextView textView = (TextView) itemImageView.findViewById(R.id.textViewGridCommon);
                View bottomShadow = itemImageView.findViewById(R.id.bottomShadowGrid);

                bottomShadow.setEnabled(true);

                cardView.setCardBackgroundColor(Color.WHITE);
                cardView.setCardElevation(4 * Constant.dp);
                cardView.setRadius(2 * Constant.dp);
                cardView.setOnClickListener(this);

                cardViews.add(cardView);
                textViews.add(textView);
                imageViews.add(imageView);
            }
        }

        fillView();
    }

    @Override
    public void initData(ItemFeed itemFeed) {
        if (itemFeed==null) return;
        mData=itemFeed.getData();
        mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(SUB_GRID_VIEW_DATA);
        fillView();
    }

    @Override
    protected void fillView() {
        if (mMovieInfoList!=null&&mMovieInfoList.size()>0){
            if (columnCount==0&&parent!=null){
                columnCount=mMovieInfoList.size();
                initView(parent);
            }

            for (int i=0;i<mMovieInfoList.size();i++){
                MovieInfo movie=mMovieInfoList.get(i);
                CardView cardView=cardViews.get(i);
                TextView textView=textViews.get(i);
                SimpleDraweeView imageView=imageViews.get(i);

                cardView.setTag(movie);
                textView.setText(movie.getName());
                String posterUrl = movie.getPosterUrl();
                if (posterUrl != null) {
                    imageView.setTag(movie.getPosterUrl());
                    imageView.setImageURI(Uri.parse(posterUrl));
                }else {
                    imageView.setImageURI(null);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        MovieInfo movie= (MovieInfo) v.getTag();
        if (movie==null) return;
        Intent intent=new Intent(mContext, DetailActivity.class);
        intent.putExtra(DetailActivity.INTENT_DETAIL,movie);
        mContext.startActivity(intent);
    }
}
