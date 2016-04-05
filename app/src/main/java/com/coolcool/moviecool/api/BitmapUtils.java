package com.coolcool.moviecool.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.coolcool.moviecool.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by yanshili on 2016/3/3.
 */
public class BitmapUtils {
    public static final String TAG="BitmapUtils";

    //imageView视图到父布局的宽带
    private static int padding=16;

    /**
     * 按照原图宽高比缩放图片，适应当前的imageView
     * @param originalBitmap    原始图片
     * @param imageView         图片容器
     * @return                  缩放后的图片
     */
    public static Bitmap getScaledBitmap(Bitmap originalBitmap, ImageView imageView){
        if (originalBitmap==null||imageView==null) return null;
        int width= (int) (imageView.getWidth()/ Constant.dp);
        int height= (int) (imageView.getHeight()/ Constant.dp);
        Log.i(TAG,  "newWidth ==" + width + "  newHeight==" + height);
        return getScaledBitmap(originalBitmap,width,height);
    }

    private static Bitmap getScaledBitmap(Bitmap originalBitmap, int viewWidth,int viewHeight){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        originalBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] bytes=baos.toByteArray();
        BitmapFactory.Options options=new BitmapFactory.Options();
        //采样时将inJustDecodeBounds设置为true
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        options.inSampleSize=calculateInSampleSize(options
                , viewWidth, viewHeight);

        //开始解析时将inJustDecodeBounds设置为false
        options.inJustDecodeBounds=false;

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length,options);
    }

    //按照imageView的比例缩放
    public static Bitmap getFixedSizeBitmap(Bitmap originalBitmap,ImageView imageView){
        Bitmap newBitmap=originalBitmap;
        int newWidth=imageView.getWidth();
        int newHeight=imageView.getHeight();
        if (newHeight>0&&newWidth>0) {
            newBitmap=getFixedSizeBitmap(originalBitmap,newWidth,newHeight);
        }else {

        }
        return newBitmap;
    }

    static int getTimes=0;
    public static final String IMAGE_WIDTH="IMAGE_WIDTH";
    public static final String IMAGE_HEIGHT="IMAGE_HEIGHT";
    volatile static HashMap<String ,Integer> imageSize=new HashMap<>();
    //按照imageView的比例缩放,并保存当前view的尺寸，并且savedTag相同时用此尺寸
    public synchronized static Bitmap getFixedSizeBitmap(Bitmap originalBitmap
            ,ImageView imageView,String  savedSizeTag){

        if (imageView==null||originalBitmap==null) return null;
        Log.i(TAG,savedSizeTag+"  转化前 =="+originalBitmap.getByteCount()/1024+"  kb");

        int savedWidth;
        int savedHeight;
        if (imageSize.get(IMAGE_HEIGHT+savedSizeTag)==null){
            savedWidth =imageView.getWidth();
            savedHeight =imageView.getHeight();
            imageSize.put(IMAGE_HEIGHT+savedSizeTag,savedHeight);
            imageSize.put(IMAGE_WIDTH+savedSizeTag,savedWidth);
        }else {
            savedWidth=imageSize.get(IMAGE_WIDTH+savedSizeTag);
            savedHeight=imageSize.get(IMAGE_HEIGHT+savedSizeTag);
        }
        Bitmap bitmapNew=originalBitmap;

        if (savedHeight >0&& savedWidth >0) {
            bitmapNew=getFixedSizeBitmap(originalBitmap, savedWidth, savedHeight);
        }

        Log.i(TAG,  "newWidth ==" + savedWidth + "  newHeight==" + savedHeight);

        Log.i(TAG,savedSizeTag+"  转化后 =="+bitmapNew.getByteCount()/1024+"  kb");

        while (getTimes<3&&bitmapNew!=null&&bitmapNew.getByteCount()==originalBitmap.getByteCount()){
            bitmapNew= getFixedSizeBitmap(originalBitmap,imageView,savedSizeTag);
            getTimes++;
        }

        return bitmapNew;
    }

    //按照固定长宽缩放
    public static Bitmap getFixedSizeBitmap(Bitmap originalBitmap,int newWidth,int newHeight){
        if (originalBitmap==null) return null;
        Bitmap newBitmap=originalBitmap;

        int oldWidth=originalBitmap.getWidth();
        int oldHeight=originalBitmap.getHeight();
        Log.i(TAG,  "newWidth ==" + newWidth + "  newHeight==" + newHeight);
        float scaleWidth=(float)newWidth/(float)oldWidth;
        float scaleHeight=(float)newHeight/(float)oldHeight;
        Matrix matrix=new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        newBitmap=Bitmap.createBitmap(originalBitmap,0,0,oldWidth,oldHeight,matrix,true);
        //回收原图片，本应用中不需要回收，因为还需要将原图缓存到磁盘
//        if (originalBitmap!=null&&!originalBitmap.isRecycled()){
//            originalBitmap.recycle();
//        }

        return newBitmap;
    }

    //参照imageView的大小按图片比例缩放
    public static Bitmap getFullViewBitmap(Bitmap originalBitmap,ImageView imageView){
        Bitmap bitmap=originalBitmap;
        Context context=imageView.getContext();
        WindowManager windowManager= (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int wWidth=windowManager.getDefaultDisplay().getWidth();
        int wHeight=windowManager.getDefaultDisplay().getHeight();
        int oldWidth=originalBitmap.getWidth();
        int oldHeight=originalBitmap.getHeight();
        int newWidth=oldWidth;
        int newHeight=oldHeight;
        if (wWidth<=wHeight){
            newWidth=imageView.getWidth();
            float ratio=(float)newWidth/(float)oldWidth;
            newHeight=(int)(oldHeight*ratio);
        }else {
            newHeight=wHeight-padding*2;
            float ratio=(float)newHeight/(float)oldHeight;
            newWidth=(int)(oldWidth*ratio);
        }
        bitmap=getFixedSizeBitmap(bitmap,newWidth,newHeight);
        return bitmap;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options
            ,int viewWidth,int viewHeight){
//        options.inJustDecodeBounds=false;
        int inSampleSize=1;
        int widthRatio=1;
        int heightRatio=1;

        int imgWidth=options.outWidth;
        int imgHeight=options.outHeight;
        if (imgHeight>viewHeight||imgWidth>viewWidth){
            if (viewWidth>0) {
                widthRatio=Math.round((float)imgWidth/(float)viewWidth);
            }
            if (viewHeight>0){
                heightRatio=Math.round((float)imgHeight/(float)viewHeight);
            }
            if (viewHeight>0&&viewWidth>0) {
                inSampleSize=widthRatio>heightRatio?widthRatio:heightRatio;
            }else {
                inSampleSize=widthRatio<heightRatio?widthRatio:heightRatio;
            }
        }

        return inSampleSize;
    }
}
