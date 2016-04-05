package com.coolcool.moviecool.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.custom.ItemImageView;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于展示普通2行3列网格类型视图的holder
 * Created by yanshili on 2016/3/26.
 */
public class LinearGridViewHolder extends RecyclerBaseViewHolder implements View.OnClickListener{
    public static final String TAG="LinearGridViewHolder";
    public static final String SUB_GRID_VIEW_DATA ="SUB_GRID_VIEW_DATA";
    public static final String SUB_GRID_VIEW_HEADER_LEFT ="SUB_GRID_VIEW_HEADER_LEFT";
    public static final String SUB_GRID_VIEW_HEADER_RIGHT ="SUB_GRID_VIEW_HEADER_RIGHT";
    public static final String SUB_GRID_VIEW_HEADER_IMAGE ="SUB_GRID_VIEW_HEADER_IMAGE";

    private Context mContext;
    private ArrayList<MovieInfo> mMovieInfoList;
    private String labelLeft;
    private String labelRight;
    private Bundle mData;
    //是否有头部的图片布局
    private boolean hasHeaderImage=true;

    private TextView rightText;
    private TextView leftText;
    private GridLayout mGridLayout;
    private LinearLayout linearLayout;
    private ItemImageView headerImageView;
    private int dataSize;

    public LinearGridViewHolder(View itemView, Context context
            , int columnCount, boolean hasHeader, int dataSize) {
        super(itemView,context);
        mContext=context;
        this.columnCount=columnCount;
        hasHeaderImage=hasHeader;
        this.dataSize=dataSize;
        initView(itemView);
    }

    private List<ItemImageView> mItemImageViews=new ArrayList<>();
    private List<SimpleDraweeView> mImageViews=new ArrayList<>();
    private List<TextView> mTextViews=new ArrayList<>();
    private List<View> mBottomViews=new ArrayList<>();

    @Override
    protected void initView(View view) {
        leftText= (TextView)view.findViewById(R.id.tvLabelLeft);
        rightText= (TextView)view.findViewById(R.id.tvLabelRight);
        mGridLayout= (GridLayout) view.findViewById(R.id.gridLayout);
        linearLayout= (LinearLayout) mGridLayout.getParent();
        mGridLayout.setColumnCount(columnCount);

        mItemImageViews=new ArrayList<>();
        mImageViews=new ArrayList<>();
        mTextViews=new ArrayList<>();
        mBottomViews=new ArrayList<>();

        for (int i=0;i<dataSize;i++){
            ItemImageView itemImageView=new ItemImageView(mContext,columnCount,hasHeaderImage);
            SimpleDraweeView imageView=itemImageView.getView(R.id.imageViewCommon);
            TextView textView=itemImageView.getView(R.id.textViewCommon);
            View bottomShadow=itemImageView.getView(R.id.bottomShadow);

            imageView.setOnClickListener(this);
            if (hasHeaderImage){
                headerImageView=itemImageView;
                linearLayout.addView(headerImageView,1);
                hasHeaderImage=false;
            }else {
                mGridLayout.addView(itemImageView);
            }
            mItemImageViews.add(itemImageView);
            mImageViews.add(imageView);
            mTextViews.add(textView);
            mBottomViews.add(bottomShadow);
        }

        fillView();
    }

    @Override
    public void initData(ItemFeed itemFeed) {
        if (itemFeed==null) return;
        mData=itemFeed.getData();
        mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(SUB_GRID_VIEW_DATA);
        labelLeft=mData.getString(SUB_GRID_VIEW_HEADER_LEFT);
        labelRight=mData.getString(SUB_GRID_VIEW_HEADER_RIGHT);
        Log.i(TAG,"左侧标签    "+labelLeft);

        fillView();
    }

    int columnCount=3;
    @Override
    protected void fillView() {
        if (mMovieInfoList!=null&&mMovieInfoList.size()>0){
            leftText.setText(labelLeft);
            rightText.setText(labelRight);
            Log.i(TAG, "个数==" + mMovieInfoList.size() );

            for (int i=0;i<mMovieInfoList.size();i++){
                MovieInfo movie=mMovieInfoList.get(i);
                Log.i(TAG, "名字==" + movie.getName() );

                SimpleDraweeView imageView=mImageViews.get(i);
                TextView textView=mTextViews.get(i);
                View bottomShadow=mBottomViews.get(i);

                textView.setText(movie.getName());
                bottomShadow.setEnabled(true);
                imageView.setTag(movie);
                if (movie.getPosterUrl()!=null)
                imageView.setImageURI(Uri.parse(movie.getPosterUrl()));

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
