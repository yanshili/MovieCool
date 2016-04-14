package com.coolcool.moviecool.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.base.BaseActivity;
import com.coolcool.moviecool.utils.TextViewUtils;
import com.coolcool.moviecool.utils.TintDrawableUtil;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    private ImageView ivArrowMark;
    private TextView tvAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initView();
    }

    private void initView() {
        tvAbout= (TextView) findViewById(R.id.tvAbout);
        SpannableString sp= TextViewUtils.getSPAboutText(this);
        tvAbout.setText(sp);
        tvAbout.setMovementMethod(LinkMovementMethod.getInstance());
        ivArrowMark= (ImageView) findViewById(R.id.ivArrowMark);

        if (ivArrowMark!=null){
            ivArrowMark.setOnClickListener(this);
            Drawable drawable= TintDrawableUtil
                    .tintDrawable(this, R.drawable.ic_arrow_back_24dp, R.color.colorWhite);
            ivArrowMark.setImageDrawable(drawable);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivArrowMark:
                super.onBackPressed();
                break;
        }    }
}
