package com.coolcool.moviecool;

import android.app.Application;

import com.coolcool.moviecool.utils.Constant;
import com.facebook.drawee.backends.pipeline.Fresco;

import cn.bmob.v3.Bmob;

/**
 * Created by yanshili on 2016/4/2.
 */
public class MyApp extends Application {
    public static final String TAG="MyApp";

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp(){
        //服务器初始化，用于同数据库连接的
        Bmob.initialize(this, Constant.APPLICATION_ID);
        //fresco库初始化，用于展示图片的库
        Fresco.initialize(this);
        //初始化常量
        Constant.initConstant(this);
        //获取屏幕参数
        Constant.initDpi(this);
    }

}
