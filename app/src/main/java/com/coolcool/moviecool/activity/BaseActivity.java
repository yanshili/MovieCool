package com.coolcool.moviecool.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.utils.PasswordUtils;
import com.coolcool.moviecool.utils.TintDrawableUtil;
import com.coolcool.moviecool.fragment.MainAccountFragment;
import com.coolcool.moviecool.fragment.MainNavigationFragment;
import com.coolcool.moviecool.fragment.MainVideoFragment;
import com.coolcool.moviecool.model.OrdinaryUser;

import java.lang.ref.WeakReference;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class BaseActivity extends AppCompatActivity
        implements View.OnClickListener{
    public static final String TAG="BaseActivity";

    //用于获取destroy前显示哪个fragment的状态key
    public static final String ACTIVITY_STATE_MAIN_FRAGMENT=
            "com.coolcool.moviecool.ACTIVITY_STATE_MAIN_FRAGMENT";
    //MainVideoFragment的标志
    public static final String FRAGMENT_VIDEO ="com.coolcool.moviecool.FRAGMENT_VIDEO";
    //MainNavigationFragment的标志
    public static final String FRAGMENT_NAVIGATION="com.coolcool.moviecool.FRAGMENT_NAVIGATION";
    //MainAccountFragment的标志
    public static final String FRAGMENT_ACCOUNT="com.coolcool.moviecool.FRAGMENT_ACCOUNT";

//    private TextView tvPerfect;
//    private TextView tvAccount;
//    private TextView tvNavigation;
    private ImageView imageAccount;
    private TextView tvAccount;
    private MainVideoFragment mPerfectFragment;
    private MainNavigationFragment mNavigationFragment;
    private MainAccountFragment mAccountFragment;
    private FragmentManager mFragmentManager;
    //用于指示当前显示的fragment的标志
    private String eventTag =FRAGMENT_VIDEO;
    //当前显示的fragment
    private Fragment showFragment;

    //处理消息队列的内部静态类对象
    private MyHandler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (savedInstanceState!=null){
            eventTag=savedInstanceState.getString(ACTIVITY_STATE_MAIN_FRAGMENT);
            if (eventTag==null)
                eventTag =FRAGMENT_VIDEO;
        }
        //初始化视图
        initView();
        //初始化fragment
        initFragment(eventTag);
        //登陆账户
        initAccount();
        //处理消息
        handleMessage();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateAccountState(Constant.ONLINE_STATE);
    }

    //处理消息
    private void handleMessage(){
        mHandler=new MyHandler(this);
    }

    //初始化视图
    private void initView() {
        LinearLayout linearAccount= (LinearLayout) findViewById(R.id.linearAccount);
        tvAccount= (TextView) findViewById(R.id.tvAccount);
        imageAccount= (ImageView) findViewById(R.id.imageAccount);
        CardView searchView= (CardView) findViewById(R.id.searchCardView);
        if (searchView!=null)
            searchView.setOnClickListener(this);
        if (linearAccount!=null){
            linearAccount.setOnClickListener(this);
        }


        mFragmentManager=getSupportFragmentManager();

        mPerfectFragment=new MainVideoFragment();
        mNavigationFragment=new MainNavigationFragment();
        mAccountFragment=new MainAccountFragment();

    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.tvPerfect:
//                eventTag=FRAGMENT_VIDEO;
//                break;
//            case R.id.tvNavigation:
//                eventTag=FRAGMENT_NAVIGATION;
//                break;
//            case R.id.tvAccount:
//                eventTag=FRAGMENT_ACCOUNT;
//                break;
//        }
//        if (eventTag!=null)
//            initFragment(eventTag);

        if (v.getId()==R.id.searchCardView){
            Intent intent=new Intent(this,SearchActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.linearAccount) {
            eventTag=FRAGMENT_ACCOUNT;
            initFragment(FRAGMENT_ACCOUNT);
        }

    }

    /**
     * 根据fragment标签tag来显示相应的视图即事件
     * @param showFragmentTag   当前显示的fragment的标志
     */
    private void initFragment(String showFragmentTag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();

        if (showFragment!=null){
            transaction.hide(showFragment);
//            changeBottomView(showFragment.getTag(),false);
        }

        showFragment=mFragmentManager.findFragmentByTag(showFragmentTag);
        if (showFragment==null){
            Log.i(TAG, "showFragment为空");
            switch (showFragmentTag){
                case FRAGMENT_VIDEO:
                    showFragment=mPerfectFragment;
                    eventTag=FRAGMENT_VIDEO;
                    break;
                case FRAGMENT_NAVIGATION:
                    showFragment=mNavigationFragment;
                    eventTag=FRAGMENT_NAVIGATION;
                    break;
                case FRAGMENT_ACCOUNT:
                    showFragment=mAccountFragment;
                    eventTag=FRAGMENT_ACCOUNT;
                    break;
            }
            transaction.add(R.id.mainFragmentContainer, showFragment, showFragmentTag);
            showFragment.setUserVisibleHint(false);
        }

        if (showFragment!=null&&showFragment.isAdded()) {
            transaction.show(showFragment);
            showFragment.setUserVisibleHint(true);
        }
//        changeBottomView(showFragmentTag, true);
        transaction.commit();
    }

//    /**
//     * 根据fragment的tag标签，改变相应的底部视图外观
//     * @param previousFragmentTag   fragment的tag标签
//     * @param shown                 true当前标签对应的fragment为显示状态
//     *                              false当前标签对应的fragment为隐藏状态
//     */
//    private void changeBottomView(String previousFragmentTag,boolean shown){
//        int selectedColor=getResources().getColor(R.color.colorAccent);
//        int unselectedColor=getResources().getColor(R.color.colorPrimaryDark);
//        int textColor=shown?selectedColor:unselectedColor;
//        switch (previousFragmentTag){
//            case FRAGMENT_VIDEO:
//                tvPerfect.setTextColor(textColor);
//                break;
//            case FRAGMENT_NAVIGATION:
//                tvNavigation.setTextColor(textColor);
//                break;
//            case FRAGMENT_ACCOUNT:
//                tvAccount.setTextColor(textColor);
//                break;
//        }
//    }

    @Override
    public void onBackPressed() {
        if (exitApp()){
            finish();
            super.onBackPressed();
        }
    }

    //用户在2秒内双击即可退出应用
    long previousTime=2000;
    //用于提示用户双击退出
    private boolean exitApp(){
        if (eventTag.equals(FRAGMENT_VIDEO)){
            long elapseTime=System.currentTimeMillis()-previousTime;
            if (elapseTime<2000){
                return true;
            }else {
                Toast.makeText(this, "再次点击退出", Toast.LENGTH_SHORT).show();
                previousTime=System.currentTimeMillis();
                return false;
            }
        }else {
            eventTag=FRAGMENT_VIDEO;
            initFragment(FRAGMENT_VIDEO);
            return false;
        }
    }

    //登陆账户
    private void initAccount(){
        SharedPreferences preferences= getSharedPreferences(Constant.USER_ACCOUNT_FILE,MODE_PRIVATE);
        boolean isRemember=preferences.getBoolean(Constant.USER_PASSWORD_SAVED_STATE,false);
        if (isRemember){
            String account=preferences.getString(Constant.USER_ACCOUNT,"");
            String p=preferences.getString(Constant.USER_PASSWORD,"");
            long savedTime=preferences.getLong(Constant.USER_PASSWORD_SAVED_TIME, 0);
            String password= PasswordUtils.decodePassword(p, savedTime);
            long currentTime=System.currentTimeMillis();
            //用户保存的秘密期限为30天
            if (currentTime-(long)30*24*60*60*1000>savedTime){
                //密码已过期，让用户重新输入密码

                Constant.ONLINE_STATE=false;
                updateAccountState(false);
                Log.i(TAG,"保存的密码已过期");
            }else {

                OrdinaryUser bu=new OrdinaryUser(this);
                bu.setUsername(account);
                bu.setPassword(password);
                bu.login(this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        Constant.ONLINE_STATE=true;
                        OrdinaryUser user= BmobUser
                                .getCurrentUser(BaseActivity.this,OrdinaryUser.class);
                        if (user!=null) Constant.ordinaryUser=user;

                        updateAccountState(true);
                        Log.i(TAG,"登陆成功    "+Constant.ONLINE_STATE);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Constant.ONLINE_STATE=false;
                        updateAccountState(false);
                        Log.i(TAG,"登陆失败");
                    }
                });
            }
        }else {
            Constant.ONLINE_STATE=false;
            updateAccountState(false);
            Log.i(TAG,"没有保存的密码文件");
        }
    }

    //更新账户头像的颜色
    public void updateAccountState(boolean online){
        int color=online?R.color.colorAccent:R.color.grayWhite;
        Drawable drawable= TintDrawableUtil
                .tintDrawable(this, R.drawable.ic_account_identity_24dp, color);
        imageAccount.setImageDrawable(drawable);
        imageAccount.invalidate();

        if (Constant.ordinaryUser!=null){
            tvAccount.setText(online?Constant.ordinaryUser.getUsername():"账号");
        }else {
            tvAccount.setText("账号");
        }

    }

    //处理消息的内部静态类
    static class MyHandler extends Handler{
        private final WeakReference<BaseActivity> mActivity;

        public MyHandler(BaseActivity activity) {
            this.mActivity = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BaseActivity activity=mActivity.get();
            if (activity==null){
                super.handleMessage(msg);
                return;
            }
            switch (msg.what){
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

}
