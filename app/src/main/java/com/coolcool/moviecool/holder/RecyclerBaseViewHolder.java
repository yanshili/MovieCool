package com.coolcool.moviecool.holder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.coolcool.moviecool.model.ItemFeed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanshili on 2016/3/26.
 */
public abstract class RecyclerBaseViewHolder extends RecyclerView.ViewHolder {
    Context mContext;
    //空的imageView视图，即需要加载图片的imageView集合，根据此集合去下载图片
    List<ImageView> emptyImageViews=new ArrayList<>();

    protected RecyclerBaseViewHolder(View itemView,Context context) {
        super(itemView);
        mContext=context;
    }

    //初始化视图
    protected abstract void initView(View itemView);
    //初始化数据
    public abstract void initData(ItemFeed itemFeed);
    //将数据填充视图
    protected abstract void fillView();

}
