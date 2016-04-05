package com.coolcool.moviecool.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.holder.CarouselViewHolder;
import com.coolcool.moviecool.holder.GridItemViewHolder;
import com.coolcool.moviecool.holder.LinearGridViewHolder;
import com.coolcool.moviecool.holder.RecyclerBaseViewHolder;
import com.coolcool.moviecool.holder.SingleImageHolder;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类展示的子页面里RecyclerView的适配器
 * Created by yanshili on 2016/3/26.
 */
public class SubRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG="SubRecyclerAdapter";
    public static final String SUB_RECYCLER_VIEW_DATA ="SUB_GRID_VIEW_DATA";

    //轮播图片的布局
    public static final int VIEW_TYPE_CAROUSEL=0;

    //可根据数据源个数自动改变行列布局的网格视图
    public static final int VIEW_TYPE_GRID=1;
    public static final int VIEW_TYPE_GRID_11=11;
    public static final int VIEW_TYPE_GRID_22=22;
    public static final int VIEW_TYPE_GRID_33=33;
    public static final int VIEW_TYPE_GRID_44=44;
    public static final int VIEW_TYPE_GRID_55=55;

    //有多少个数据，就可以在一行中展示多少张图片，长宽比固定，但是大小会随着数据的增多而减小
    public static final int VIEW_TYPE_SINGLE_IMAGE =2;
    //只有一列的长宽比为0.6的单张图片
    public static final int VIEW_TYPE_SINGLE_IMAGE_111 =111;
    //只有两列的长宽比为0.6的单张图片
    public static final int VIEW_TYPE_SINGLE_IMAGE_222 =222;
    //只有三列的长宽比为0.6的单张图片
    public static final int VIEW_TYPE_SINGLE_IMAGE_333 =333;
    //只有三列的长宽比为1.4的单张图片
    public static final int VIEW_TYPE_SINGLE_IMAGE_444 =444;

    //gridLayoutManager长宽比为1.4的单张图片
    public static final int VIEW_TYPE_GRID_VIEW =1111;




    private LayoutInflater mInflater;
    private Context mContext;
    private List<ItemFeed> mFeedList;

    public SubRecyclerAdapter(Context context, List<ItemFeed> feedList) {
        mContext = context;
        mFeedList = feedList;
        mInflater=LayoutInflater.from(context);
    }

    public SubRecyclerAdapter(Context context) {
        mContext = context;
        mFeedList = new ArrayList<>();
        mInflater=LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            //轮播视图
            case VIEW_TYPE_CAROUSEL:
                View carouselView=mInflater.inflate(R.layout.sub_item_carousel,parent,false);
                return new CarouselViewHolder(carouselView,mContext);

            //可根据数据源个数自动改变行列布局的网格视图
            case VIEW_TYPE_GRID_11:
                View gridView11=mInflater.inflate(R.layout.sub_item_grid,parent,false);
                return new LinearGridViewHolder(gridView11,mContext,2,true,3);

            case VIEW_TYPE_GRID_22:
                View gridView22=mInflater.inflate(R.layout.sub_item_grid,parent,false);
                return new LinearGridViewHolder(gridView22,mContext,3,true,4);

            case VIEW_TYPE_GRID_33:
                View gridView33=mInflater.inflate(R.layout.sub_item_grid,parent,false);
                return new LinearGridViewHolder(gridView33,mContext,2,true,5);

            case VIEW_TYPE_GRID_44:
                View gridView44=mInflater.inflate(R.layout.sub_item_grid,parent,false);
                return new LinearGridViewHolder(gridView44,mContext,3,false,6);

            case VIEW_TYPE_GRID_55:
                View gridView55=mInflater.inflate(R.layout.sub_item_grid,parent,false);
                return new LinearGridViewHolder(gridView55,mContext,3,true,7);

            case VIEW_TYPE_SINGLE_IMAGE_111:
                View singleView111=mInflater.inflate(R.layout.item_image_single,parent,false);
                return new SingleImageHolder(singleView111,mContext,1,0.6f);

            case VIEW_TYPE_SINGLE_IMAGE_222:
                View singleView222=mInflater.inflate(R.layout.item_image_single,parent,false);
                return new SingleImageHolder(singleView222,mContext,2,0.6f);

            case VIEW_TYPE_SINGLE_IMAGE_333:
                View singleView333=mInflater.inflate(R.layout.item_image_single,parent,false);
                return new SingleImageHolder(singleView333,mContext,3,0.6f);

            case VIEW_TYPE_SINGLE_IMAGE_444:
                View singleView444=mInflater.inflate(R.layout.item_image_single,parent,false);
                return new SingleImageHolder(singleView444,mContext,3,1.4f);

            //有多少个数据，就可以在一行中展示多少张图片，长宽比固定，但是大小会随着数据的增多而减小
            case VIEW_TYPE_SINGLE_IMAGE:
                View singleView=mInflater.inflate(R.layout.item_image_single,parent,false);
                return new SingleImageHolder(singleView,mContext);

            case VIEW_TYPE_GRID_VIEW:
                View singleGridView=mInflater.inflate(R.layout.item_grid_recycler_view,parent,false);
                return new GridItemViewHolder(singleGridView,mContext);
        }
        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RecyclerBaseViewHolder){
            ((RecyclerBaseViewHolder) holder).initData(mFeedList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mFeedList==null?0:mFeedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mFeedList==null) super.getItemViewType(position);
        int type=mFeedList.get(position).getItemType();
        if (type==VIEW_TYPE_GRID){
            ItemFeed itemFeed=mFeedList.get(position);
            Bundle mData=itemFeed.getData();
            List<MovieInfo> mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA);
            if (mMovieInfoList!=null&&mMovieInfoList.size()>0) {
                switch (mMovieInfoList.size()){
                    case 1:
                        type=VIEW_TYPE_SINGLE_IMAGE_111;
                        break;
                    case 2:
                        type=VIEW_TYPE_SINGLE_IMAGE_222;
                        break;
                    case 3:
                        type=VIEW_TYPE_GRID_11;
                        break;
                    case 4:
                        type=VIEW_TYPE_GRID_22;
                        break;
                    case 5:
                        type=VIEW_TYPE_GRID_33;
                        break;
                    case 6:
                        type=VIEW_TYPE_GRID_44;
                        break;
                    case 7:
                        type=VIEW_TYPE_GRID_55;
                        break;
                    default:
                        type=VIEW_TYPE_GRID_55;
                }
            }
        }
        if (type==VIEW_TYPE_SINGLE_IMAGE){
            ItemFeed itemFeed=mFeedList.get(position);
            Bundle mData=itemFeed.getData();
            List<MovieInfo> mMovieInfoList= (ArrayList<MovieInfo>) mData.getSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA);
            if (mMovieInfoList!=null&&mMovieInfoList.size()>0){
                switch (mMovieInfoList.size()){
                    case 1:
                        type=VIEW_TYPE_SINGLE_IMAGE_111;
                        break;
                    case 2:
                        type=VIEW_TYPE_SINGLE_IMAGE_222;
                        break;
                    case 3:
                        type=VIEW_TYPE_SINGLE_IMAGE_333;
                        break;
                    default:
                        type=VIEW_TYPE_SINGLE_IMAGE;
                        break;
                }
            }
        }
        return type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //添加单条数据
    public void addData(ItemFeed feed){
        mFeedList.add(feed);
    }

    //添加单条数据到指定位置
    public void addData(int position,ItemFeed feed){
        mFeedList.add(position,feed);
    }

    //一次添加多条数据
    public void addData( List<ItemFeed> feedList){
        mFeedList.addAll(feedList);
    }

    //一次添加多条数据
    public void clearData(){
        mFeedList.clear();
    }

}
