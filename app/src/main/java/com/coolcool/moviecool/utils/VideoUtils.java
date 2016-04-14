package com.coolcool.moviecool.utils;

import android.content.Context;
import android.util.Log;

import com.coolcool.moviecool.common.Constant;
import com.coolcool.moviecool.model.DisplayType;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class VideoUtils {
    public static final String TAG="VideoUtils";

    public static List<List<DisplayType>> PAGE_TYPE_LIST=new ArrayList<>();

    public static void initPageTypeArray(Context context){
        for (int i=0;i< Constant.videoTabs.length;i++){
            if (i!=0){
                String pageName= Constant.videoTabs[i];
                fetchDisplayType(context,pageName);
            }
        }
    }

    //抓取展示类型数据
    private static void fetchDisplayType(final Context context, final String pageName){

        BmobQuery<DisplayType> query=new BmobQuery<>();
        query.addWhereEqualTo("pageName",pageName);
        query.order("displayOder");
        query.findObjects(context, new FindListener<DisplayType>() {
            @Override
            public void onSuccess(List<DisplayType> list) {
                Log.i(TAG, "成功下载page==，个数" + list.size());
                if (list.size() > 0) {
                    PAGE_TYPE_LIST.add(list);
                }
            }

            @Override
            public void onError(int i, String s) {

                Log.i(TAG, "下载失败 i==" + i + "   " + s);
            }
        });
    }


}
