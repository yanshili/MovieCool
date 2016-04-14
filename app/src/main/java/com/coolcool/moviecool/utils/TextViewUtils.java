package com.coolcool.moviecool.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Toast;

import com.coolcool.moviecool.R;

public class TextViewUtils {
    public static final String URL_INFO_HEADER="下载链接：\n";
    public static final String BAIDU_URL_INFO_HEADER="百度网盘：\n";
    public static final String BAIDU_URL_KEY="\n密码：";

    /**
     * 用于处理下载链接特殊效果
     * @param context       上下文环境
     * @param url           下载地址
     * @param urlText       下载地址得描述，即用于展示给用户的信息
     * @param passWord      百度网盘的密码
     * @param isbaidu       true：百度网盘的链接 false：普通下载链接
     * @return              用于在textView中展示该效果
     */
    public static SpannableString getSPText(Context context,String url,String urlText,String passWord,boolean isbaidu){
        SpannableString sp;
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

    //用于处理下载链接特殊效果
    public static SpannableString getSPText(Context context,String url,String urlText){
        return getSPText(context,url,urlText,null,false);
    }

    //用于处理声明的的特殊效果
    public static SpannableString getSPAboutText(final Context context){
        Resources resources=context.getResources();
        String header=resources.getString(R.string.about_info_statement_header);
        String body=resources.getString(R.string.about_info_body);
        final String mail=resources.getString(R.string.about_info_mail);
        String statement=header+body+mail;

        SpannableString sp=new SpannableString(statement);
        //设置字体大小
        sp.setSpan(new RelativeSizeSpan(1.3f)
                ,0
                ,header.length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置字体下划线
        sp.setSpan(new UnderlineSpan()
                ,(header+body).length()
                ,statement.length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置邮箱颜色
        sp.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081"))
                ,(header+body).length()
                ,statement.length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置邮箱字体大小
        sp.setSpan(new RelativeSizeSpan(1.3f)
                ,(header+body).length()
                ,statement.length()
                ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置字体邮箱点击监听事件
        sp.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Toast.makeText(context,"点击了邮箱",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"+mail));
//                intent.putExtra(Intent.EXTRA_SUBJECT, "标题");
//                intent.putExtra(Intent.EXTRA_TEXT,"内容");
                context.startActivity(intent);
            }
        },(header+body).length(),statement.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sp;
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

    /**
     * 用于辅助处理下载链接特殊效果
     * @param context       上下文环境
     * @param sp
     * @param start         开始位置
     * @param end           结束位置
     * @param url           下载链接
     * @param urlText       下载链接的描述
     */
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

    /**
     * 用于辅助处理下载链接特殊效果
     * @param sp
     * @param start         开始位置
     * @param end           结束位置
     * @param url           下载链接
     */
    private static void setSpans(SpannableString sp, int start, int end,String url){
        setSpans(sp, start, end);
        sp.setSpan(new URLSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

}
