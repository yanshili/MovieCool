package com.coolcool.moviecool.custom;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * 单个影片视图，名字在海报下方
 * Created by yanshili on 2016/3/26.
 */
public class ItemImageView extends FrameLayout {
    public static final String TAG="ItemImageView";

    public ItemImageView(Context context,int columnCount,boolean hasHeaderImage) {
        super(context);
        initView(context,columnCount,hasHeaderImage);
    }

    public ItemImageView(Context context) {
        super(context);
        initView(context,3,false);
    }

    public ItemImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context,3,false);
    }

    public ItemImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context,3,false);
    }

    SimpleDraweeView imageView;
    TextView textView;
    View mView;
    private void initView(Context context,int columnCount,boolean hasHeaderImage){

        int width= (int) (((Constant.WIDTH-32* Constant.dp)*12f)/(columnCount*13f));
        int height= (int) (width*1.3F);
        //列间距和图片宽度比为1：12，即列间距总和为图片宽度总和的1/12   4dp
        int columnPadding=(int) (width/(12*2f));
        //行间距
        int rowPadding=columnPadding*6;

        View itemView=inflate(context, R.layout.item_image,this);

        imageView= (SimpleDraweeView) itemView.findViewById(R.id.imageViewCommon);
        textView= (TextView) itemView.findViewById(R.id.textViewCommon);
        mView=itemView.findViewById(R.id.bottomShadow);
        mView.setEnabled(false);

        if (hasHeaderImage){
            width=(int) (Constant.WIDTH-32* Constant.dp-2*columnPadding);
            height= (int) (width*0.5F);
            GenericDraweeHierarchyBuilder builder
                    =new GenericDraweeHierarchyBuilder(context.getResources());
            GenericDraweeHierarchy hierarchy=builder
                    .setActualImageScaleType(ScalingUtils.ScaleType.FIT_XY)
                    .build();
            imageView.setHierarchy(hierarchy);
            rowPadding=columnPadding*4;
        }

        switch (columnCount){
            case 4:
                rowPadding=columnPadding*4;
                break;
            case 5:
                rowPadding=columnPadding*4;
                break;
            case 6:
                rowPadding=columnPadding*6;
                break;
            case 7:
                rowPadding=columnPadding;
                break;
        }

        if (columnCount<3){
            rowPadding=columnPadding;
        }

        setPadding(0, 0, 0, rowPadding);

        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(width,height);
        lp.setMargins(columnPadding, columnPadding, columnPadding, columnPadding);

        CardView cardView= (CardView) imageView.getParent();
        cardView.setLayoutParams(lp);
    }


    public <T extends View> T getView(@IdRes int id){
        switch (id){
            case R.id.imageViewCommon:
                return (T)imageView;
            case R.id.textViewCommon:
                return (T) textView;
            case R.id.bottomShadow:
                return (T) mView;
        }
        return null;
    }

}
