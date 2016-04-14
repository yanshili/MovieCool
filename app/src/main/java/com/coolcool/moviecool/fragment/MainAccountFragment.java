package com.coolcool.moviecool.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.coolcool.moviecool.activity.AboutActivity;
import com.coolcool.moviecool.activity.MainActivity;
import com.coolcool.moviecool.activity.FavoriteActivity;
import com.coolcool.moviecool.activity.LoginActivity;
import com.coolcool.moviecool.common.Constant;
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
    private CardView aboutCard;

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
        aboutCard= (CardView) view.findViewById(R.id.aboutCard);

        accountCard.setOnClickListener(this);
        favoriteCard.setOnClickListener(this);
        aboutCard.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.accountCard:
                if (Constant.ONLINE_STATE){
                    //当用户点击注销时，提醒用户是否确定注销账号
                    showAlert();
                }else {
                    //用户点击登陆按钮时进入登陆页面
                    Intent intentLogin=new Intent(mContext, LoginActivity.class);
                    startActivity(intentLogin);
                }
                break;
            case R.id.favoriteCard:
                //当用户点击收藏按钮时进入收藏页面
                Intent intentFavorite=new Intent(mContext, FavoriteActivity.class);
                startActivity(intentFavorite);
                break;
            case R.id.aboutCard:
                //当用户点击收藏按钮时进入收藏页面
                Intent intentAbout=new Intent(mContext, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
    }

    //当用户点击注销时，提醒用户是否确定注销账号
    private void showAlert(){
        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setTitle("点击确定注销账号")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case AlertDialog.BUTTON_POSITIVE:
                                offLine();
                                break;
                            case AlertDialog.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                })
                .show();
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

        MainActivity activity= (MainActivity) mContext;
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
