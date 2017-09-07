package com.recycleview.client.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Hongmingwei on 2017/7/12.
 * Email: 648600445@qq.com
 */

public class RecyclerListView extends RecyclerView {

    private Context mContext;
    private ArrayList<View> mHeaderViews = new ArrayList<>();//传入的头文件列表
    private RecyclerViewHeader mHeader;
    private RecyclerViewFooter mFooter;
    private Adapter mAdapter; //传入的Adapter
    private Adapter mWrapAdapter;//组合的Adapter
    private float mLastY = -1;//记录的Y轴坐标
    private LoadingRefreshListener mLoadingRefreshListener;//滑动监听
    private LoadingLoadMoreListener mLoadingLoadMoreListener;
    private static final float DRAG_RATE = 3;//滑动阻率


    private boolean pullRefreshEnabled = true;//刷新状态
    private boolean loadingMoreEnabled = true;//上拉状态

    private static final int TYPE_REFRESH_HEADER = -5;//添加刷新头
    private static final int TYPE_HEADER = -4;//添加头部
    private static final int TYPE_FOOTER = -3;//添加上拉加载布局
    private static final int TYPE_NORMAL = 0;
    private int previousTotal = 0;//记录ITEM条数

    public RecyclerListView(Context context) {
        this(context, null);
    }

