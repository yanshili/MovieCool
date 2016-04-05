package com.coolcool.moviecool.custom;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 全部影视页面的listView的单个视图
 * 每个视图为一个单行的gridLayout网格视图，单个影片视图，名字重叠在海报上面
 * Created by yanshili on 2016/3/29.
 */
public class ItemGridLayout extends GridLayout {
    public static final String TAG="ItemGridLayout";
    //列数
    public static int columnCount=3;
    //列数
    public static float WHRatio=1.4f;
    SimpleDraweeView imageView;
    TextView textView;
    View mView;

    public ItemGridLayout(Context context) {
        super(context);
        initView(context,columnCount);
    }

    public ItemGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,columnCount);
    }

    public ItemGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,columnCount);
    }

    Context mContext;
    private void initView(Context context,int columnCount){
        mContext=context;
        if (columnCount>10) columnCount=10;
        setColumnCount(columnCount);
        for (int i=0;i<columnCount;i++){
            addView(initItemView());
        }
    }

    private View initItemView(){
        View itemView=inflate(mContext, R.layout.item_image_grid,null);
        CardView cardView= (CardView) itemView.findViewById(R.id.cardViewGridCommon);

        int width= (int) ((Constant.WIDTH*12f)/(columnCount*13f));
        int height= (int) (width*WHRatio);
        //列间距和图片宽度比为1：12，即列间距总和为图片宽度总和的1/12   4dp
        int columnPadding=(int) (width/(12*2f));
        //行间距
        int rowPadding=columnPadding*2;

        Log.i(TAG, "imageViewWidth ==" + width + "  imageViewHeight==" + height);
        LayoutParams params=new LayoutParams();
        params.setMargins(columnPadding, rowPadding, columnPadding, 0);

        cardView.setLayoutParams(params);
        cardView.setMinimumWidth(width);
        cardView.setMinimumHeight(height);
//        cardView.setPadding(columnPadding, 0, columnPadding, rowPadding);
        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        cardView.setCardElevation(0);
        cardView.setRadius(0);

        CardView.LayoutParams imageParams=new CardView.LayoutParams(width, height);
        imageView= (SimpleDraweeView) itemView.findViewById(R.id.imageViewGridCommon);
        imageView.setLayoutParams(imageParams);

        CardView.LayoutParams textParams=new CardView.LayoutParams(width
                , CardView.LayoutParams.WRAP_CONTENT);
        textParams.gravity=Gravity.BOTTOM;
        textView= (TextView) itemView.findViewById(R.id.textViewGridCommon);
        textView.setTextColor(Color.WHITE);
        if (height/8<10* Constant.dp){
            textView.setTextSize(height/8);
            textView.setTextColor(Color.TRANSPARENT);
        }
        textView.setLayoutParams(textParams);

        mView=itemView.findViewById(R.id.bottomShadowGrid);
        mView.setEnabled(false);
        CardView.LayoutParams p=new CardView.LayoutParams(width, (int) (height/8.5f));
        p.gravity=Gravity.BOTTOM;
        mView.setLayoutParams(p);

        return itemView;
    }

    public <T extends View> T getView(@IdRes int id){
        switch (id){
            case R.id.imageViewGridCommon:
                return (T)imageView;
            case R.id.textViewGridCommon:
                return (T) textView;
            case R.id.bottomShadowGrid:
                return (T) mView;
        }
        return null;
    }
}
