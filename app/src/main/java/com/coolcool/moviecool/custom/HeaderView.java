package com.coolcool.moviecool.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;

/**
 * 全部影视页面>>可滚动的>>分类标签>>头布局视图
 * Created by yanshili on 2016/3/12.
 */
public class HeaderView extends LinearLayout{
    public static final String CRITERIA_SCROLL="HeaderView.CRITERIA_SCROLL";

    public HeaderView(Context context) {
        super(context);
        initView(context);
        this.setOrientation(VERTICAL);
        LayoutParams layoutParams=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
    }

    //xml初始化调用此构造方法
    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        for (int i=0;i< Constant.array.size();i++){
            HorizontalScrollView scrollView=new HorizontalScrollView(context);
            scrollView.setHorizontalScrollBarEnabled(false);
            addView(scrollView);
            LinearLayout linear=new LinearLayout(context);
            linear.setOrientation(LinearLayout.HORIZONTAL);
            //为每一列linear容器设置tag标签
            linear.setTag(Constant.array.get(i)[0] + CRITERIA_SCROLL);
            //将linear容器添加到可横向滚动的容器中
            scrollView.addView(linear);
            for (int j=0;j< Constant.array.get(i).length;j++){
                TextView textView=new TextView(context);
                //为textView设置二维数组array第i列j行的字符串
                textView.setText(Constant.array.get(i)[j]);
                if (Constant.criteria[i][j].equals(Constant.selectCategory[i])){
                    //初始化时第一行均设为红色，即选中状态
                    textView.setTextColor(getResources().getColor(R.color.colorAccent));
                }else {
                    //初始化时，除了第一行其他均设置为蓝色，即未选中状态
                    textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                }
                //为每一个textView均设置一个tag标签
                textView.setTag(Constant.array.get(i)[j] + j);
                textView.setTextSize(4* Constant.dp);
                //设置间隔
                textView.setPaddingRelative((int) (16* Constant.dp)
                        , (int) (4* Constant.dp)
                        , (int) (16* Constant.dp)
                        , (int) (4* Constant.dp));

                //将textView添加到linear容器中
                linear.addView(textView);
            }
        }
    }

}
