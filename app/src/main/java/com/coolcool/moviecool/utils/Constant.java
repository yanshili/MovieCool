package com.coolcool.moviecool.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.model.OrdinaryUser;

import java.util.ArrayList;

/**
 * Created by yanshili on 2016/3/7.
 */
public class Constant {
    public static final String TAG="VideoUtils";
    //服务器application id
    public static final String APPLICATION_ID="55c1f4bed3785eaabd622b1257ae0645";

    //sharedPreference文件用户信息文件名
    public static final String USER_ACCOUNT_FILE ="user_account";
    //sharedPreference文件内保存账号的键名
    public static final String USER_ACCOUNT="account";
    //sharedPreference文件内保存密码的键名
    public static final String USER_PASSWORD="password";
    //sharedPreference文件内保存密码的键名
    public static final String USER_PASSWORD_SAVED_TIME="savedTime";
    //sharedPreference文件内是否保存账号和密码的布尔值的键名
    public static final String USER_PASSWORD_SAVED_STATE="saved";
    //账户是否在线
    public static boolean ONLINE_STATE=false;
    //当前登录的用户
    public static OrdinaryUser ordinaryUser;

    public static DisplayMetrics metrics;
    public static float density;
    public static float dp;
    public static float xdp;
    public static float ydp;

    //屏幕宽度像素pixel
    public static int HEIGHT;
    //屏幕高度像素pixel
    public static int WIDTH;
    //tab的名字数组
    public static String[] videoTabs;
    //筛选条件的二维数组，即第几列第几行的条件
    public static String[][] criteria;
    //数据库列名数组
    public static String[] DBColumnName;
    //筛选条件分类类目集合
    public static ArrayList<String[]> array;
    public static String[] selectCategory;
    public static String[] areasText,genreText,releaseYearText;

    //获取屏幕参数
    public static void initDpi(Context context) {
        metrics=context.getResources().getDisplayMetrics();
        dp=metrics.densityDpi/160;
        xdp=metrics.xdpi/160;
        ydp=metrics.ydpi/160;
        density=metrics.density;


        HEIGHT=metrics.heightPixels;
        WIDTH=metrics.widthPixels;

        Log.i(TAG, "dp=" + dp
                + " xdp=" + xdp
                + " ydp=" + ydp
                + " density=" + density
                +" HEIGHT="+HEIGHT
                +" WIDTH"+WIDTH
                +" scaledDensity"+metrics.scaledDensity
        );
    }

    //初始化数组常量
    public static void initConstant(Context context){
        //数据库列名数组
        DBColumnName=context.getResources().getStringArray(R.array.database_column);
        //区域名称数组
        areasText=context.getResources().getStringArray(R.array.criteria_area);
        //流派名称数组
        genreText=context.getResources().getStringArray(R.array.criteria_genre);
        //上映年份数组
        releaseYearText=context.getResources().getStringArray(R.array.criteria_release_year);

        //筛选条件分类类目集合
        array=new ArrayList<String[]>();
        array.add(areasText);
        array.add(genreText);
        array.add(releaseYearText);

        criteria=new String[array.size()][array.get(1).length];
        //筛选条件分类类目二维数组
        //即第几列第几行的条件内容
        for (int i=0;i<array.size();i++){
            for (int j=0;j<array.get(i).length;j++){
                criteria[i][j]=array.get(i)[j];
            }

        }

        //初始化已选中条件的数组
        selectCategory=new String[array.size()];
        for (int i=0;i<selectCategory.length;i++){
            selectCategory[i]=criteria[i][0];
        }

        videoTabs=context.getResources().getStringArray(R.array.main_video_tab);
    }

}