    public RecyclerListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecyclerListView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        if (pullRefreshEnabled){
            RecyclerViewHeader recyclerViewHeader = new RecyclerViewHeader(mContext);
            mHeaderViews.add(0, recyclerViewHeader);
            mHeader = recyclerViewHeader;
        }
        mFooter = new RecyclerViewFooter(mContext);
        mFooter.setVisibility(GONE);
    }

    /**
     * 添加头文件的时候，判断有没有刷新头
     * @param view
     */
    public void addHeaderView(View view){
        if (pullRefreshEnabled && !(mHeaderViews.get(0) instanceof RecyclerViewHeader)){
            RecyclerViewHeader recyclerViewHeader = new RecyclerViewHeader(mContext);
            mHeaderViews.add(0, recyclerViewHeader);
            mHeader = recyclerViewHeader;
        }
        mHeaderViews.add(view);
    }

    /**
     * 上拉加载完成后，隐藏上拉加载布局
     */
    public void loadMoreComplete(){
        if (previousTotal < getLayoutManager().getItemCount()){
            mFooter.setState(RecyclerViewFooter.STATE_COMPLETE);
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    /**
     * 下拉刷新完成后的，隐藏下拉加载布局
     */
    public void refreshComplete(){
        mHeader.refreshComplate();
    }

    /**
     * 设置是否可以刷新
     * @param enabled
     */
    public void setPullRefreshEnabled(boolean enabled) {
        pullRefreshEnabled = enabled;
    }

    /**
     * 设置是否可以上拉
     */
    public void setLoadingMoreEnabled(boolean enabled) {
        loadingMoreEnabled = enabled;
        if (!enabled) {
            mFooter.setVisibility(GONE);
        }
    }


    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new WrapAdapter(mHeaderViews, mFooter, adapter);
        super.setAdapter(mWrapAdapter);
        mAdapter.registerAdapterDataObserver(mDataObserver);
    }

    /**
     * 监听活动，是否滑动到最低，用来加载
     * @param state
     */
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE && mLoadingLoadMoreListener != null && loadingMoreEnabled) {
            LayoutManager layoutManager = getLayoutManager();
            int lastVisibleItemPosition;  //最后可见的Item的position的值
            if (layoutManager instanceof GridLayoutManager) {   //网格布局的中lastVisibleItemPosition的取值
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {//瀑布流布局中lastVisibleItemPosition的取值
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {   //剩下只有线性布局（listview）中lastVisibleItemPosition的取值
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= layoutManager.getItemCount() - 1 && layoutManager.getItemCount() > layoutManager.getChildCount()
                    && mHeader.getState() < RecyclerViewHeader.STATE_REFRESHING) {

                mFooter.setState(RecyclerViewFooter.STATE_LOADING);

                mLoadingLoadMoreListener.onLoadMore();
            }
        }
    }

    /**
     * 监听手势活动  判断有没有到顶，用于刷新
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mLastY == -1) {
            mLastY = e.getRawY();
        }
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = e.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = e.getRawY() - mLastY;
                mLastY = e.getRawY();
                if (isOnTop() && pullRefreshEnabled) {
                    mHeader.onMove(deltaY / DRAG_RATE);
                    if (mHeader.getVisiableHeight() > 0 && mHeader.getState() < RecyclerViewHeader.STATE_REFRESHING) {
                        Log.i("getVisiableHeight", "getVisiableHeight = " + mHeader.getVisiableHeight());
                        Log.i("getVisiableHeight", " mHeader.getState() = " + mHeader.getState());
                        return false;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled) {
                    if (mHeader.releaseAction()) {
                        if (mLoadingRefreshListener != null) {
                            mLoadingRefreshListener.onRefresh();
                            previousTotal = 0;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(e);
    }

    /**
     * 判断是不是在顶部
     * @return
     */
    private boolean isOnTop(){
        if (mHeaderViews == null || mHeaderViews.isEmpty()){
            return false;
        }
        View view = mHeaderViews.get(0);
        if (view.getParent() != null){
            return true;
        } else {
            return false;
        }
    }


    /**
     * 流瀑布里面用到的计算公式
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions){
        int max = lastPositions[0];
        for (int value : lastPositions){
            if (value > max){
                max = value;
            }
        }
        return max;
    }

    /**
     * adapter数据观察者
     */
    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        // TODO: 2017/4/12 修改过（降低SDK版本）
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
        }
    };

    /**
     * 适配器重组
     */
    private class WrapAdapter extends RecyclerView.Adapter<ViewHolder> {

        private RecyclerView.Adapter adapter;

        private ArrayList<View> mHeaderViews;

        private RecyclerViewFooter mFootView;

        private int headerPosition = 1;

        public WrapAdapter(ArrayList<View> headerViews, RecyclerViewFooter footView, RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            this.mHeaderViews = headerViews;
            this.mFootView = footView;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (isHeader(position) || isFooter(position))
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            super.onViewAttachedToWindow(holder);
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null
                    && lp instanceof StaggeredGridLayoutManager.LayoutParams
                    && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }

        public boolean isHeader(int position) {
            return position >= 0 && position < mHeaderViews.size();
        }

        public boolean isFooter(int position) {
            return position < getItemCount() && position >= getItemCount() - 1;
        }

        public boolean isRefreshHeader(int position) {
            return position == 0;
        }

        public int getHeadersCount() {
            return mHeaderViews.size();
        }

        public int getFootersCount() {
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_REFRESH_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(0));
            } else if (viewType == TYPE_HEADER) {
                return new SimpleViewHolder(mHeaderViews.get(headerPosition++));
            } else if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(mFootView);
            }
            return adapter.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (isHeader(position)) {
                return;
            }
            int adjPosition = position - getHeadersCount();
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    adapter.onBindViewHolder(holder, adjPosition);
                    return;
                }
            }
        }

        @Override
        public int getItemCount() {
            if (adapter != null) {
                return getHeadersCount() + getFootersCount() + adapter.getItemCount();
            } else {
                return getHeadersCount() + getFootersCount();
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isRefreshHeader(position)) {
                return TYPE_REFRESH_HEADER;
            }
            if (isHeader(position)) {
                return TYPE_HEADER;
            }
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            int adjPosition = position - getHeadersCount();
            ;
            int adapterCount;
            if (adapter != null) {
                adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemViewType(adjPosition);
                }
            }
            return TYPE_NORMAL;
        }

        @Override
        public long getItemId(int position) {
            if (adapter != null && position >= getHeadersCount()) {
                int adjPosition = position - getHeadersCount();
                int adapterCount = adapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return adapter.getItemId(adjPosition);
                }
            }
            return -1;
        }

        @Override
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.unregisterAdapterDataObserver(observer);
            }
        }

        @Override
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            if (adapter != null) {
                adapter.registerAdapterDataObserver(observer);
            }
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }
    }

    /**
     * 监听接口
     * @param listener
     */
    public void setLoadingRefreshListener(LoadingRefreshListener listener){
        mLoadingRefreshListener = listener;
    }

    public void setLoadingLoadMoreListener(LoadingLoadMoreListener listener){
        mLoadingLoadMoreListener = listener;
    }

    public interface LoadingRefreshListener{
        void onRefresh();
    }

    public interface LoadingLoadMoreListener{
        void onLoadMore();
    }

}
