package com.coolcool.moviecool.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.fragment.MainAccountFragment;
import com.coolcool.moviecool.fragment.MainNavigationFragment;
import com.coolcool.moviecool.fragment.MainVideoFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG="MainActivity";
    public static final String ACTIVITY_STATE_MAIN_FRAGMENT=
            "com.coolcool.moviecool.ACTIVITY_STATE_MAIN_FRAGMENT";
    public static final String FRAGMENT_VIDEO ="com.coolcool.moviecool.FRAGMENT_VIDEO";
    public static final String FRAGMENT_NAVIGATION="com.coolcool.moviecool.FRAGMENT_NAVIGATION";
    public static final String FRAGMENT_ACCOUNT="com.coolcool.moviecool.FRAGMENT_ACCOUNT";

    private String eventTag =FRAGMENT_VIDEO;
    private TextView tvPerfect;
    private TextView tvAccount;
    private TextView tvNavigation;
    private MainVideoFragment mPerfectFragment;
    private MainNavigationFragment mNavigationFragment;
    private MainAccountFragment mAccountFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((getIntent().getFlags()& Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT)!=0){
            finish();
            return;
        }
        Log.i(TAG, "onCreate");
        if (savedInstanceState!=null){
            eventTag=savedInstanceState.getString(ACTIVITY_STATE_MAIN_FRAGMENT);
            if (eventTag==null)
                eventTag =FRAGMENT_VIDEO;
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        initView();

        initEvent(eventTag);

    }

    private void initView() {
        tvAccount= (TextView) findViewById(R.id.tvAccount);
        tvAccount.setOnClickListener(this);
        tvPerfect= (TextView) findViewById(R.id.tvPerfect);
        tvPerfect.setOnClickListener(this);
        tvNavigation= (TextView) findViewById(R.id.tvNavigation);
        tvNavigation.setOnClickListener(this);

        mFragmentManager=getSupportFragmentManager();

        mPerfectFragment=new MainVideoFragment();
        mNavigationFragment=new MainNavigationFragment();
        mAccountFragment=new MainAccountFragment();

    }

    Fragment showFragment;
    /**
     * 根据fragment标签tag来显示相应的视图即事件
     * @param showFragmentTag
     */
    private void initEvent(String showFragmentTag) {
        mTransaction=mFragmentManager.beginTransaction();
        //得到前一fragment的tag标签，做出相应的反应
//        Fragment previousFragment=mFragmentManager.findFragmentById(R.id.mainFragmentContainer);
//        if (previousFragment!=null){
//            changeBottomView(previousFragment.getTag(),false);
//        }

        if (showFragment!=null){
            mTransaction.hide(showFragment);
            changeBottomView(showFragment.getTag(),false);
        }

        showFragment=mFragmentManager.findFragmentByTag(showFragmentTag);
        if (showFragment==null){
            Log.i(TAG, "showFragment为空");
            switch (showFragmentTag){
                case FRAGMENT_VIDEO:
//                    mPerfectFragment=new MainVideoFragment();
                    showFragment=mPerfectFragment;
                    break;
                case FRAGMENT_NAVIGATION:
//                    mNavigationFragment=new MainNavigationFragment();
                    showFragment=mNavigationFragment;
                    break;
                case FRAGMENT_ACCOUNT:
//                    mAccountFragment=new MainAccountFragment();
                    showFragment=mAccountFragment;
                    break;
            }
            mTransaction.add(R.id.mainFragmentContainer, showFragment, showFragmentTag);
            showFragment.setUserVisibleHint(false);
        }

        if (showFragment!=null&&showFragment.isAdded()) {
            mTransaction.show(showFragment);
            showFragment.setUserVisibleHint(true);
        }
        changeBottomView(showFragmentTag, true);
        mTransaction.commit();
    }

    /**
     * 根据fragment的tag标签，改变相应的底部视图外观
     * @param previousFragmentTag   fragment的tag标签
     * @param shown                 true当前标签对应的fragment为显示状态
     *                              false当前标签对应的fragment为隐藏状态
     */
    private void changeBottomView(String previousFragmentTag,boolean shown){
        int selectedColor=getResources().getColor(R.color.colorAccent);
        int unselectedColor=getResources().getColor(R.color.colorPrimaryDark);
        int textColor=shown?selectedColor:unselectedColor;
        switch (previousFragmentTag){
            case FRAGMENT_VIDEO:
                tvPerfect.setTextColor(textColor);
                break;
            case FRAGMENT_NAVIGATION:
                tvNavigation.setTextColor(textColor);
                break;
            case FRAGMENT_ACCOUNT:
                tvAccount.setTextColor(textColor);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvPerfect:
                eventTag=FRAGMENT_VIDEO;
                break;
            case R.id.tvNavigation:
                eventTag=FRAGMENT_NAVIGATION;
                break;
            case R.id.tvAccount:
                eventTag=FRAGMENT_ACCOUNT;
                break;
        }
        if (eventTag!=null)
            initEvent(eventTag);
    }

    @Override
    public void onBackPressed() {
        if (exitApp()){
            finish();
            super.onBackPressed();
        }
    }

    long previousTime=3000;
    private boolean exitApp(){
        long elapseTime=System.currentTimeMillis()-previousTime;
        if (elapseTime<2000){
            return true;
        }else {
            Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
            previousTime=System.currentTimeMillis();
            return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTIVITY_STATE_MAIN_FRAGMENT,eventTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        eventTag=savedInstanceState.getString(ACTIVITY_STATE_MAIN_FRAGMENT);

        initEvent(eventTag);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

}
