package com.coolcool.moviecool.core;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Toast;

/**
 * Created by yanshili on 2016/3/21.
 */
public class TextViewUtils {
    public static final String URL_INFO_HEADER="下载链接：\n";
    public static final String BAIDU_URL_INFO_HEADER="百度网盘：\n";
    public static final String BAIDU_URL_KEY="\n密码：";

//    SpannableString sp;
//    String url;
//    String urlText;
//    String passWord;

    public static SpannableString getSPText(Context context,String url,String urlText,String passWord,boolean isbaidu){
        SpannableString sp=null;
        if (isbaidu){
            if (passWord!=null){
                String textHeader=BAIDU_URL_INFO_HEADER+urlText;
                String text=textHeader+BAIDU_URL_KEY;

                sp=new SpannableString(text);
                setSpans(sp
                        , BAIDU_URL_INFO_HEADER.length()
                        , textHeader.length()
                        ,url);
            }else {
                String text=BAIDU_URL_INFO_HEADER+urlText;
                sp=new SpannableString(text);
                setSpans(sp
                        ,BAIDU_URL_INFO_HEADER.length()
                        , text.length()
                        ,url);
            }

        }else {
            String text=URL_INFO_HEADER+urlText;
            sp=new SpannableString(text);
            setSpans(context,sp
                    , URL_INFO_HEADER.length()
                    , text.length()
                    ,url,urlText);

        }

        return sp;
    }

    public static SpannableString getSPText(Context context,String url,String urlText){
        return getSPText(context,url,urlText,null,false);
    }

    private static void setSpans(SpannableString sp, int start, int end){
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081"))
                ,start
                ,end
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new UnderlineSpan()
                ,start
                ,end
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sp.setSpan(new RelativeSizeSpan(1.3f)
                ,start
                ,end
                , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

    }

    private static void setSpans(Context context,SpannableString sp, int start, int end
            ,String url,String urlText){
        final Context mContext=context;
        final String mUrl=url;
        final String mUrlText=urlText;
        setSpans(sp, start, end);
        sp.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                ClipboardManager cmUrl= (ClipboardManager) mContext
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmUrl.setPrimaryClip(ClipData.newPlainText(mUrlText
                        , mUrl));
                Toast.makeText(mContext, "下载链接：\n" + mUrlText + "\n已复制到剪贴板"
                        , Toast.LENGTH_SHORT).show();
            }
        },start,end,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private static void setSpans(SpannableString sp, int start, int end,String url){
        setSpans(sp, start, end);
        sp.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
