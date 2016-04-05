package com.coolcool.moviecool.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.BaseActivity;
import com.coolcool.moviecool.activity.FavoriteActivity;
import com.coolcool.moviecool.activity.LoginActivity;
import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.utils.PasswordUtils;
import com.coolcool.moviecool.utils.TintDrawableUtil;
import com.coolcool.moviecool.fragment.common.StateFragment;

import cn.bmob.v3.BmobUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainAccountFragment extends StateFragment implements View.OnClickListener{

    private Context mContext;
    private ImageView imageAccount;
    private TextView tvAccountPrompt;
    private TextView tvAccount;
    private CardView accountCard;
    private CardView favoriteCard;

    public MainAccountFragment() {
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
        View view=inflater.inflate(R.layout.fragment_main_account, container, false);
        Log.i(TAG,"onCreateView");
        initView(view);

        updateAccountState(Constant.ONLINE_STATE);

        return view;
    }

    private void initView(View view) {
        imageAccount= (ImageView) view.findViewById(R.id.imageAccountFragment);
        tvAccount= (TextView) view.findViewById(R.id.tvAccountFragment);
        tvAccountPrompt = (TextView) view.findViewById(R.id.tvAccountPromptFragment);
        accountCard= (CardView) view.findViewById(R.id.accountCard);
        favoriteCard= (CardView) view.findViewById(R.id.favoriteCard);

        accountCard.setOnClickListener(this);
        favoriteCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountCard:
                if (Constant.ONLINE_STATE){
                    offLine();
                }else {
                    Intent intent=new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.favoriteCard:
                Intent intent=new Intent(mContext, FavoriteActivity.class);
                startActivity(intent);
                break;
        }
    }

    //下线
    private void offLine() {
        BmobUser.logOut(mContext);

        Constant.ONLINE_STATE=false;
        updateAccountState(false);

        SharedPreferences preferences= mContext
                .getSharedPreferences(Constant.USER_ACCOUNT_FILE, Activity.MODE_PRIVATE);
        boolean isRemember=preferences.getBoolean(Constant.USER_PASSWORD_SAVED_STATE,false);
        if (isRemember){
            String account=preferences.getString(Constant.USER_ACCOUNT,"");
            if (!account.equals("")){
                clearPassword(account);
            }
        }
    }

    //更新账户头像的颜色
    private void updateAccountState(boolean online){

        BaseActivity activity= (BaseActivity) mContext;
        activity.updateAccountState(online);
        int color=online?R.color.colorAccent:R.color.grayWhite;
        Drawable drawable= TintDrawableUtil
                .tintDrawable(mContext, R.drawable.ic_account_identity_24dp, color);
        imageAccount.setImageDrawable(drawable);
        imageAccount.invalidate();

        if (online&&Constant.ordinaryUser!=null){
            tvAccount.setText(Constant.ordinaryUser.getUsername());
        }else {
            tvAccount.setText("账号");
        }
        tvAccountPrompt.setText(online ? "注销" : "登陆");
    }

    //删除用户密码
    private void clearPassword(String userName){
        String password="";
        SharedPreferences.Editor editor = mContext
                .getSharedPreferences(Constant.USER_ACCOUNT_FILE, Context.MODE_PRIVATE).edit();
        editor.putString(Constant.USER_ACCOUNT, userName);
        long currentTime=System.currentTimeMillis();
        editor.putString(Constant.USER_PASSWORD, PasswordUtils.encodePassword(password, currentTime));
        editor.putLong(Constant.USER_PASSWORD_SAVED_TIME, currentTime);
        editor.putBoolean(Constant.USER_PASSWORD_SAVED_STATE, true);
        editor.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAccountState(Constant.ONLINE_STATE);
        Log.i(TAG,"onResume");
    }
}
