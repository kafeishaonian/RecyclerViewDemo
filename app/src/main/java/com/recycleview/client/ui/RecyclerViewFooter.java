package com.recycleview.client.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.recycleview.client.R;

/**
 * 加载更多
 * Created by Hongmingwei on 2017/7/12.
 * Email: 648600445@qq.com
 */

public class RecyclerViewFooter extends LinearLayout {

    private LinearLayout mContainer;//布局指向
    private Context mContext;

    private ImageView mProgressBar;  // 正在刷新的图标

    public final static int STATE_LOADING = 0; //正在加载
    public final static int STATE_COMPLETE = 1;  //加载完成

    //设置旋转动画
    private RotateAnimation mRotateAnimation;


    public RecyclerViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public RecyclerViewFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecyclerViewFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        mContainer = (LinearLayout) ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, null);
        LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(0, 0, 0, 0);
        this.setLayoutParams(mLayoutParams);
        this.setPadding(0, 0, 0, 0);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);

        mProgressBar = (ImageView) findViewById(R.id.listview_foot_progerss);
        mRotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotating);
        LinearInterpolator mInterpolator = new LinearInterpolator();
        mRotateAnimation.setInterpolator(mInterpolator);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setState(int start){
        switch (start){
            case STATE_LOADING:
                this.setVisibility(VISIBLE);
                mProgressBar.startAnimation(mRotateAnimation);
                break;
            case STATE_COMPLETE:
                mProgressBar.clearAnimation();
                this.setVisibility(GONE);
                break;
            default:
                break;
        }
    }
}
