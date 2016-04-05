package com.coolcool.moviecool.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.VideoPagerAdapter;
import com.coolcool.moviecool.custom.TopTabView;
import com.coolcool.moviecool.fragment.common.StateFragment;
import com.coolcool.moviecool.fragment.video.SubAllGridVideosFragment;
import com.coolcool.moviecool.fragment.video.SubCommonPageFragment;
import com.coolcool.moviecool.fragment.video.SubPerfectFragment;
import com.coolcool.moviecool.fragment.video.SubTrailerFragment;
import com.coolcool.moviecool.model.PageType;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainVideoFragment extends StateFragment implements View.OnClickListener{
    public static final String TAG="MainVideoFragment";

    public int COLOR_ACCENT;
    public int COLOR_PRIMARY_DARK;

    //所有影片
    private SubAllGridVideosFragment mAllGridVideosFragment;
    //精选片
    private SubPerfectFragment mPerfectFragment;

    private SubCommonPageFragment mHollywoodFragment;
    private SubCommonPageFragment mRecentUpdateFragment;

    //预告片
    private SubTrailerFragment mTrailerFragment;

    private SubCommonPageFragment mComedyFragment;
    private SubCommonPageFragment mActionFragment;
    private SubCommonPageFragment mAffectionalFragment;
    private SubCommonPageFragment mScienceFragment;
    private SubCommonPageFragment mClassicFragment;
    private ArrayList<Fragment> mList;
    private VideoPagerAdapter mPagerAdapter;
    private FragmentManager mFragmentManager;
    private TopTabView mTopTabView;

    //电影页面类型列表
    private List<PageType> mPageTypes =new ArrayList<>();
    private Context mContext;

    ViewPager mViewPager;
    public MainVideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        COLOR_ACCENT=getResources().getColor(R.color.colorAccent);
        COLOR_PRIMARY_DARK=getResources().getColor(R.color.colorPrimaryDark);

        View view=inflater.inflate(R.layout.fragment_main_video, container, false);

        initView(view);
        initData();

        return view;
    }

    //初始化视图
    private void initView(View view){
        mTopTabView = (TopTabView) view.findViewById(R.id.tabMainVideo);
        mViewPager= (ViewPager) view.findViewById(R.id.vpVideoInfo);

        for (int i=0;i< Constant.videoTabs.length;i++){
            TextView textView= (TextView) mTopTabView.findViewWithTag(Constant.videoTabs[i]);
            textView.setOnClickListener(this);
        }
    }

    //抓取电影展示页面类型数据
    int fetchCount=0;
    private void fetchData(){

        if (mPageTypes.size()>0){
            initData();
            return;
        }

        BmobQuery<PageType> query=new BmobQuery<>();
        query.addWhereNotEqualTo("pageName", "只要不是空就行");
        query.order("pageNumber");
        query.findObjects(mContext, new FindListener<PageType>() {
            @Override
            public void onSuccess(List<PageType> list) {
                Log.i(TAG, "成功下载page==，个数" + list.size());
                if (list.size() > 0) {
                    mPageTypes = list;
                    initData();

                    fetchCount = 0;
                } else if (fetchCount > 2) {
                    fetchCount = 0;
                } else if (fetchCount < 3) {
                    fetchData();
                    fetchCount++;
                }
            }

            @Override
            public void onError(int i, String s) {
                if (fetchCount > 2) {
                    fetchCount = 0;
                } else {
                    fetchData();
                    fetchCount++;
                }
                Log.i(TAG, "下载失败 i==" + i + "   " + s);
            }
        });
    }

    //初始化fragmentList数据
    private void initData(){
        mList=new ArrayList<>();

        //全部页面
        mAllGridVideosFragment=new SubAllGridVideosFragment();
        //精选页面
        mPerfectFragment=new SubPerfectFragment();
        Bundle perfectBundle=new Bundle();
        perfectBundle.putString(SubPerfectFragment.SUB_COMEDY_CRITERIA,"精选");
        mPerfectFragment.setArguments(perfectBundle);

        //好莱坞页面
        mHollywoodFragment=new SubCommonPageFragment();
        Bundle hollywoodBundle=new Bundle();
        hollywoodBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA,"好莱坞");
        mHollywoodFragment.setArguments(hollywoodBundle);

        mRecentUpdateFragment =new SubCommonPageFragment();
        Bundle recentUpdateBundle=new Bundle();
        recentUpdateBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA, "最近更新");
        mRecentUpdateFragment.setArguments(recentUpdateBundle);

        //预告片页面
        mTrailerFragment =new SubTrailerFragment();

        mComedyFragment=new SubCommonPageFragment();
        Bundle comedyBundle=new Bundle();
        comedyBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA,"喜剧片");
        mComedyFragment.setArguments(comedyBundle);

        mActionFragment=new SubCommonPageFragment();
        Bundle actionBundle=new Bundle();
        actionBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA, "动作片");
        mActionFragment.setArguments(actionBundle);

        mAffectionalFragment=new SubCommonPageFragment();
        Bundle affectionBundle=new Bundle();
        affectionBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA, "爱情片");
        mAffectionalFragment.setArguments(affectionBundle);

        mScienceFragment=new SubCommonPageFragment();
        Bundle scienceBundle=new Bundle();
        scienceBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA, "科幻片");
        mScienceFragment.setArguments(scienceBundle);

        mClassicFragment=new SubCommonPageFragment();
        Bundle  classicBundle=new Bundle();
        classicBundle.putString(SubCommonPageFragment.SUB_COMMON_CRITERIA, "经典老片");
        mClassicFragment.setArguments(classicBundle);

        mList.add(mAllGridVideosFragment);
        mList.add(mPerfectFragment);

        mList.add(mHollywoodFragment);
        mList.add(mRecentUpdateFragment);

        mList.add(mTrailerFragment);

        mList.add(mComedyFragment);
        mList.add(mActionFragment);
        mList.add(mAffectionalFragment);
        mList.add(mScienceFragment);
        mList.add(mClassicFragment);

        for (int i=0;i< mPageTypes.size();i++){
            PageType type= mPageTypes.get(i);

            Log.i(TAG,"第  "+i+"  个的名字是   "+type.getPageName());

            if (i!=0){
                SubCommonPageFragment fragment=new SubCommonPageFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable(SubCommonPageFragment.SUB_COMMON_CRITERIA, type);
                fragment.setArguments(bundle);
                mList.add(fragment);
            }else {
                SubAllGridVideosFragment fragment=new SubAllGridVideosFragment();
                mList.add(fragment);
            }
        }

