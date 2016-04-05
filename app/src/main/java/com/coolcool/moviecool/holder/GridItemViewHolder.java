package com.coolcool.moviecool.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.SubRecyclerAdapter;
import com.coolcool.moviecool.fragment.SearchFragment;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by yanshili on 2016/4/2.
 */
public class GridItemViewHolder extends RecyclerBaseViewHolder implements View.OnClickListener{
    private SimpleDraweeView imageView;
    private TextView textView;
    private Bundle data;
    private MovieInfo mMovieInfo;

    public GridItemViewHolder(View itemView, Context context) {
        super(itemView, context);
        initView(itemView);
    }

    @Override
    protected void initView(View itemView) {
        imageView= (SimpleDraweeView) itemView.findViewById(R.id.recyclerGridItemImageView);
        textView= (TextView) itemView.findViewById(R.id.recyclerGridTextView);

        imageView.setOnClickListener(this);
    }

    @Override
    public void initData(ItemFeed itemFeed) {
        if (itemFeed==null) return;
        data=itemFeed.getData();
        mMovieInfo= (MovieInfo) data.getSerializable(SubRecyclerAdapter.SUB_RECYCLER_VIEW_DATA);
        if (mMovieInfo!=null){
            fillView();
        }
    }

    @Override
    protected void fillView() {
        if (mMovieInfo==null) return;
        Log.i(SearchFragment.TAG, "电影的名字== " + mMovieInfo.getName());
        String url=mMovieInfo.getPosterUrl();
        if (url!=null)
        imageView.setImageURI(Uri.parse(url));
        imageView.setTag(mMovieInfo);
        textView.setText(mMovieInfo.getName());
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
