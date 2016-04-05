package com.coolcool.moviecool.core;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by yanshili on 2016/3/28.
 */
public class BitmapWorkerTask extends AsyncTask<String,Void,Bitmap> {
    public static final String TAG="BitmapWorkerTask";
    private ImageView mImageView;
    private String sizeTag;

    public BitmapWorkerTask(ImageView imageView, String sizeTag) {
        mImageView = imageView;
        this.sizeTag = sizeTag;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
//        InputStream bitmapStream= NetUtils.getImageStreamGetMethod(params[0]);
        try {
            BitmapFactory.Options options=new BitmapFactory.Options();

        }catch (OutOfMemoryError error){
            Log.i(TAG,"解码图片异常--->"+error);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    private static Bitmap getImageGetMethod(String url){
        HttpURLConnection conn=null;
        try {
            //利用String构建一个URL
            URL mUrl=new URL(url);
            //利用URL开启一个连接
            conn = (HttpURLConnection) mUrl.openConnection();
            //设置连接方法
            conn.setRequestMethod("GET");
            //设置连接超时为5秒
            conn.setConnectTimeout(5000);
            //设置读取超时未3秒
            conn.setReadTimeout(3000);

            //获取网络连接状态码
            int responseCode=conn.getResponseCode();
            if (responseCode== HttpURLConnection.HTTP_OK) {
                BitmapFactory.Options options=new BitmapFactory.Options();
                options.inPreferredConfig= Bitmap.Config.RGB_565;

                return BitmapFactory.decodeStream(conn.getInputStream(),null,options);
            }else {
                Log.i(TAG ,"网络连接状态码：" + responseCode);
            }
        } catch (MalformedURLException e) {
            Log.i(TAG,"String url 格式不正确，无法创建URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"网络连接失败HttpUrlConnection设置错误，但URL格式正确");
            e.printStackTrace();
        }finally {
            if (conn != null) {
                //关闭网络连接
                conn.disconnect();
            }
        }
        return null;
    }






    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Bitmap bitmap) {
        super.onCancelled(bitmap);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
