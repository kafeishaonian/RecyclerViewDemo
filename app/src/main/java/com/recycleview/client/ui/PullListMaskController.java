package com.recycleview.client.ui;

import android.view.View;

/**
 * Created by Hongmingwei on 2017/9/4.
 * Email: 648600445@qq.com
 */

public class PullListMaskController {

    private static final String TAG = PullListMaskController.class.getSimpleName();

    public enum ListViewState {
        /** 首次加载，正在加载 */
        EMPTY_LOADING,
        /** 首次加载，没有网络重试 */
        EMPTY_RETRY,
        /** 首次加载，没有数据 */
        EMPTY_BLANK,
        /** 正常显示List,有更多数据 */
        LIST_NORMAL_HAS_MORE,
        /** 列表，重新刷新数据,强制显示HeadView的正在刷新 ,并且调用回调onRefresh */
        LIST_REFRESHING_AND_REFRESH,
        /** 下拉刷新完成，收起下拉HeadView */
        LIST_REFRESH_COMPLETE,
        /** 加载更多数据完成，收起上拉Footer*/
        LIST_LOAD_MORE,
        /** 列表,没有更多数据 */
        LIST_NO_MORE,
        /** 列表,出错重试页面 */
        LIST_RETRY,
        /** 列表，设置不可以下拉刷新 */
        LIST_NO_HEADER;
    }

    private final RecyclerListView mListView;
    private final ErrorView mErrorView;

    private View.OnClickListener mRetryClickListener;
    private View.OnClickListener mEmptyClickListener;
    private RecyclerListView.LoadingRefreshListener mRefreshListener;
    private RecyclerListView.LoadingLoadMoreListener mFootViewListener;

    public PullListMaskController(RecyclerListView listView, ErrorView errorView){
        this.mListView = listView;
        this.mErrorView = errorView;
        initListener();
    }

    private void initListener(){
        mErrorView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRetryClickListener != null){
                    mRetryClickListener.onClick(v);
                }
            }
        });
        mErrorView.setOnEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmptyClickListener != null){
                    mErrorView.setLoadingStatus();
                    mEmptyClickListener.onClick(v);
                }
            }
        });

        mListView.setLoadingRefreshListener(new RecyclerListView.LoadingRefreshListener() {
            @Override
            public void onRefresh() {
                if (mRefreshListener != null){
                    mRefreshListener.onRefresh();
                }
            }
        });

        mListView.setLoadingLoadMoreListener(new RecyclerListView.LoadingLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (mFootViewListener != null){
                    mFootViewListener.onLoadMore();
                }
            }
        });
    }

    /**
     * 在出现错误时，点击回调
     *
     * @param listener
     */
    public void setOnRetryClickListener(View.OnClickListener listener) {
        mRetryClickListener = listener;
    }

    public void setOnEmptyClickListener(View.OnClickListener listener){
        mEmptyClickListener = listener;
    }

    /**
     * 1、拖动ListView的下拉刷新，松开后，调用该回调 2、点击Mask页面的点击刷新按钮
     *
     * @param onRefreshListener
     */
    public void setOnRefreshListener(RecyclerListView.LoadingRefreshListener onRefreshListener) {
        mRefreshListener = onRefreshListener;
    }

    /**
     * 1、下拉到底部的时候自动调用 2、List底部点击重试后调用
     *
     * @param listener
     */
    public void setOnLoadMoreListener(RecyclerListView.LoadingLoadMoreListener listener) {
        mFootViewListener = listener;
    }

    public void showViewStatus(ListViewState state){
        if (mListView == null || mErrorView == null){
            return;
        }

        switch (state){
            case EMPTY_LOADING: {
                //首次加载，正在刷新
                mListView.setVisibility(View.INVISIBLE);
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.setLoadingStatus();
                break;
            }
            case EMPTY_RETRY:{
                //没有网络重试页面
                mListView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.setErrorStatus();
                break;
            }
            case EMPTY_BLANK:{
                //数据没有数据
                mListView.setVisibility(View.GONE);
                mErrorView.setVisibility(View.VISIBLE);
                mErrorView.setEmptyStatus();
                break;
            }
            case LIST_NORMAL_HAS_MORE:{
                //有更多数据，设置可以上拉刷新
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setLoadingMoreEnabled(true);
                break;
            }
            case LIST_REFRESH_COMPLETE: {
                //下拉刷新完成，回收Header
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.refreshComplete();
                break;
            }
            case LIST_LOAD_MORE: {
                //上拉加载完成，回收footer
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.loadMoreComplete();
                break;
            }
            case LIST_NO_MORE: {
                //没有更多数据，隐藏上拉刷新
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setLoadingMoreEnabled(false);
                break;
            }
            case LIST_NO_HEADER:{
                //设置不可下拉刷新
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.setPullRefreshEnabled(false);
                break;
            }
            case LIST_REFRESHING_AND_REFRESH:{
                mErrorView.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
                mListView.addHeaderView();
            }
        }
    }

}
