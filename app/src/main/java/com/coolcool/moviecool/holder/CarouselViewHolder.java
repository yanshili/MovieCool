package com.coolcool.moviecool.holder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.adapter.CarouselPagerAdapter;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.coolcool.moviecool.utils.Constant;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * 用于展示的轮播图片类型的holder
 * Created by yanshili on 2016/3/26.
 */
public class CarouselViewHolder extends RecyclerBaseViewHolder implements View.OnClickListener{
    public static final String TAG="CarouselViewHolder";
    public static final String SUB_CAROUSEL_DATA ="SUB_CAROUSEL_DATA";
    public static final String SUB_CAROUSEL_STATE_INDEX ="CarouselViewHolder.SUB_CAROUSEL_STATE_INDEX";
    public static final String SUB_CAROUSEL_STATE_IMAGE_VIEWS ="CarouselViewHolder.SUB_CAROUSEL_STATE_IMAGE_VIEWS";
    public static final String SUB_CAROUSEL_STATE_DOT_VIEWS ="CarouselViewHolder.SUB_CAROUSEL_STATE_DOT_VIEWS";

    public static final int CAROUSEL_MSG_SCROLLING =1;

    private Bundle mData;

    private Context mContext;
    private ViewPager mViewPager;
    private TextView tvCarouselTitle;
    private LinearLayout llCarouselDotContainer;
    private ArrayList<MovieInfo> mMovieInfoList;
    private ArrayList<String> videoUrl;
    //fragment会在destroyView时只会销毁view，所有list<>内的会保存
    private ArrayList<SimpleDraweeView> mImageViewList=new ArrayList<>();
    private ArrayList<View> dotViews=new ArrayList<>();
    private CarouselPagerAdapter mAdapter;
    //正在显示的图片
    private int previousImage=0;
    //确认是否继续滚动,destroyView时设置为false，即可结束滚动
    //createView时进行判断，如果为true说明是第一次初始化，如果为false说明是第二次初始化
    private boolean isScrolling=true;


    public CarouselViewHolder(View itemView,Context context) {
        super(itemView,context);
        mContext=context;
        initView(itemView);
    }

    @Override
    protected void initView(View itemView) {
        int height= (int) (Constant.WIDTH*0.6F);
        FrameLayout.LayoutParams params=new FrameLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);

        mViewPager = (ViewPager) itemView.findViewById(R.id.vpCarouselImage);
        mViewPager.setLayoutParams(params);

        tvCarouselTitle= (TextView) itemView.findViewById(R.id.tvCarouselTitle);
        llCarouselDotContainer= (LinearLayout) itemView.findViewById(R.id.llCarouselDotContainer);

        fillView();
    }

    //初始化数据
    @Override
    public void initData(ItemFeed itemFeed){
        if (mData!=null||itemFeed==null) return;
        mData=itemFeed.getData();
        mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(SUB_CAROUSEL_DATA);

        fillView();
    }

    //将数据填充视图
    @Override
    protected void fillView(){
        if (mMovieInfoList!=null&&mMovieInfoList.size()>0){

            if (llCarouselDotContainer.getChildCount()==0){
                dotViews=new ArrayList<>();
                for (int i=0;i<mMovieInfoList.size();i++){
                    MovieInfo movie=mMovieInfoList.get(i);
                    if (movie!=null){
                        View dot=new View(mContext);
                        dot.setBackgroundResource(R.drawable.backgroun_dot_selector);
                        LinearLayout.LayoutParams params=new LinearLayout
                                .LayoutParams((int) (4* Constant.dp)
                                , (int) (4* Constant.dp));
                        params.rightMargin= (int) (8* Constant.dp);
                        params.topMargin = (int) (8* Constant.dp);
                        dot.setEnabled(false);
                        dot.setLayoutParams(params);
                        int id=Integer.parseInt(movie.getPostId()+"");
                        dot.setId(id*2);
                        dotViews.add(dot);

                        llCarouselDotContainer.addView(dot);
                    }
                }
            }

            if (mImageViewList.size()==0&&mAdapter==null){
                mImageViewList=new ArrayList<>();
                for (int i=0;i<mMovieInfoList.size();i++){
                    MovieInfo movie=mMovieInfoList.get(i);
                    SimpleDraweeView imageView=new SimpleDraweeView(mContext);
                    imageView.setTag(movie.getPosterUrl());
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    imageView.setOnClickListener(this);
                    int id=Integer.parseInt(movie.getPostId()+"");
                    imageView.setId(id * 2 + 1);

                    GenericDraweeHierarchyBuilder builder
                            =new GenericDraweeHierarchyBuilder(mContext.getResources());
                    GenericDraweeHierarchy hierarchy=builder
                            .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                            .build();
                    imageView.setHierarchy(hierarchy);
                    imageView.setImageURI(Uri.parse(movie.getPosterUrl()));

                    mImageViewList.add(imageView);
                }
            }
            if (mAdapter==null)
                mAdapter=new CarouselPagerAdapter(mContext,mImageViewList);

            initEvent();
            startCarouselImage();
        }

    }

    //初始化事件
    private void initEvent(){
        mViewPager.setAdapter(mAdapter);
        tvCarouselTitle.setText(mMovieInfoList.get(previousImage).getName());
        llCarouselDotContainer.getChildAt(previousImage).setEnabled(true);
        mViewPager.setCurrentItem(previousImage + mMovieInfoList.size() * 20, false);
        mViewPager.addOnPageChangeListener(new MyPageChangeListener());
    }


    //开启线程开始轮播图片
    private void startCarouselImage() {
        isScrolling=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isScrolling) {
                    try {
                        Thread.sleep(5000);

                        Message msg = new Message();
                        msg.what = CAROUSEL_MSG_SCROLLING;
                        mHandler.sendMessage(msg);
//                        mHandler.obtainMessage(CAROUSEL_MSG_SCROLLING).sendToTarget();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CAROUSEL_MSG_SCROLLING:
                    int nextImage = (mViewPager.getCurrentItem() + 1);
                    mViewPager.setCurrentItem(nextImage, true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onClick(View v) {
        String posterUrl= (String) v.getTag();
        if (posterUrl!=null) {
            for (MovieInfo movie:mMovieInfoList){
                if (movie.getPosterUrl().equals(posterUrl)){
                    Intent intent=new Intent(mContext, DetailActivity.class);
                    intent.putExtra(DetailActivity.INTENT_DETAIL,movie);
                    mContext.startActivity(intent);
                }
            }
        }
    }

    class MyPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int newPosition=position%mMovieInfoList.size();

            tvCarouselTitle.setText(mMovieInfoList.get(newPosition).getName());
            llCarouselDotContainer.getChildAt(previousImage).setEnabled(false);
            llCarouselDotContainer.getChildAt(newPosition).setEnabled(true);
            previousImage =newPosition;

        }

        @Override
        public void onPageScrollStateChanged(int state) {

            switch (state){
                case ViewPager.SCROLL_STATE_IDLE:
                    break;
            }

        }
    }

}
