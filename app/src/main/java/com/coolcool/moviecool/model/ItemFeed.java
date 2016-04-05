package com.coolcool.moviecool.model;

/**
 * Created by yanshili on 2016/3/24.
 */
public class ItemFeed {

    private int type;
    private Object itemData;

    public ItemFeed(Object itemData,int type) {
        this.itemData = itemData;
        this.type = type;
    }

    public <T extends Object> T getData(){
        return (T) itemData;
    }

    public int getItemType() {
        return type;
    }

}