//        for (int i=0;i< VideoUtils.PAGE_TYPE_LIST.size();i++){
//            ArrayList<DisplayType> types= (ArrayList<DisplayType>) VideoUtils.PAGE_TYPE_LIST.get(i);
//
//            Log.i(TAG,"第  "+i+"  个的名字是   "+types.size());
//
//            if (i!=0){
//                SubCommonPageFragment fragment=new SubCommonPageFragment();
//                Bundle bundle=new Bundle();
//                bundle.putSerializable(SubCommonPageFragment.SUB_COMMON_CRITERIA, types);
//                fragment.setArguments(bundle);
//                mList.add(fragment);
//            }else {
//                SubAllGridVideosFragment fragment=new SubAllGridVideosFragment();
//                mList.add(fragment);
//            }
//        }

        mFragmentManager=getChildFragmentManager();
        mPagerAdapter=new VideoPagerAdapter(mFragmentManager,mList);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.addOnPageChangeListener(mPageChangeListener);

        initEvent(1);
    }

    //初始化事件
    private void initEvent(int position){
        mViewPager.setCurrentItem(position);
        mTopTabView.scroll(position, -1);
    }

    //viewPager的滚动监听器
    ViewPager.OnPageChangeListener mPageChangeListener=new ViewPager.OnPageChangeListener() {
        boolean scrolling=false;
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (!scrolling) return;
            if (position>=mList.size()-1) return;

            mTopTabView.scroll(position,positionOffset);
        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state){
                case ViewPager.SCROLL_STATE_IDLE:
                    scrolling=false;
                    mTopTabView.scroll(mViewPager.getCurrentItem(),-1);
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    scrolling=true;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    scrolling=true;
                    break;
            }
        }
    };

    //tab中textView的点击事件监听器
    @Override
    public void onClick(View v) {
        for (int i=0;i< Constant.videoTabs.length;i++){
            String tag= (String) v.getTag();
            if (tag.equals(Constant.videoTabs[i])){
                mViewPager.setCurrentItem(i);
            }
        }
    }

}
