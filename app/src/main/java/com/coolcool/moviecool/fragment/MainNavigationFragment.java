package com.coolcool.moviecool.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.fragment.common.StateFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainNavigationFragment extends StateFragment {


    public MainNavigationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_main_navigation, container, false);


        return view;
    }

}
