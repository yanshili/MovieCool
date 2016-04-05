package com.coolcool.moviecool.holder;

import android.util.SparseArray;
import android.view.View;

/**
 * 用于复用listView的holder
 * Created by yanshili on 2016/2/27.
 */
public class ViewHolder {

    private final SparseArray<View> views;
    private View convertView;


    private ViewHolder(View convertView) {
        this.views = new SparseArray<View>();
        this.convertView = convertView;
        convertView.setTag(this);
    }

    public static ViewHolder getHolder(View convertView) {
        if (convertView.getTag() == null) {
            return new ViewHolder(convertView);
        }
        ViewHolder existedHolder = (ViewHolder) convertView.getTag();
        return existedHolder;
    }

    public <T extends View> T getView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = convertView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

}
