package com.coolcool.moviecool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.CrawlAdapter;
import com.coolcool.moviecool.api.DataCacheBase;
import com.coolcool.moviecool.model.MovieInfo;

/**
 * Created by yanshili on 2016/3/7.
 */
public class CrawlActivity extends AppCompatActivity {
    public static final String TAG="CrawlActivity";
    public static final String CRITERIA_SCROLL="CrawlActivity.CRITERIA_SCROLL";
    public static final String CRITERIA_AREA="CrawlActivity.CRITERIA_AREA";
    public static final String CRITERIA_GENRE="CrawlActivity.CRITERIA_GENRE";
    public static final String CRITERIA_RELEASE_YEAR="CrawlActivity.CRITERIA_RELEASE_YEAR";
    public static final String INTENT_MESSAGE_KEY ="com.coolcool.moviecool.activity.CrawlActivity";
    //每秒中滑过item的个数
    public static final int ITEM_PER_SECOND=10;

    private boolean lastFlag =false;
    private int page=1;
    private ListView lvMovieList;
    private ImageView ivPoster;
    private TextView tvMovieTitle;
    private TextView tvMovieDescription;
    private TextView criteriaAllAreas;
    private CrawlAdapter adapter;
    private DataCacheBase mDataCacheBase;
    private ViewPager headerContainerPager;
    private LinearLayout criteriaContainer;

    TextView tvPage;
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDataCacheBase = DataCacheBase.getInstance(getApplicationContext());

        initView();

        adapter= CrawlAdapter.getInstance(lvMovieList);
        lvMovieList.setAdapter(adapter);

        lvMovieList.setOnItemClickListener(mOnItemClickListener);

        lvMovieList.setOnTouchListener(mOnTouchListener);

        lvMovieList.setOnScrollListener(mOnScrollListener);
        //每次刷新后都会跳到最后一行
//        lvMovieList.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);

    }

    private void initView(){

        tvPage= (TextView) findViewById(R.id.textPage);
        lvMovieList= (ListView) findViewById(R.id.lvMovieList);
        ivPoster= (ImageView) findViewById(R.id.ivPoster);
        tvMovieDescription= (TextView) findViewById(R.id.tvMovieDescription);
        tvMovieTitle= (TextView) findViewById(R.id.tvMovieTitle);

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
                        tvPage.setText("第 "+page+" 页");
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

            //判断当前可见项是否为最后一项
            if (firstVisibleItem+visibleItemCount==totalItemCount){
                lastFlag =true;
            }else {
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
                        tvPage.setText("第 "+page+" 页");
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
                    if (adapter!=null&&originPosition==adapter.getCount()){
                        //为最后一项
                        lastFlag =true;
                        //加载下一页
                        page=page+1;
                        tvPage.setText("第 "+page+" 页");
                        adapter.loadPage(page);
                        //加载完后
                        lastFlag=false;
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
            Intent intent = new Intent(CrawlActivity.this, DetailActivity.class);

            MovieInfo movieInfo = (MovieInfo) lvMovieList.getItemAtPosition(position);
            String htmlUrl=movieInfo.getHtmlUrl();
            intent.putExtra(DetailActivity.INTENT_DETAIL, htmlUrl);
            startActivity(intent);
        }
    };
}
