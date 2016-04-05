package com.coolcool.moviecool.model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by yanshili on 2016/4/4.
 */
public class OrdinaryUser extends BmobUser{

    public OrdinaryUser(Context context) {

    }

    //收藏的电影
    private List<Long> favoriteIdList =new ArrayList<>();
    //收藏的电影
    private BmobRelation favorites;

    public BmobRelation getFavorites() {
        return favorites;
    }

    public void setFavorites(BmobRelation favorites) {
        this.favorites = favorites;
    }



    public List<Long> getFavoriteIdList() {
        return favoriteIdList;
    }

    public void setFavoriteIdList(List<Long> favoriteIdList) {
        this.favoriteIdList = favoriteIdList;
    }

    @Override
    public void signUp(Context context, SaveListener listener) {
//        BmobRole ordinaryUser=new BmobRole("ordinaryUser");
//        ordinaryUser.getUsers().add(this);
//        ordinaryUser.save(context);
        super.signUp(context, listener);
    }

}
