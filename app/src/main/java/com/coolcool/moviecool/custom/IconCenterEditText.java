package com.coolcool.moviecool.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.coolcool.moviecool.R;

/**
 * <View
 android:focusable="true"
 android:focusableInTouchMode="true"
 android:layout_width="0px"
 android:layout_height="0px"/>
 * 如果想要该editText自动获取和失去焦点，在相同布局中添加一个上面的可获取焦点的视图即可
 * Created by yanshili on 2016/4/1.
 */
public class IconCenterEditText extends EditText implements View.OnFocusChangeListener, View.OnKeyListener {
    private static final String TAG = IconCenterEditText.class.getSimpleName();
    /**
     * 是否是默认图标再左边的样式
     */
    private boolean isLeft = false;
    /**
     * 是否点击软键盘搜索
     */
    private boolean pressSearch = false;
    /**
     * 软键盘搜索键监听
     */
    private OnSearchClickListener listener;

    private boolean hasText=false;
    //清除文本内容的 标志
//    private Drawable mClearDrawable = (VectorDrawableCompat) getResources().getDrawable(R.drawable.ic_clear_24dp);
    private Drawable mClearDrawable=VectorDrawableCompat
            .create(getResources(), R.drawable.ic_clear_24dp, null);
   //控件是否有焦点
    private boolean hasFocus;

    public void setOnSearchClickListener(OnSearchClickListener listener) {
        this.listener = listener;
    }

    public IconCenterEditText(Context context) {
        this(context, null);
        mContext=context;
        init();
    }

    public IconCenterEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
        mContext=context;
        init();
    }

    public IconCenterEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
        init();
    }

    Context mContext;
    private void init() {
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(mTextWatcher);

        //设置焦点改变的监听
        setOnFocusChangeListener(this);
        setOnKeyListener(this);
    }

    //设置输入框里面内容发生改变的监听
    TextWatcher mTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            hasText=text.length()>0;

            if (hasFocus) {
                setClearIconVisible(hasText);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //监听触摸操作，如果点击范围在清楚按钮范围内就删除文本
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClearDrawable != null
                && event.getAction() == MotionEvent.ACTION_UP
                &&hasFocus
                &&hasText) {

            int x = (int) event.getX();
            //判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight())) &&
                    (x < (getWidth() - getPaddingRight()));
            //获取删除图标的边界，返回一个Rect对象
            Rect rect = mClearDrawable.getBounds();
            //获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            //计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;
            //判断触摸点是否在竖直范围内(可能会有点误差)
            //触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerHeight && isInnerWidth) {
                this.setText("");
            }
        }

        InputMethodManager imm =
                (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return super.onTouchEvent(event);
    }



    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    private void setClearIconVisible(boolean visible) {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable =VectorDrawableCompat
                .create(getResources(), R.drawable.ic_clear_24dp, null);
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());

        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1],
                right, getCompoundDrawables()[3]);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (isLeft) { // 如果是默认样式，则直接绘制
            super.onDraw(canvas);
        } else { // 如果不是默认样式，需要将图标绘制在中间
            Drawable[] drawables = getCompoundDrawables();
            Drawable drawableLeft = drawables[0];
            Drawable drawableRight = drawables[2];
            translate(drawableLeft, canvas);
            translate(drawableRight, canvas);
            super.onDraw(canvas);
        }

    }

    public void translate(Drawable drawable, Canvas canvas) {
        if (drawable != null) {
            float textWidth = getPaint().measureText(getHint().toString());
            int drawablePadding = getCompoundDrawablePadding();
            int drawableWidth = drawable.getIntrinsicWidth();
            float bodyWidth = textWidth + drawableWidth + drawablePadding;
            if (drawable == getCompoundDrawables()[0]) {
                canvas.translate((getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2, 0);
            } else {
                setPadding(getPaddingLeft(), getPaddingTop(), (int)(getWidth() - bodyWidth - getPaddingLeft()), getPaddingBottom());
                canvas.translate((getWidth() - bodyWidth - getPaddingLeft()) / 2, 0);
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus=hasFocus;
        if (hasFocus&&hasText) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        Log.d(TAG, "onFocusChange execute");
        // 恢复EditText默认的样式
        if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
            isLeft = hasFocus;
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
        if (pressSearch && listener != null) {
            /*隐藏软键盘*/
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            if (event.getAction() == KeyEvent.ACTION_UP) {
                listener.onSearchClick(v);
            }
        }
        return false;
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.startAnimation(shakeAnimation(5));
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }


    public interface OnSearchClickListener {
        void onSearchClick(View view);
    }

}
