package com.coolcool.moviecool.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.base.BaseActivity;
import com.coolcool.moviecool.common.Constant;
import com.coolcool.moviecool.model.MovieInfo;
import com.coolcool.moviecool.model.OrdinaryUser;
import com.coolcool.moviecool.utils.TextViewUtils;
import com.coolcool.moviecool.utils.TintDrawableUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class DetailActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG="DetailActivity";
    //用于从intent中获取当前页面所展示的电影信息对象的标志
    public static final String INTENT_DETAIL ="com.coolcool.moviecool.activity.DetailActivity";

    public static final String XUNLEI_URL="XUNLEI_URL";
    public static final String XUNLEI_TEXT="XUNLEI_TEXT";
    public static final String BAIDU_URL="BAIDU_URL";
    public static final String BAIDU_TEXT="BAIDU_TEXT";

    //电影海报海报的视图
    private SimpleDraweeView imageView;
    //电影基本信息
    private TextView tvContent;
    //当前页面显示的电影
    private MovieInfo movieInfo;
    //可滚动的视图scrollView用于将视图自动滚至顶部
    private ScrollView scrollView;
    //容纳电影基本信息的布局，用于动态添加电影链接地址的容器
    private LinearLayout linear;
    //用于展示收藏状态额星状图标
    private ImageView favoriteImage;
    //用于提示用户返回的箭头图标
    private ImageView ivArrowMark;
    //电影的名字，用于提示用户所在的的页面内容
    private TextView tvMovieName;
    //收藏的提示语
    private TextView tvFavoriteLabel;
    //当前图标颜色状态
    boolean currFavorite=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //获取页面的电影信息，以供后续操作
        Intent intent=getIntent();
        movieInfo= (MovieInfo) intent.getSerializableExtra(INTENT_DETAIL);

        //初始化视图
        initView();

        //填充视图
        fillView();
        //默认将收藏图标的设为未收藏的颜色
        changeFavoriteImage(false);
        //确认当前电影是否已收藏并同步收藏图标的颜色，
        syncImage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //确认当前电影是否已收藏并同步收藏图标的颜色，
        syncImage();
    }

    //初始化视图
    private void initView(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvContent= (TextView) findViewById(R.id.tvContent);
        imageView = (SimpleDraweeView) findViewById(R.id.ivMovie);
        tvFavoriteLabel= (TextView) findViewById(R.id.tvFavoriteLabel);
        linear= (LinearLayout) tvContent.getParent();
        scrollView= (ScrollView) findViewById(R.id.detailScrollView);
        favoriteImage= (ImageView) findViewById(R.id.favoriteImage);
        tvMovieName= (TextView) findViewById(R.id.tvMovieName);
        ivArrowMark= (ImageView) findViewById(R.id.ivArrowMark);

        if (ivArrowMark!=null){
            ivArrowMark.setOnClickListener(this);
            Drawable drawable=TintDrawableUtil
                    .tintDrawable(this,R.drawable.ic_arrow_back_24dp,R.color.colorWhite);
            ivArrowMark.setImageDrawable(drawable);
        }

        if (imageView!=null){
            imageView.setOnClickListener(this);
        }
    }

    //填充视图
    private void fillView(){
        if (movieInfo==null||movieInfo.getPostId()==null) {
            Toast.makeText(this,"暂无详细信息",Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
        tvMovieName.setText(movieInfo.getName());
        final String url=movieInfo.getPosterUrl();
        tvContent.setText(movieInfo.toString());
        LinearLayout.LayoutParams params=new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        int margins= (int) (8* Constant.dp);
        params.setMargins(margins, 0, margins, 0);

        if (url!=null){
            imageView.setAspectRatio(0.7f);
            imageView.setImageURI(Uri.parse(url));

//            ImageRequest imageRequest = ImageRequest.fromUri(Uri.parse(url));
//            ImagePipeline imagePipeline = Fresco.getImagePipeline();
//            DataSource<CloseableReference<CloseableImage>> dataSource =
//                    imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
//            dataSource.subscribe(new BaseBitmapDataSubscriber() {
//                @Override
//                public void onNewResultImpl(@Nullable Bitmap bitmap) {
//                    if (bitmap != null) {
//                        Log.i(TAG, "转换前 图片大小==   " + bitmap.getByteCount());
//                        imageView.setAspectRatio((float)(bitmap.getWidth())/(float)(bitmap.getHeight()));
//                        imageView.setImageURI(Uri.parse(url));
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onFailureImpl(DataSource dataSource) {
//
//                }
//            }, CallerThreadExecutor.getInstance());
        }

        ArrayList<HashMap<String,String>> listUrls;
        listUrls=movieInfo.getDownloadUrls();
        if (listUrls!=null)
        for (int i=0;i<listUrls.size();i++){
            TextView textView=new TextView(this);
            TextView passWordView=new TextView(this);
            textView.setLayoutParams(params);
            passWordView.setLayoutParams(params);
            HashMap<String,String> map=listUrls.get(i);
            SpannableString sp=null;
            if (map.get(XUNLEI_URL)!=null){
                sp= TextViewUtils.getSPText(this, map.get(XUNLEI_URL)
                        , map.get(XUNLEI_TEXT));
                if (sp!=null)
                    textView.setTag(TextViewUtils.URL_INFO_HEADER);
            }else if (map.get(BAIDU_URL)!=null){
                if (map.get(BAIDU_TEXT)!=null){
                    sp= TextViewUtils.getSPText(this, map.get(BAIDU_URL)
                            , map.get(BAIDU_URL)
                            , map.get(BAIDU_TEXT), true);
                }else {
                    sp= TextViewUtils.getSPText(this, map.get(BAIDU_URL)
                            , map.get(BAIDU_URL), null, true);
                }
            }
            if (sp!=null){
                textView.setText(sp);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                textView.setFocusable(false);
                linear.addView(textView);

                if (map.get(BAIDU_TEXT)!=null){
                    String pass=map.get(BAIDU_TEXT);
                    SpannableString s=new SpannableString(pass);
                    s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081"))
                            ,0
                            ,pass.length()
                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    s.setSpan(new RelativeSizeSpan(1.3f)
                            , 0
                            , pass.length()
                            , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    passWordView.setText(s);
                    passWordView.setTextIsSelectable(true);
                    passWordView.setOnClickListener(this);
                    passWordView.setTag(TextViewUtils.BAIDU_URL_KEY);
                    passWordView.setFocusable(false);
                    linear.addView(passWordView);
                }
            }
        }

        View bottomView=new View(this);
        params=new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , (int) (16* Constant.dp));
        bottomView.setLayoutParams(params);
        linear.addView(bottomView,-1);

        //由于动态添加子view及加载图片的时间，每次初始化scrollView不是滚动到最顶部
        //开启线程监听500毫秒内scrollView是否滚动到最顶部
        //如果不在最顶部，就每隔50毫秒滚动到最顶部
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long elapseTime=0;
                    while (elapseTime<300){
                        if (scrollView.getScrollY()!=0){
                            scrollView.scrollTo(0, 0);
                            Thread.sleep(50);
                            elapseTime+=100;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //改变收藏图标的颜色
    private void changeFavoriteImage(boolean favorite) {
        if (favoriteImage==null||tvFavoriteLabel==null) return;
        currFavorite=favorite;

        int color=favorite?R.color.colorYellow :R.color.colorWhite;
        Drawable drawable= TintDrawableUtil.tintDrawable(this, R.drawable.ic_star_24dp, color);
        favoriteImage.setImageDrawable(drawable);
        favoriteImage.invalidate();

        tvFavoriteLabel.setTextColor(favorite ? Color.YELLOW : Color.WHITE);
        tvFavoriteLabel.setText(favorite?"已收藏":"未收藏");

    }

    //确认当前电影是否已收藏并同步收藏图标的颜色，
    private void syncImage(){
        OrdinaryUser user= Constant.ordinaryUser;
        if(Constant.ONLINE_STATE&&user!=null&&user.getFavoriteIdList()!=null){
            List<Long> list=user.getFavoriteIdList();
            if (list.size()>0){
                for (Long movie:list){
                    if (movie!=null&&movie.equals(movieInfo.getPostId())) {
                        changeFavoriteImage(true);
                        return;
                    }
                }
            }
        }
        changeFavoriteImage(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivMovie:
                checkIsFavorite();
                break;
            case R.id.ivArrowMark:
                super.onBackPressed();
                break;
        }
        if (v.getTag()!=null){
            switch ((String)v.getTag()){
                case TextViewUtils.BAIDU_URL_KEY:
                    TextView passWordView=(TextView)v;
                    ClipboardManager cm= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText(passWordView.getText(),passWordView.getText()));
                    Toast.makeText(this,"百度网盘密码：\n"+passWordView.getText()+"\n已复制到剪贴板"
                            ,Toast.LENGTH_SHORT).show();
                    break;
//            case TextViewUtils.URL_INFO_HEADER:
//                TextView downloadView=(TextView)v;
//                ClipboardManager cmUrl= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//                cmUrl.setPrimaryClip(ClipData.newPlainText(downloadView.getText()
//                        ,downloadView.getText()));
//                Toast.makeText(this,"下载链接："+downloadView.getText()+"已复制到剪贴板"
//                        ,Toast.LENGTH_SHORT).show();
//                break;
            }
        }
    }

    //处理用户收藏图标的点击事件
    private void checkIsFavorite(){
        OrdinaryUser user= Constant.ordinaryUser;
        if(!Constant.ONLINE_STATE||user==null){
            changeFavoriteImage(false);
            Toast.makeText(this,"您还没有登陆，请先登录再收藏",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }else {
            favoriteList=user.getFavoriteIdList();
            checkFavorite();
        }
    }

    List<Long> favoriteList;
    //添加收藏
    private void addFavorite(){
        BmobRelation relation=new BmobRelation();
        relation.add(movieInfo);
        Constant.ordinaryUser.setFavorites(relation);
        Constant.ordinaryUser.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(DetailActivity.this
                        , "收藏成功", Toast.LENGTH_SHORT).show();
                changeFavoriteImage(true);
                updateMovieInfo(true);
                if (favoriteList==null) favoriteList=new ArrayList<>();
                favoriteList.add(movieInfo.getPostId());
                updateUserFavorites(favoriteList);
                Log.i(TAG, "成功添加至收藏");
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(DetailActivity.this
                        , "网络原因收藏失败，建议重新收藏", Toast.LENGTH_SHORT).show();
                changeFavoriteImage(false);
            }
        });
    }

    //取消收藏
    private void removeFavorite(){
        BmobRelation relation=new BmobRelation();
        relation.remove(movieInfo);
        Constant.ordinaryUser.setFavorites(relation);
        Constant.ordinaryUser.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(DetailActivity.this
                        , "已成功从收藏中删除", Toast.LENGTH_SHORT).show();
                changeFavoriteImage(false);
                updateMovieInfo(false);
                if (favoriteList!=null&&favoriteList.size()>0){
                    try{
                        boolean s=favoriteList.remove(movieInfo.getPostId());
                        if (s) updateUserFavorites(favoriteList);
                    }catch (UnsupportedOperationException e){
                        Log.i(TAG,"要删除的电影id不在此集合中  "+e);
                    }
                }
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(DetailActivity.this
                        , "网络原因取消失败，建议重新取消", Toast.LENGTH_SHORT).show();
                changeFavoriteImage(true);
            }
        });

    }

    private void updateMovieInfo(boolean increase){
        long count=0;
        if (movieInfo.getFavoriteCount()!=null){
            count=movieInfo.getFavoriteCount();
        }
        movieInfo.setFavoriteCount(increase?count+1:count-1);
        movieInfo.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"成功更收藏次数数据");
            }

            @Override
            public void onFailure(int i, String s) {
                Log.i(TAG,"更新更收藏次数数据失败");
            }
        });
    }

    //查询收藏
    private void checkFavorite(){
        changeFavoriteImage(!currFavorite);
        BmobQuery<MovieInfo> query=new BmobQuery<>();
        query.addWhereRelatedTo("favorites",new BmobPointer(Constant.ordinaryUser));
        query.findObjects(this, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                Log.i(TAG,"当前用户喜欢的电影个数== "+list.size());
                if (list.size()>0){
                    boolean isFavorite=false;
                    for (MovieInfo movie:list){
                        if (movie!=null&&movie.getPostId().equals(movieInfo.getPostId())){
                            isFavorite=true;
                            break;
                        }
                    }
                    if (isFavorite){
                        removeFavorite();
                    }else {
                        addFavorite();
                    }

                }else {
                    addFavorite();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    //更新用户的收藏列表
    private void updateUserFavorites(@NonNull List<Long> idList){
        Constant.ordinaryUser.setFavoriteIdList(idList);
        Constant.ordinaryUser.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
//                Toast.makeText(DetailActivity.this, "同步成功", Toast.LENGTH_SHORT).show();
                Log.i(TAG,"同步成功");
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(DetailActivity.this, "同步失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
