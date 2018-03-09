package com.recycleview.client.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import java.util.ArrayList;

/**
 * Created by Hongmingwei on 2017/12/19.
 * Email: 648600445@qq.com
 */

public class RecyclerScrollView extends ScrollView {

    private Context mContext;
    // 头部刷新的View
    private RecyclerViewHeader mHeader;
    //传入头文件列表
    private ArrayList<View> mHeaderViews = new ArrayList<>();//传入的头文件列表
    // 容器布局，因为scroll只允许嵌套一个子布局
    private LinearLayout mScrollContainer = null;
    private boolean pullRefreshEnabled = true;//刷新状态
    private float mLastY = -1;//记录的Y轴坐标
    private static final float DRAG_RATE = 3;//滑动阻率
    private LoadingRefreshListener mLoadingRefreshListener;//滑动监听

    // 辅助滑动
    private Scroller mAssistScroller;

    public RecyclerScrollView(Context context) {
        super(context);
        init(context);
    }

    public RecyclerScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RecyclerScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        this.mContext = context;
        mContext = context;
        if (pullRefreshEnabled){
            RecyclerViewHeader recyclerViewHeader = new RecyclerViewHeader(mContext);
            mHeaderViews.add(0, recyclerViewHeader);
            mHeader = recyclerViewHeader;

            LinearLayout.LayoutParams headerViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mScrollContainer = new LinearLayout(mContext);
            mScrollContainer.addView(mHeader, headerViewParams);
            mScrollContainer.setOrientation(LinearLayout.VERTICAL);
            this.removeAllViews();
            addView(mScrollContainer);
        }
        mAssistScroller = new Scroller(mContext);
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
                if (getScrollY() == 0 && deltaY > 0 && isOnTop() && pullRefreshEnabled) {
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
                if (getScrollY() == 0 && isOnTop() && pullRefreshEnabled) {
                    if (mHeader.releaseAction()) {
                        if (mLoadingRefreshListener != null) {
                            mLoadingRefreshListener.onRefresh();
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

    @Override
    public void computeScroll() {
        if(mAssistScroller.computeScrollOffset()) {
            mHeader.refreshComplate();
            //继续重绘
            postInvalidate();
        }
        super.computeScroll();
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        // 2.重载addView(View child, android.view.ViewGroup.LayoutParams params)方法
        // 解决 java.lang.IllegalStateException
        // 因为scrollView只许添加一个子布局，如果在xml中添加子布局，那么肯定会throw
        // java.lang.IllegalStateException:ScrollView can host only one direct child
        // 所以必须override这个方法，自己查看源码做对应的修改
        this.removeAllViews();
        mScrollContainer.addView(child, params);
        super.addView(mScrollContainer, mScrollContainer.getLayoutParams());
    }

    /**
     * 监听接口
     * @param listener
     */
    public void setLoadingRefreshListener(LoadingRefreshListener listener){
        mLoadingRefreshListener = listener;
    }

    public interface LoadingRefreshListener{
        void onRefresh();
    }
}
