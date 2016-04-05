package com.coolcool.moviecool.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * 轮播图片的ViewPager的适配器
 * Created by yanshili on 2016/3/22.
 */
public class CarouselPagerAdapter extends PagerAdapter{
    List<SimpleDraweeView> mImageViews;
    Context mContext;

    public CarouselPagerAdapter(Context context, List<SimpleDraweeView> imageViews) {
        mContext = context;
        mImageViews=imageViews;

    }

    @Override
    public int getCount() {
        return mImageViews.size()*800;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mImageViews.get(position % mImageViews.size()));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view=mImageViews.get(position%mImageViews.size());
        ViewGroup viewParent= (ViewGroup) view.getParent();
        if (viewParent!=null) {
            viewParent.removeView(view);
        }

        container.addView(view);
        return view;
    }

}
