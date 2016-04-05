package com.coolcool.moviecool.test;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.activity.DetailActivity;
import com.coolcool.moviecool.R;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.custom.HeaderView;
import com.coolcool.moviecool.model.MovieInfo;

import cn.bmob.v3.Bmob;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubAllVideosFragment extends Fragment implements View.OnClickListener{

    //每秒中滑过item的个数
    public static final int ITEM_PER_SECOND=10;
    //服务器application id
    public static final String APPLICATION_ID="55c1f4bed3785eaabd622b1257ae0645";

    //可见项的最后一项为已下载数据的最后一项的时候为true
    private boolean lastFlag =false;
    private int page=1;
    private ListView lvMovieList;
    private ImageView ivPoster;
    private TextView tvMovieTitle;
    private TextView tvMovieDescription;
    private TextView criteriaAllAreas;
    private BmobAdapter adapter;
    private DataCacheBase mDataCacheBase;
    private ViewAnimator headerFloatAnimator;
    private LinearLayout criteriaContainer;
    private TextView tvSelect;
    private Context mContext;
    private View headerView;
    private ViewAnimator headerListAnimator;

    public SubAllVideosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getParentFragment().getContext();
        Bmob.initialize(mContext, APPLICATION_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_sub_all_videos, container, false);
        mDataCacheBase = DataCacheBase.getInstance(mContext);

        initView(view, inflater);


        return view;
    }

    private void initView(View view,LayoutInflater inflater){

        lvMovieList= (ListView) view.findViewById(R.id.lvMovieList);
        ivPoster= (ImageView) view.findViewById(R.id.ivPoster);
        tvMovieDescription= (TextView) view.findViewById(R.id.tvMovieDescription);
        tvMovieTitle= (TextView) view.findViewById(R.id.tvMovieTitle);
        tvSelect = (TextView) view.findViewById(R.id.selectCategory);


        //悬浮布局和listView头布局都用的共用视图，用来显示筛选条件的视图
        headerView =new HeaderView(mContext);

        //listView的头布局，用来存放headerView视图的
        headerListAnimator = (ViewAnimator) inflater.inflate(
                R.layout.list_header_view, lvMovieList, false);

        //将headerView添加到listView的头布局中
        headerListAnimator.addView(headerView);
        headerListAnimator.setDisplayedChild(1);

        //将listView的头布局添加到列表的头部
        lvMovieList.addHeaderView(headerListAnimator, null, false);

        //悬浮布局，用来填充headerView和textView的筛选条件视图
        headerFloatAnimator = (ViewAnimator) view.findViewById(R.id.headerFloatAnimator);

        adapter= BmobAdapter.getInstance(lvMovieList);
        String textCategory= Constant.selectCategory[0]+"·"
                + Constant.selectCategory[1]+"·"+ Constant.selectCategory[2];
        tvSelect.setText(textCategory);

        lvMovieList.setAdapter(adapter);

        lvMovieList.setOnItemClickListener(mOnItemClickListener);

        lvMovieList.setOnTouchListener(mOnTouchListener);

        lvMovieList.setOnScrollListener(mOnScrollListener);
        //每次刷新后都会跳到最后一行
//        lvMovieList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);

        tvSelect.setOnClickListener(this);

        //为headerView内的textView设置监听事件
        for (int i=0;i< Constant.array.size();i++){
            LinearLayout linear= (LinearLayout) headerView
                    .findViewWithTag(Constant.array.get(i)[0] + HeaderView.CRITERIA_SCROLL);

            for (int j=0;j< Constant.array.get(i).length;j++){
                String tag= Constant.array.get(i)[j]+j;
                TextView textView= (TextView) linear.findViewWithTag(tag);
                textView.setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.selectCategory){
            //判断headerView的父布局如果为空，就将其添加到悬浮布局中并显示出来；
            //如果父布局为listView的头布局，就将其移除；然后添加到悬浮布局中，并显示出来。
            if (headerView.getParent()!=null){
                if (headerView.getParent().equals(headerListAnimator)){
                    headerListAnimator.setDisplayedChild(0);
                    headerListAnimator.removeView(headerView);
                    headerFloatAnimator.addView(headerView);
                }
            }else {
                headerFloatAnimator.addView(headerView);
            }
            headerFloatAnimator.setDisplayedChild(1);
        }else {
            for (int i=0;i< Constant.array.size();i++){
                LinearLayout linear= (LinearLayout) headerView
                        .findViewWithTag(Constant.array.get(i)[0] + HeaderView.CRITERIA_SCROLL);
                for (int j=0;j< Constant.array.get(i).length;j++){
                    String tag= Constant.array.get(i)[j]+j;
                    TextView textView= (TextView) linear.findViewWithTag(tag);
                    if (v.getTag().equals(tag)){
                        //根据当前点击的位置建立筛选条件
                        //所以筛选条件也应该制作成数组
                        for (int n=0;n< Constant.array.get(i).length;n++){
                            String text= Constant.array.get(i)[n]+n;
                            TextView view= (TextView) linear.findViewWithTag(text);
                            //蓝色
                            view.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                        //红色
                        textView.setTextColor(getResources().getColor(R.color.colorAccent));
                        Constant.selectCategory[i]= Constant.criteria[i][j];
                        loadNewCategory(i,j);
                    }
                }
            }
        }

    }

    //第i列j行的筛选条件，并调用adapter加载数据
    public void loadNewCategory(int i,int j){
        adapter.addCriteria(i,j);
        page=1;
        adapter.selectContent(page);
        String textCategory= Constant.selectCategory[0]+"·"
                + Constant.selectCategory[1]+"·"+ Constant.selectCategory[2];
        tvSelect.setText(textCategory);
    }

    AbsListView.OnScrollListener mOnScrollListener=new AbsListView.OnScrollListener() {
        long previousTime=0L;
        int previousVisibleItem=0;
        double items;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState){
                case SCROLL_STATE_IDLE:
                    adapter.setLoadingFlag(true);

                    adapter.startLoadBitmap();
                    //当前可见最后一行为数列的最后一项时，下载下一页数据
                    if (lastFlag){
                        lastFlag=false;
                        page=page+1;
                        adapter.loadPage(page);
                    }
                    break ;
                case SCROLL_STATE_TOUCH_SCROLL:

                    break;
                case SCROLL_STATE_FLING:

                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem
                , int visibleItemCount, int totalItemCount) {
            //判断如果第一个可见视图大于0时就显示悬浮布局，
            // 并且如果headerView视图在悬浮布局内，就将其显示
            //如果不在就显示textView视图，
            // 并且判断如果headerView在listView的头布局中，就将其移除
            //如果第一个可见视图为0时就将悬浮布局隐藏，
            // 并且将headerView视图移除，然后添加到listView的header布局内
            if (firstVisibleItem>0){
                headerFloatAnimator.setVisibility(View.VISIBLE);
                headerFloatAnimator.setDisplayedChild(0);
                if (headerView.getParent()!=null){
                    if (headerView.getParent().equals(headerListAnimator)){
                        headerListAnimator.setDisplayedChild(0);
                        headerListAnimator.removeView(headerView);
                    }else if (headerView.getParent().equals(headerFloatAnimator)){
                        headerFloatAnimator.setDisplayedChild(1);
                    }
                }
            }else {
                headerFloatAnimator.setVisibility(View.GONE);
                if (headerView.getParent()!=null){
                    if (headerView.getParent().equals(headerFloatAnimator)){
                        headerFloatAnimator.setDisplayedChild(0);
                        headerFloatAnimator.removeView(headerView);
                        headerListAnimator.addView(headerView);
                    }
                }else {
                    headerListAnimator.addView(headerView);
                }

                headerListAnimator.setDisplayedChild(1);

            }
            int lastVisibleItem=firstVisibleItem+visibleItemCount-1;
            //判断当前可见项是否为最后一项
            if (lastVisibleItem==totalItemCount-1){
                lastFlag =true;
            }else {
                lastFlag =false;
            }
            //已经到最后一项数据，没有数据了
            if (lastVisibleItem>=adapter.getItemCount()) {
                lastFlag =false;
            }
            if (previousVisibleItem!=firstVisibleItem){
                long currentTime=System.currentTimeMillis();
                //滑过一个item所经历的时间
                long elapseTime=currentTime-previousTime;
                //一秒钟所滑过的item个数items
                if (elapseTime!=0)
                    items =(double)(1000/elapseTime);
                if (elapseTime!=0&&items<ITEM_PER_SECOND){
                    //速度慢时，开始加载
                    adapter.setLoadingFlag(true);
                    if (lastFlag){
                        lastFlag=false;
                        //加载下一页
                        page=page+1;
                        adapter.loadPage(page);
                    }
                }else {
                    //速度快时，应当停止加载
                    adapter.setLoadingFlag(false);
                }
                previousVisibleItem=firstVisibleItem;
                previousTime=currentTime;
            }

        }
    };

    View.OnTouchListener mOnTouchListener=new View.OnTouchListener() {
        float startX=0,startY=0,endX=0,endY=0,slideX=0,slideY=0;
        int originPosition=-1;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    startX=event.getX();
                    startY=event.getY();
                    originPosition=lvMovieList.getLastVisiblePosition();

                    //判断当前可见项是否为数列最后一项
                    //因为加了headerView所以不用减一,否则adapter.getCount()-1
                    if (adapter!=null){
                        if (adapter.getItemCount()>originPosition){
                            if (originPosition==adapter.getCount()){
                                //为最后一项
                                lastFlag =true;
                                //加载下一页
                                page=page+1;
                                adapter.loadPage(page);
                                //加载完后
                                lastFlag=false;
                            }
                        }else {
                            Toast.makeText(mContext, "已经是最后一项了", Toast.LENGTH_SHORT).show();
                        }
                    }


                    //当触摸屏幕，并且headerView的父布局为悬浮布局时，就移除headerView视图
                    if (headerView.getParent()!=null
                            &&headerView.getParent().equals(headerFloatAnimator)){
                        headerFloatAnimator.removeView(headerView);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    endX=event.getX();
                    endY=event.getY();
                    slideX=endX-startX;
                    slideY=endY-startY;

                    int touchPosition=-1;
                    if (slideX<-20&&Math.abs(slideX)>Math.abs(slideY)){
                        touchPosition=lvMovieList.pointToPosition((int) startX, (int) startY);
                        if (touchPosition>0){
                            adapter.deleteData(touchPosition-1);
                        }
                    }
                    break;
            }
            return false;
        }
    };

    AdapterView.OnItemClickListener mOnItemClickListener=new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position==0) return;
            Intent intent = new Intent(mContext, DetailActivity.class);
            MovieInfo movieInfo = (MovieInfo) lvMovieList.getItemAtPosition(position);
            intent.putExtra(DetailActivity.INTENT_DETAIL, movieInfo);
            startActivity(intent);
        }
    };

}
