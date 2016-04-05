package com.coolcool.moviecool.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.activity.base.SearchBaseActivity;
import com.coolcool.moviecool.utils.TintDrawableUtil;
import com.coolcool.moviecool.custom.IconCenterEditText;
import com.coolcool.moviecool.fragment.SearchFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends SearchBaseActivity implements View.OnClickListener {

    private IconCenterEditText searchEditText;
    private FragmentManager mFragmentManager;
    private SearchFragment mSearchFragment;

    private ImageView ivArrowMark;
    private TextView tvSearchLabel;
    private Spinner mSpinner;
    private ArrayAdapter<String> spAdapter;
    private String searchColumn="name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        iniView();

        initSpinner();

        initFragment();
    }

    public void iniView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinner= (Spinner) findViewById(R.id.spSearchType);
        ivArrowMark=(ImageView)findViewById(R.id.ivArrowMark);
        tvSearchLabel =(TextView)findViewById(R.id.tvSearchLabel);
        if (ivArrowMark!=null){
            Drawable drawable= TintDrawableUtil
                    .tintDrawable(this, R.drawable.ic_arrow_back_24dp, R.color.colorWhite);
            ivArrowMark.setImageDrawable(drawable);
            ivArrowMark.setOnClickListener(this);
        }

        if (tvSearchLabel!=null)
            tvSearchLabel.setOnClickListener(this);

        searchEditText = (IconCenterEditText) findViewById(R.id.centerSearchEditText);

        // 实现TextWatcher监听即可
        searchEditText.setOnSearchClickListener(new IconCenterEditText.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                executeSearch();
            }
        });
    }

    private void initSpinner(){
        String[] arrays=getResources().getStringArray(R.array.search_criteria);
        List<String> list=new ArrayList<>();
        for (String s:arrays){
            list.add(s);
        }
        spAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list);
        spAdapter.setDropDownViewResource(R.layout.spinner_search_layout);
        mSpinner.setAdapter(spAdapter);
        mSpinner.setPrompt("搜索");
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchColumn=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    //执行搜索
    private void executeSearch(){
        String text=searchEditText.getText().toString();
        if (text.replaceAll(" ","").length()==0) {
            Toast.makeText(SearchActivity.this, "请输入具体内容"
                    , Toast.LENGTH_SHORT).show();
        } else {

            mSearchFragment= (SearchFragment) mFragmentManager.findFragmentByTag("SearchFragment");
            Log.i(SearchFragment.TAG,"mSearchFragment!=null"+(mSearchFragment!=null));
            if (mSearchFragment!=null){
                mSearchFragment.searchData(searchEditText.getText().toString(),searchColumn);
                Log.i(SearchFragment.TAG,"搜索内容==="+searchEditText.getText().toString());
            }
        }
    }

    public void initFragment(){
        if (mFragmentManager==null){
            mFragmentManager=getSupportFragmentManager();
        }
        mSearchFragment= (SearchFragment) mFragmentManager.findFragmentByTag("SearchFragment");
        if (mSearchFragment==null){
            mSearchFragment=new SearchFragment();
        }
        FragmentTransaction transaction=mFragmentManager.beginTransaction();
        transaction.replace(R.id.searchFragmentContainer,mSearchFragment,"SearchFragment");
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivArrowMark:
                super.onBackPressed();
                break;
            case R.id.tvSearchLabel:
                executeSearch();
                break;
        }

    }
}
