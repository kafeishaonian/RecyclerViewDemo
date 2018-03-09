package com.recycleview.client.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.icu.util.MeasureUnit;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
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

    private static final String TAG = RecyclerViewFooter.class.getSimpleName();

    private LinearLayout mContainer;//布局指向
    private Context mContext;

    private ImageView mProgressBar;  // 正在刷新的图标

    public final static int STATE_LOADING = 0; //正在加载
    public final static int STATE_COMPLETE = 1;  //加载完成
    public final static int STATE_NO_LOADING = 2; //没有更多数据

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
        mContainer = (LinearLayout) ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_footer, this, false);
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
//                this.setPadding(0, 0, 0,  0); //下拉结束后的动画的高度
                this.setVisibility(VISIBLE);
                mProgressBar.startAnimation(mRotateAnimation);
                break;
            case STATE_COMPLETE:
                mProgressBar.clearAnimation();
                this.setVisibility(GONE);
                this.setPadding(0, 0, 0, dip2px(-70)); //下次下拉的高度
//                setVisiableHeight(0);
                Log.e(TAG, "setState: ========" + getVisiableHeight());
                break;
//            case STATE_NO_LOADING:
//                setVisiableHeight(0);
//                this.setVisibility(GONE);
            default:
                break;
        }
    }

    public void move(int height){
        if (px2dip(height) <= 80) {
            this.setPadding(0, 0, 0, height); //下次下拉的高度
            this.setVisibility(VISIBLE);
        }
    }


//    private void init(){
//        mRotateAnimation
//    }

    private void smoothScrollTo(int startHeight, int stopHeight){
        int height = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getHeight();
        ValueAnimator animator = ValueAnimator.ofInt(height - startHeight, height - stopHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisiableHeight((Integer) animation.getAnimatedValue());
            }
        });
        animator.start();
    }


    /**
     * 设置布局可见高度
     * @param height
     */
    public void setVisiableHeight(int height){
        if (height < 0){
            height = 0;
        }
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        layoutParams.height = height;
        mContainer.setLayoutParams(layoutParams);
    }


    /**
     * 获取刷新头可见高度
     * @return
     */
    public int getVisiableHeight(){
        int height = 0;
        LayoutParams layoutParams = (LayoutParams) mContainer.getLayoutParams();
        height = layoutParams.height;
        return height;
    }

    public int px2dip(float pxValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public int dip2px(float dpValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
