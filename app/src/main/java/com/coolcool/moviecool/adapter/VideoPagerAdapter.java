package com.coolcool.moviecool.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 影视推荐页面内的ViewPager的适配器
 * Created by yanshili on 2016/3/14.
 */
public class VideoPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mList;

    public VideoPagerAdapter(FragmentManager fm,List<Fragment> list) {
        super(fm);
        mList=list;
    }

    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

}
