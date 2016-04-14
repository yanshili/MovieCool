package com.coolcool.moviecool.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.ImageView;


/**
 * 功能：网络与UI的同步操作
 * Created by yanshili on 2016/2/19.
 */
public class AsyncNetUtils {
    public static final String TAG="AsyncNetUtils";

    public interface Callback{
        void onResponse(Object response);
    }

//    public static void post(final String url, final String content, final Callback callback){
//        final Handler handler=new Handler();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final String response= new String(NetUtils.getByteArray(NetUtils.postMethod(url, content)));
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        callback.onResponse(response);
//                    }
//                });
//            }
//        }).start();
//    }

    /**
     * 获取网页文本信息
     * @param htmlUrl
     * @param callback
     * @param context
     */
    public static void getHtmlText(final String htmlUrl, final Callback callback, final Context context){
        final Handler handler=new Handler();
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                if (!ThreadUtils.getInstance().isExecute()){
//                    Thread.yield();
//                }
                final String htmlText = NetUtils.getMethod(htmlUrl);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(htmlText);
                    }
                });
            }
        });

    }

    /**
     * 获取图片位图
     * @param imgUrl
     * @param callback
     *
     */
    public static void loadBitmap(final String imgUrl, final Callback callback
            , final Context context){

        final Handler handler=new Handler();
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                if (!ThreadUtils.getInstance().isExecute()){
//                    Thread.yield();
//                }

//                Bitmap b = DiskCache.getInstance(context).get(imgUrl,1);
//                if (b==null){
                Bitmap b = NetUtils.getImgGetMethod(imgUrl,1);
//                    if (b!=null)
//                        DiskCache.getInstance(context).put(imgUrl,b,null);
//                }
//
//                if (b==null){
//                    b=DiskCache.getInstance(context).get(imgUrl,2);
//                }
//                if (b==null){
//                    b = NetUtils.getImgGetMethod(imgUrl, 2);
//                    if (b!=null)
//                        DiskCache.getInstance(context).put(imgUrl,b,null);
//                }
                final Bitmap bitmap=b;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(bitmap);
                    }
                });
            }
        });

    }

    /**
     * 获取图片位图
     * @param imgUrl
     * @param callback
     *
     */
    public static void loadBitmap(final String imgUrl, final Callback callback
            , final Context context,final ImageView imageView,final String sizeTag){

//        //从内存获取转换后的图片,直接添加至imageView内
//        Bitmap bitmapM= DataCacheBase.getInstance(context).getBitmapFromMemoryCache(imgUrl,sizeTag);
//        if (bitmapM!=null){
//            imageView.setImageBitmap(bitmapM);
//            return;
//        }
//
        final Handler handler=new Handler();
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                //从磁盘获取转换后的图片，请求一次
                Bitmap b = DiskCache.getInstance(context).get(imgUrl,1,imageView,sizeTag);
                //从网络获取原始图片，获取后再转换，请求一次
                if (b==null){
                    b = NetUtils.getImgGetMethod(imgUrl,1);
                    if (b!=null){
                        DiskCache.getInstance(context).put(imgUrl,b,sizeTag);
                        b= BitmapUtils.getFixedSizeBitmap(b,imageView,sizeTag);
                        DataCacheBase.getInstance(context).addBitmapToMemoryCache(imgUrl, b, sizeTag);
                    }
                }

                //从磁盘获取转换后的图片，请求两次
                if (b==null){
                    b=DiskCache.getInstance(context).get(imgUrl,2,imageView,sizeTag);
                }

                //从网络获取原始图片，获取后再转换，请求两次
                if (b==null){
                    b = NetUtils.getImgGetMethod(imgUrl, 2);
                    if (b!=null){
                        DiskCache.getInstance(context).put(imgUrl,b,sizeTag);
                        b= BitmapUtils.getFixedSizeBitmap(b,imageView,sizeTag);
                        DataCacheBase.getInstance(context).addBitmapToMemoryCache(imgUrl,b,sizeTag);
                    }
                }
                final Bitmap bitmap=b;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(bitmap);
                    }
                });
            }
        });

    }

    public static void getBitmapFromDisk(final long postId, final Callback callback, final Context context){
        final Handler handler=new Handler();
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                if (!ThreadUtils.getInstance().isExecute()){
//                    Thread.yield();
//                }
                final Bitmap bitmap = DiskCache.getInstance(context).get(postId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(bitmap);
                    }
                });
            }
        });
    }

}
