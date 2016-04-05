package com.coolcool.moviecool.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coolcool.moviecool.R;
import com.coolcool.moviecool.adapter.SubRecyclerAdapter;
import com.coolcool.moviecool.holder.LinearGridViewHolder;
import com.coolcool.moviecool.model.ItemFeed;
import com.coolcool.moviecool.model.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by yanshili on 2016/4/1.
 */
public class SearchFragment extends Fragment {
    public static final String TAG="SearchFragment";
    public static final String SEARCH_TEXT_DATA="SEARCH_TEXT_DATA";

    private Context mContext;
    private TextView tvPrompt;
    private RecyclerView searchRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SubRecyclerAdapter mAdapter;


    public SearchFragment() {}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext=context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_search,container,false);

        initView(view);

        return view;

    }

    private void initView(View view) {
        searchRecyclerView= (RecyclerView) view.findViewById(R.id.searchRecyclerView);
        tvPrompt= (TextView) view.findViewById(R.id.tvPrompt);

        mLayoutManager=new GridLayoutManager(mContext,3,GridLayoutManager.VERTICAL,false);
        mLayoutManager.setAutoMeasureEnabled(true);

        RecyclerView.ItemDecoration decoration=new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
//                outRect.top=(int) (8* VideoUtils.dp);
            }
        };

        searchRecyclerView.addItemDecoration(decoration);
        searchRecyclerView.setLayoutManager(mLayoutManager);

        searchRecyclerView.addOnScrollListener(mScrollListener);
    }


    String searchColumn;
    String searchText;
    /**
     * 开始搜索
     * @param searchText        要搜索的内容
     * @param searchColumn      要搜索的条件
     */
    public void searchData(String searchText,String searchColumn){
        this.searchText=searchText;
        this.searchColumn=searchColumn;
        pageCount=0;
        mAdapter=new SubRecyclerAdapter(mContext);
        searchRecyclerView.setAdapter(mAdapter);

        Log.i(TAG, "开始搜索： "+searchText);
        fetchNextPageData(pageCount);
    }

    int pageCount=0;
    public void fetchNextPageData(int pageCount) {


        if (searchText==null) return;
        if (mAdapter==null){
            mAdapter=new SubRecyclerAdapter(mContext);
        }

        BmobQuery<MovieInfo> query=new BmobQuery<>();
        query=processQuery(searchText,query);
        query.setLimit(15);
        query.setSkip(15 * pageCount);
        query.findObjects(mContext, new FindListener<MovieInfo>() {
            @Override
            public void onSuccess(List<MovieInfo> list) {
                Log.i(TAG, "电影个数： "+list.size());
                if (list.size() > 0) {
                    for (MovieInfo movie:list){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(LinearGridViewHolder.SUB_GRID_VIEW_DATA, movie);
                        ItemFeed feed = new ItemFeed(bundle
                                , SubRecyclerAdapter.VIEW_TYPE_GRID_VIEW);

                        searchRecyclerView
                                .setBackgroundColor(getResources().getColor(R.color.colorHoloGreen));
                        mAdapter.addData(feed);
                        mAdapter.notifyDataSetChanged();
                    }
                    if (list.size() < 15) {
                        Toast.makeText(mContext, "已经到最后一项了", Toast.LENGTH_SHORT).show();
                    }

                }else {
//                    tvPrompt.setText(R.string.search_prompt_nothing);
//                    searchRecyclerView
//                            .setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(mContext, "网络异常 "+i+": \n"+s, Toast.LENGTH_SHORT).show();
                searchRecyclerView
                        .setBackgroundColor(getResources().getColor(R.color.colorTransparent));
            }
        });


    }

    //根据不同页面建立不同的加载条件
    private BmobQuery<MovieInfo> processQuery(String queryText,BmobQuery<MovieInfo> query){

//        List<BmobQuery<MovieInfo>> list=new ArrayList<>();
//        BmobQuery<MovieInfo> query1=new BmobQuery<>();
//        query1.addWhereContains("name",queryText);
//        list.add(query1);
//        BmobQuery<MovieInfo> query2=new BmobQuery<>();
//        query2.addWhereContains("stars",queryText);
//        list.add(query2);
//        BmobQuery<MovieInfo> query3=new BmobQuery<>();
//        query3.addWhereContains("director", queryText);
//        list.add(query3);
//
//        query.or(list);
//        query.order("name");

        switch (searchColumn){
            case "电影":
                searchColumn="name";
                break;
            case "演员":
                searchColumn="stars";
                break;
            case "导演":
                searchColumn="director";
                break;
            default:
                searchColumn="name";
                break;
        }

        if (searchColumn.equals("name")){
            List<BmobQuery<MovieInfo>> list = new ArrayList<>();
            BmobQuery<MovieInfo> query1 = new BmobQuery<>();
            query1.addWhereContains("name", queryText);
            list.add(query1);
            BmobQuery<MovieInfo> query2 = new BmobQuery<>();
            query2.addWhereContains("intrinsicName", queryText);
            list.add(query2);
            BmobQuery<MovieInfo> query3 = new BmobQuery<>();
            query3.addWhereContains("title", queryText);
            list.add(query3);
            query.or(list);
        }else {
            query.addWhereContains(searchColumn, queryText);
        }

        query.order(searchColumn);

        return query;
    }

    RecyclerView.OnScrollListener mScrollListener=new RecyclerView.OnScrollListener() {
        int lastPosition =0;
        int totalCount =0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

            RecyclerView.LayoutManager layoutManager=recyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager){
                lastPosition =((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                totalCount =layoutManager.getItemCount();
            }

            if (layoutManager instanceof GridLayoutManager){
                lastPosition=((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            }

            switch (newState){
                case RecyclerView.SCROLL_STATE_IDLE:
                    if (lastPosition == totalCount -1){
                        pageCount++;
                        //已经到最后一项了
                        fetchNextPageData(pageCount);
                    }
                    break;
                case RecyclerView.SCROLL_STATE_DRAGGING:
                    break;
                case RecyclerView.SCROLL_STATE_SETTLING:
                    break;
            }
        }
    };

}
