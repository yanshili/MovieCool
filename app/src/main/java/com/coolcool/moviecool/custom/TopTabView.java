package com.coolcool.moviecool.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolcool.moviecool.utils.Constant;
import com.coolcool.moviecool.R;

/**
 * 全部影视页面>>头部>>可滚动的>>>fragment指示器页面标签视图
 * Created by yanshili on 2016/3/14.
 */
public class TopTabView extends LinearLayout{
    //颜色值红色
    public int COLOR_ACCENT;
    //颜色值蓝色
    public int COLOR_PRIMARY_DARK;

    //画笔
    Paint mPaint;
    //指示器下划线的开始位置
    float startX=2*16 * Constant.dp+2*16 * Constant.metrics.scaledDensity;
    //指示器下划线的高度位置
    float positionY;
    //指示器下划线的结束位置
    float stopX=2*startX;
    //当前viewPager选中页面位置
    int itemPosition;

    public TopTabView(Context context) {
        super(context);
    }

    public TopTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initChildrenView();
        initPaint();
    }

    public TopTabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        positionY =h;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.save();
        canvas.drawLine(startX, positionY, stopX, positionY, mPaint);
        canvas.restore();
    }

    //初始化画笔
    private void initPaint(){
        mPaint=new Paint();
        mPaint.setColor(COLOR_ACCENT);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(4 * Constant.dp);
    }

    //初始化子视图textView
    private void initChildrenView() {
        COLOR_ACCENT=getResources().getColor(R.color.colorAccent);
        COLOR_PRIMARY_DARK=getResources().getColor(R.color.colorPrimaryDark);

        for (int i=0;i< Constant.videoTabs.length;i++){
            TextView textView=new TextView(getContext());
            textView.setText(Constant.videoTabs[i]);
            textView.setTag(Constant.videoTabs[i]);
            textView.setTextColor(COLOR_PRIMARY_DARK);
            //设置间隔
            textView.setPaddingRelative((int) (16 * Constant.dp)
                    , (int) (8 * Constant.dp)
                    , (int) (16 * Constant.dp)
                    , (int) (8 * Constant.dp));
            textView.setTextSize(4* Constant.dp);
            addView(textView);
        }
    }

    //滚指示器下划线、textView和容器HorizontalScrollView的状态
    // 随着viewPager视图移动位置的改变而改变
    public void scroll(int position,float offset){

        if (offset>=0){
            TextView currView= (TextView) getChildAt(position);
            TextView nextView= (TextView) getChildAt(position+1);

            //指示器下划线移动
            startX=currView.getX()+currView.getWidth()*offset;
            stopX=nextView.getX()+nextView.getWidth()*offset;

            // textView颜色改变
            // 即当向左移动时该值由1递减至0，当向右移动时该值由0递增至1
            if (offset<0.6){
                changeTabColor(position+1,position);
            }else {
                changeTabColor(position,position+1);
            }

            //容器移动
            if (position>1&&position<getChildCount()-2){
                TextView view= (TextView) getChildAt(position-2);
                TextView lastView= (TextView) getChildAt(getChildCount()-1);
                int scrollLength= (int) (view.getWidth()*offset);
                if (position==getChildCount()-3){
                    scrollLength= (int) (lastView.getWidth()*offset);
                }

                int scrollPosition=(int) (view.getX()) + scrollLength;
                ((HorizontalScrollView) getParent()).scrollTo(scrollPosition, 0);
            }
            invalidate();

        }else {
            //停止滚动时，确认一遍tab中textView的颜色是否正确
            itemPosition=position;
            changeTabColor(position);
        }
    }

    /**
     * 改变textView的颜色
     * @param fromPosition  原始位置
     * @param toPosition    最新位置
     */
    private void changeTabColor(int fromPosition, int toPosition) {
        TextView textView= (TextView)findViewWithTag(Constant.videoTabs[fromPosition]);
        textView.setTextColor(COLOR_PRIMARY_DARK);
        textView= (TextView)findViewWithTag(Constant.videoTabs[toPosition]);
        textView.setTextColor(COLOR_ACCENT);
    }

    /**
     * 改变textView的颜色
     * @param toPosition    当前视图位置
     */
    private void changeTabColor(int toPosition){
        for (int i=0;i< Constant.videoTabs.length;i++){
            TextView textView= (TextView)getChildAt(i);
            if (i==toPosition){
                textView.setTextColor(COLOR_ACCENT);
            }else {
                textView.setTextColor(COLOR_PRIMARY_DARK);
            }
        }
    }

    //保存视图状态
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable p= super.onSaveInstanceState();
        SavedState savedState=new SavedState(p);
        savedState.stopX=stopX;
        savedState.startX=startX;
        savedState.itemPosition=itemPosition;
        return savedState;

    }

    //恢复视图
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState= (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        startX=savedState.startX;
        stopX=savedState.stopX;
        itemPosition=savedState.itemPosition;

        scroll(itemPosition,-1);
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    //保存tab视图状态的辅助类，实现parcelable接口
    static class SavedState extends BaseSavedState{
        float startX;
        float stopX;
        int itemPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            float[] floats=new float[2];
            source.readFloatArray(floats);
            startX=floats[0];
            stopX=floats[1];
            itemPosition=source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            float[] floats=new float[]{startX,stopX};
            out.writeFloatArray(floats);
            out.writeInt(itemPosition);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                =new Parcelable.ClassLoaderCreator<SavedState>(){

            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

            @Override
            public SavedState createFromParcel(Parcel source, ClassLoader loader) {
                return null;
            }
        };

    }
}
