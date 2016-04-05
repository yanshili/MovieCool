package com.coolcool.moviecool.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 功能：网络的操作
 * Created by yanshili on 2016/2/19.
 */
public class NetUtils {
    public static final String TAG="NetUtils";
    private int connectTime=5000;
    private int readTime=3000;


    //向服务器发送内容，并接收响应的内容文本
    public static String postMethod(String url, String content){
        HttpURLConnection conn=null;
        try {
            //利用String构建一个URL
            URL mUrl=new URL(url);
            //利用URL开启一个连接
            conn = (HttpURLConnection) mUrl.openConnection();
            //设置连接方法
            conn.setRequestMethod("POST");
            //设置连接超时为5秒
            conn.setConnectTimeout(5000);
            //设置读取超时未3秒
            conn.setReadTimeout(3000);
            //设置此连接可以向服务器写数据，默认不可以
            conn.setDoInput(true);
            //利用HttpURLConnection获取一个输出流
            OutputStream out = conn.getOutputStream();
            //向服务器写自定义的数据content
            out.write(content.getBytes());
            out.flush();
            out.close();
            //获取网络连接状态码
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                //利用HttpURLConnection获取输入流
                InputStream in = conn.getInputStream();
                //用自定义的方法从InputStream里读取的字符串并将字符串返回
                return getTextFromInputStream(in);
            } else {
                Log.i(TAG, "网络连接状态码：" + responseCode);
            }
        } catch (MalformedURLException e) {
            Log.i(TAG,"String url 格式不正确，无法创建URL");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,"网络连接失败，但URL格式正确");
            e.printStackTrace();
        }
        return null;
    }

    public static String getMethod(String url){
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

//            conn.setRequestProperty("Host", "www.lbldy.com");
//            conn.setRequestProperty("User-Agent:", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
//            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
////            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
//            conn.setRequestProperty("Referer","http://www.lbldy.com/");
//            conn.setRequestProperty("Cookie", "Hm_lvt_3ef61ea52db67ef6feea326e6078dc47=1456047655,1456056478,1456142943; CNZZDATA1000393914=598518632-1456043944-%7C1456141851; Hm_lpvt_3ef61ea52db67ef6feea326e6078dc47=1456142943");
//            conn.setRequestProperty("Connection", "keep-alive");
//            conn.setRequestProperty("Cache-Control", "max-age=0");

            //获取网络连接状态码
            int responseCode=conn.getResponseCode();
            if (responseCode== HttpURLConnection.HTTP_OK) {

                return getTextFromInputStream(conn.getInputStream());
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

//            conn.setRequestProperty("Host", "www.lbldy.com");
//            conn.setRequestProperty("User-Agent:", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0");
//            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            conn.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
////            conn.setRequestProperty("Accept-Encoding","gzip, deflate");
//            conn.setRequestProperty("Referer","http://www.lbldy.com/");
//            conn.setRequestProperty("Cookie", "Hm_lvt_3ef61ea52db67ef6feea326e6078dc47=1456047655,1456056478,1456142943; CNZZDATA1000393914=598518632-1456043944-%7C1456141851; Hm_lpvt_3ef61ea52db67ef6feea326e6078dc47=1456142943");
//            conn.setRequestProperty("Connection", "keep-alive");
//            conn.setRequestProperty("Cache-Control", "max-age=0");

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

    public static Bitmap getImgGetMethod(String url){
        return getImgGetMethod(url,1);
    }
    /**
     * 多次获取位图
     * @param url
     * @param tryTimes  尝试次数
     * @return
     */
    public static Bitmap getImgGetMethod(String url,int tryTimes){
        if (tryTimes<1) tryTimes=1;
        Bitmap bitmap=null;
        int times=1;
        boolean loading=true;
        while (loading){
            if (bitmap==null){
                if (times>tryTimes){
                    times=1;
                    loading=false;
                }else{
                    bitmap=getImageGetMethod(url);
                    times++;
                }
            }else {
                times=1;
                loading=false;
            }
        }
        return bitmap;
    }

    private static String getTextFromInputStream(InputStream in){
        String text=null;
        byte[] buffer=new byte[1024];
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        int len;
        try {
            while ((len=in.read(buffer,0,1024))!=-1){
                baos.write(buffer,0,len);
            }
            in.close();
            text=baos.toString();
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }

}
