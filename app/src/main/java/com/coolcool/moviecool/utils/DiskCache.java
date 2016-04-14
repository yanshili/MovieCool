package com.coolcool.moviecool.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.coolcool.moviecool.model.MovieInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskCache {
    public static final String TAG="DiskCache";
    private static final String CACHE_DIRECTORY="movieCache";
    File directory=null;
    private  static DiskCache mDiskCache;
    private Context mContext;

    private DiskCache(Context context) {
        mContext=context;
        directory=getDiskCacheDir(context,CACHE_DIRECTORY);
        if (!directory.exists()){
            boolean isDR=directory.mkdirs();
            Log.i(TAG, "创建目录-->" + isDR + directory.getPath());
        }else {
            Log.i(TAG, "目录存在-->" + directory.getPath());
        }
    }

    private static synchronized void init(Context context){
        if (mDiskCache==null){
            mDiskCache=new DiskCache(context);
        }
    }

    public static DiskCache getInstance(Context context){
        if (mDiskCache==null){
            init(context);
        }
        return mDiskCache;
    }


//    public void put(final String imgUrl, final Bitmap bitmap){
//
//    }

    public void put(final String imgUrl, final Bitmap bitmap, final String sizeTag){
        if (imgUrl==null||bitmap==null) return;
        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {

                File imgFile = new File(directory + File.separator
                        + convertUrlToName(imgUrl + sizeTag));
                boolean fileCreated = false;
                FileOutputStream fos = null;
                boolean bitmapCompressed;

                if (!imgFile.exists()) {
                    try {
                        fileCreated = imgFile.createNewFile();
                    } catch (IOException e) {
                        Log.i(TAG, "文件创建异常-->" + fileCreated + "异常：" + e);
                        e.printStackTrace();
                    }
                }

                if (imgFile.exists()) {
                    try {
                        fos = new FileOutputStream(imgFile);
                    } catch (FileNotFoundException e) {
                        Log.i(TAG, "输出流异常-->" + e);
                        e.printStackTrace();
                    }
                }

                if (fos != null) {
                    bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    if (!bitmapCompressed) {
                        Log.i(TAG, "图片压缩失败-->");
                    }
                    try {
                        fos.flush();
                    } catch (IOException e) {
                        Log.i(TAG, "输出流输出异常-->" + e);
                        e.printStackTrace();
                    }
                    try {
                        fos.close();
                    } catch (IOException e) {
                        Log.i(TAG, "输出流关闭异常-->" + e);
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public Bitmap get(String imgUrl,String sizeTag){
        if (imgUrl==null) return null;

        String imgPath=directory.getPath()+File.separator+convertUrlToName(imgUrl+sizeTag);
        File imgFile=new File(imgPath);
        Bitmap bitmap=null;
        if (imgFile.exists()){
            bitmap= BitmapFactory.decodeFile(imgPath);
            if (bitmap==null){
                imgFile.delete();
                Log.i(TAG, "图片解析为空，删除-->" + convertUrlToName(imgUrl));
            }
//            Log.i(TAG, "从磁盘中获取图片" + DiskCache.convertUrlToName(imgUrl));
        }
        return bitmap;
    }

    /**
     * 从磁盘获取原始图片图片
     * @param imgUrl        图片的URL地址
     * @param tryTimes      尝试抓取次数
     * @return              返回原始图片
     */
    public Bitmap get(String imgUrl,int tryTimes){
        if (tryTimes<1) tryTimes=1;

        String imgPath=directory.getPath()+File.separator+convertUrlToName(imgUrl);
        File imgFile=new File(imgPath);
        Bitmap bitmap=null;
        if (imgFile.exists()){

            int times=1;
            boolean loading=true;
            while (loading){
                if (bitmap==null){
                    if (times>tryTimes){
                        times=1;
                        loading=false;
                        imgFile.delete();
                    }else{
                        bitmap= BitmapFactory.decodeFile(imgPath);
                        times++;
                    }
                }else {
                    times=1;
                    loading=false;
                }
            }
        }
        return bitmap;
    }

    /**
     * 从磁盘获取原始图片图片
     * @param imgUrl        图片的URL地址
     * @param tryTimes      尝试抓取次数
     * @return              返回原始图片
     */
    public Bitmap get(String imgUrl,int tryTimes,ImageView imageView,String sizeTag){
        if (imgUrl==null||imageView==null) return null;
        Bitmap bitmap=get(imgUrl+sizeTag,tryTimes);
        if (bitmap==null) {
            bitmap=get(imgUrl, tryTimes - 1);
//            if (bitmap!=null){
//                bitmap=BitmapUtils.getFixedSizeBitmap(bitmap, imageView, sizeTag);
//                DataCacheBase.getInstance(mContext).addBitmapToMemoryCache(imgUrl,bitmap,sizeTag);
//            }
        }else {
//            DataCacheBase.getInstance(mContext).addBitmapToMemoryCache(imgUrl,bitmap,sizeTag);
        }

        bitmap= BitmapUtils.getFixedSizeBitmap(bitmap, imageView, sizeTag);
        DataCacheBase.getInstance(mContext).addBitmapToMemoryCache(imgUrl,bitmap,sizeTag);

        return bitmap;
    }

    public static String convertUrlToName(String url){
        if (url==null){
            return null;
        }
        String name;
        Pattern p=Pattern.compile(".+(?<=/)(.*?)?\\.[jpg|png|jepg]{3}");
        Matcher m=p.matcher(url);
        if (m.find()){
            name="cache"+m.group(1)+".jpg";
        }else {
            name="cache"+url;
        }
        return name;
    }

    public Bitmap get(long postId){
        if (postId==0) return null;
        String imgPath=directory.getPath()+File.separator+postId+".jpg";
        File imgFile=new File(imgPath);
        Bitmap bitmap=null;
        if (imgFile.exists()){
            bitmap= BitmapFactory.decodeFile(imgPath);
            if (bitmap==null){
                imgFile.delete();
                Log.i(TAG, "图片解析为空，删除-->" + postId);
            }
//            Log.i(TAG, "从磁盘中获取图片" + postId;
        }
        return bitmap;
    }

    public void put(final MovieInfo movieInfo, final Bitmap bitmap){
        final long postId=movieInfo.getPostId();
        if (postId==0||bitmap==null) return;

        ThreadUtils.getInstance().execute(new Runnable() {
            @Override
            public void run() {
//                if (!ThreadUtils.getInstance().isExecute()){
//                    Thread.yield();
//                }

                    File imgFile = new File(directory + File.separator +postId+".jpg");
                    boolean fileCreated = false;
                    FileOutputStream fos = null;
                    boolean bitmapCompressed;

                    if (!imgFile.exists()) {
                        try {
                            fileCreated = imgFile.createNewFile();
                        } catch (IOException e) {
                            Log.i(TAG, "文件创建异常-->" + fileCreated + "异常：" + e);
                            e.printStackTrace();
                        }
                    }

                    if (imgFile.exists()) {
                        try {
                            fos = new FileOutputStream(imgFile);
                        } catch (FileNotFoundException e) {
                            Log.i(TAG, "输出流异常-->" + e);
                            e.printStackTrace();
                        }
                    }

                    if (fos != null) {
                        bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        if (!bitmapCompressed) {
                            Log.i(TAG, "图片压缩失败-->");
                        }
                        try {
                            fos.flush();
                        } catch (IOException e) {
                            Log.i(TAG, "输出流输出异常-->" + e);
                            e.printStackTrace();
                        }
                        try {
                            fos.close();
                        } catch (IOException e) {
                            Log.i(TAG, "输出流关闭异常-->" + e);
                            e.printStackTrace();
                        }
//                        BTPFileResponse response= BmobProFile.getInstance(mContext)
//                                .upload(imgFile.getAbsolutePath(), new UploadListener() {
//                                    @Override
//                                    public void onSuccess(String fileName, String url, BmobFile bmobFile) {
//                                        movieInfo.setImageUrl(bmobFile.getUrl());
//
//                                        Log.i(TAG, "图片上传成功" + movieInfo.getImageUrl());
//                                        ThreadUtils.getInstance().execute(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                movieInfo.save(mContext, new SaveListener() {
//                                                    @Override
//                                                    public void onSuccess() {
//                                                        Log.i(TAG, "保存成功" + movieInfo.getImageUrl());
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(int i, String s) {
//                                                        Log.i(TAG, "保存失败" + movieInfo.getPostId());
//                                                    }
//                                                });
//                                            }
//                                        });
//                                    }
//
//                                    @Override
//                                    public void onProgress(int i) {
//
//                                    }
//
//                                    @Override
//                                    public void onError(int i, String s) {
//                                        Log.i(TAG, "图片上传失败" + movieInfo.getImageUrl()+"\n"+s);
//                                    }
//                                });
                    }

            }

        });
    }

    /**
     * 获取缓存目录
     * 外部目录：/storage/emulated/0/Android/data/<--...package name-->/cache/uniqueName
     * 缓存目录：/data/data/<--...package name-->/cache/uniqueName
     *
     */
    public File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath=null;
        File file=null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            file=context.getExternalCacheDir();
        }
        if (file==null){
            file = context.getCacheDir();
        }

        if (file!=null){
            cachePath=file.getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }
}
