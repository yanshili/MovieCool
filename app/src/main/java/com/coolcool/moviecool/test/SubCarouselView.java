package com.coolcool.moviecool.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.CarouselPagerAdapter;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.core.AsyncNetUtils;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

/**
 * Created by yanshili on 2016/3/25.
 */
public class SubCarouselView extends FrameLayout implements View.OnClickListener{
    public static final String TAG="SubCarouselView";
    public static final String SUB_CAROUSEL_DATA ="SubCarouselView.SUB_CAROUSEL_DATA";
    public static final String SUB_CAROUSEL_STATE_DATA ="SubCarouselView.SUB_CAROUSEL_STATE_INDEX";
    public static final String SUB_CAROUSEL_STATE_INDEX ="SubCarouselView.SUB_CAROUSEL_STATE_INDEX";
    public static final String SUB_CAROUSEL_STATE_IMAGE_VIEWS ="SubCarouselView.SUB_CAROUSEL_STATE_IMAGE_VIEWS";
    public static final String SUB_CAROUSEL_STATE_DOT_VIEWS ="SubCarouselView.SUB_CAROUSEL_STATE_DOT_VIEWS";

    public static final int CAROUSEL_MSG_SCROLLING =1;

    private LayoutInflater mInflater;
    private Bundle mData;

    private Context mContext;
    private ViewPager mViewPager;
    private TextView tvCarouselTitle;
    private LinearLayout llCarouselDotContainer;
    private ArrayList<MovieInfo> mMovieInfoList;
    //fragment会在destroyView时只会销毁view，所有list<>内的会保存
    private ArrayList<SimpleDraweeView> mImageViewList=new ArrayList<>();
    private ArrayList<View> dotViews=new ArrayList<>();
    private CarouselPagerAdapter mAdapter;
    //正在显示的图片
    private int previousImage=0;
    //确认是否继续滚动,destroyView时设置为false，即可结束滚动
    //createView时进行判断，如果为true说明是第一次初始化，如果为false说明是第二次初始化
    private boolean isScrolling=true;


    public SubCarouselView(Context context) {
        super(context);
        mContext=context;
        initView();
    }

    public SubCarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        initView();
    }

    public SubCarouselView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        initView();
    }

    private void initView() {
        setEnabled(true);
        mInflater=LayoutInflater.from(mContext);
        View view=mInflater.inflate(getResources()
                .getLayout(R.layout.sub_item_carousel),this,false);
        addView(view);

        mViewPager = (ViewPager) view.findViewById(R.id.vpCarouselImage);
        tvCarouselTitle= (TextView) view.findViewById(R.id.tvCarouselTitle);
        llCarouselDotContainer= (LinearLayout) view.findViewById(R.id.llCarouselDotContainer);

        fillView();
    }

    //初始化数据
    public void initData(ItemFeed itemFeed){
        if (mData!=null||itemFeed==null) return;
        mData=itemFeed.getData();
        mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(SUB_CAROUSEL_DATA);

        fillView();
    }

    //将数据填充视图
    private void fillView(){
        if (mMovieInfoList!=null&&mMovieInfoList.size()>0){

            if (llCarouselDotContainer.getChildCount()==0){
                dotViews=new ArrayList<>();
                for (int i=0;i<mMovieInfoList.size();i++){
                    MovieInfo movie=mMovieInfoList.get(i);
                    View dot=new View(mContext);
                    dot.setBackgroundResource(R.drawable.backgroun_dot_selector);
                    LinearLayout.LayoutParams params=new LinearLayout
                            .LayoutParams(16,16);
                    params.rightMargin=32;
                    params.topMargin = 32;
                    dot.setEnabled(false);
                    dot.setLayoutParams(params);
                    int id=Integer.parseInt(movie.getPostId()+"");
                    dot.setId(id*2);
                    dotViews.add(dot);

                    llCarouselDotContainer.addView(dot);
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
                    imageView.setId(id*2+1);
                    mImageViewList.add(imageView);
                }
            }
            if (mAdapter==null)
            mAdapter=new CarouselPagerAdapter(mContext,mImageViewList);

            initEvent();

            downloadBitmap();

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

    //下载次数数
    int downloadCount =0;
    //下载图片
    private void downloadBitmap() {
        if (downloadCount >=mMovieInfoList.size())return;
        MovieInfo movie=mMovieInfoList.get(downloadCount);
        final String posterUrl=movie.getPosterUrl();
        ImageView imageView = mImageViewList.get(downloadCount);
        Bitmap bitmapM= DataCacheBase.getInstance(mContext).getBitmapFromMemoryCache(posterUrl);
        if (bitmapM!=null){
            imageView.setImageBitmap(bitmapM);
            return;
        }

        AsyncNetUtils.loadBitmap(posterUrl, new AsyncNetUtils.Callback() {
            @Override
            public void onResponse(Object response) {

                ImageView imageView = null;
                for (ImageView view : mImageViewList) {
                    if (view.getTag() != null && view.getTag().equals(posterUrl)) {
                        imageView = view;
                    }
                }

                Bitmap bitmap = (Bitmap) response;
                if (response != null && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
                downloadBitmap();

                downloadCount++;
            }
        }, mContext,imageView,TAG);
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

//    Bundle savedState;
//    @Override
//    protected Parcelable onSaveInstanceState() {
//        Log.i(SubCommonAdapter.TAG, "onSaveInstanceState");
////
////        savedState= (Bundle) super.onSaveInstanceState();
////        savedState.putBundle(SUB_CAROUSEL_STATE_DATA,mData);
////        savedState.putInt(SUB_CAROUSEL_STATE_INDEX, previousImage);
////        savedState.putSerializable(SUB_CAROUSEL_STATE_DOT_VIEWS, dotViews);
////        savedState.putSerializable(SUB_CAROUSEL_STATE_IMAGE_VIEWS,mImageViewList);
////
////        return savedState;
//        return super.onSaveInstanceState();
//    }


    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
    }

    //    @Override
//    protected void onRestoreInstanceState(Parcelable state) {
//        Log.i(SubCommonAdapter.TAG, "onRestoreInstanceState");
////        savedState= (Bundle) state;
//        super.onRestoreInstanceState(state);
////        mData=savedState.getBundle(SUB_CAROUSEL_STATE_DATA);
////        mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(SUB_CAROUSEL_DATA);
////        previousImage=savedState.getInt(SUB_CAROUSEL_STATE_INDEX);
////        dotViews= (ArrayList<View>) savedState.getSerializable(SUB_CAROUSEL_STATE_DOT_VIEWS);
////        mImageViewList= (ArrayList<ImageView>) savedState.getSerializable(SUB_CAROUSEL_STATE_IMAGE_VIEWS);
////
////        if (dotViews.size()>0){
////            for (View dot:dotViews){
////                ViewGroup parent= (ViewGroup) dot.getParent();
////                if (parent!=null) parent.removeView(dot);
////
////                llCarouselDotContainer.addView(dot);
////            }
////        }
////
////        if (mAdapter==null)
////            mAdapter=new CarouselPagerAdapter(mContext,mImageViewList);
////
////        initEvent();
////
////        startCarouselImage();
//    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isScrolling=false;
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