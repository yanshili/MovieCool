package com.coolcool.moviecool.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.coolcool.moviecool.model.MovieInfo;

/**
 * Created by yanshili on 2016/2/28.
 */
public class DataCacheBase {
    public static final String TAG="DataCacheBase";
    private static DataCacheBase instance=null;
    private static LruCache<String,Bitmap> mBitmapMemoryCache;
    private static LruCache<String,Object> mObjectMemoryCache;
    private static DiskCache mDiskCache;
    private Context mContext;

    private DataCacheBase(Context context) {
        mContext=context;
        //得到最大可用内存，单位为kb
        int maxMemory= (int) (Runtime.getRuntime().maxMemory()/1024);
        //用可用内存的1/8作为缓存的大小
        int cacheSize=maxMemory/8;
        mBitmapMemoryCache =new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                int i=bitmap.getByteCount()/1024;
                Log.i(TAG,"图片大小"+ i+" kb");
                return i;
            }
        };
        Log.i(TAG, "图片缓存大小" + cacheSize + " kb");
        mObjectMemoryCache=new LruCache<String,Object>(cacheSize/4);
        mDiskCache= DiskCache.getInstance(context);
    }

    private static synchronized void init(Context context){
        if (instance==null){
            instance=new DataCacheBase(context);
        }
    }

    public static DataCacheBase getInstance(Context context) {
        if (instance==null){
            init(context);
        }
        return instance;
    }

    //将数据Data存入内存中
    public void addDataToMemoryCache(String htmlUrl, Object data) {
        if (getBitmapFromMemoryCache(htmlUrl)==null){
            mObjectMemoryCache.put(htmlUrl, data);
        }
    }
    //从内存中获取数据
    public <T extends Object> T getDataFromMemoryCache(String htmlUrl) {
        if (htmlUrl!=null){
            return (T) mObjectMemoryCache.get(htmlUrl);
        }else {
            return null;
        }
    }

    /**
     * 将位图存入内存中
     * @param imgUrl    图片URL地址
     * @param bitmap    需要存储的位图数据
     */
    public void addBitmapToMemoryCache(String imgUrl, Bitmap bitmap) {
        addBitmapToMemoryCache(imgUrl,bitmap,null);
    }

    /**
     * 根据图片的URL从内存中读取位图数据
     * @param imgUrl    图片的URL地址
     * @return          返回指定的位图数据
     */
    public Bitmap getBitmapFromMemoryCache(String imgUrl) {
       return getBitmapFromMemoryCache(imgUrl,null);
    }

    /**
     * 根据图片的URL从内存中读取位图数据
     * @param imgUrl    图片的URL地址
     * @param bitmap    需要存储的位图数据
     * @param sizeTag   位图的大小标志
     */
    public void addBitmapToMemoryCache(String imgUrl, Bitmap bitmap,String sizeTag) {
        if (bitmap==null||imgUrl==null) return;
        if (getBitmapFromMemoryCache(imgUrl+sizeTag)==null){
            mBitmapMemoryCache.put(imgUrl, bitmap);
        }
    }

    /**
     * 根据图片的URL从内存中读取位图数据
     * @param imgUrl    图片的URL地址
     * @param sizeTag   位图的大小标志
     * @return          返回指定的位图数据
     */
    public Bitmap getBitmapFromMemoryCache(String imgUrl,String sizeTag) {
        if (imgUrl!=null){
            Bitmap bitmap=mBitmapMemoryCache.get(imgUrl+sizeTag);
            if (bitmap!=null)
                Log.i(TAG, "从内存中获取图片" + DiskCache.convertUrlToName(imgUrl));
            return bitmap;
        }else {
            return null;
        }
    }

    /**
     * 根据图片的URL从磁盘中读取位图数据
     * @param imgUrl    图片的URL地址
     * @return          返回指定的位图数据
     */
    public Bitmap getBitmapFromDiskCache(String imgUrl) {
        if (imgUrl!=null){
            Bitmap bitmap=mDiskCache.get(imgUrl,null);
            if (bitmap!=null)
                Log.i(TAG, "从磁盘中获取图片" + DiskCache.convertUrlToName(imgUrl));
            return bitmap;

        }else {
            return null;
        }
    }

    public void addBitmapToDiskCache(MovieInfo movieInfo,Bitmap bitmap) {
        if (movieInfo==null||bitmap==null) return;
        long postId=movieInfo.getPostId();

        if (postId==0) return;
        if (mDiskCache.get(postId)==null){
            mDiskCache.put(movieInfo, bitmap);
        }
    }

    public Bitmap getBitmapFromDiskCache(MovieInfo movieInfo) {
        if (movieInfo==null) return null;
        long postId=movieInfo.getPostId();

        if (postId!=0){
            Bitmap bitmap=mDiskCache.get(postId);
            if (bitmap!=null)
                Log.i(TAG, "从磁盘中获取图片" +postId);
            return bitmap;
        }else {
            return null;
        }
    }

}
