package com.recycleview.client.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.recycleview.client.R;

import java.util.Date;

/**
 * 下拉刷新列表
 * Created by Hongmingwei on 2017/7/12.
 * Email: 648600445@qq.com
 */

public class RecyclerViewHeader extends LinearLayout {

    private LinearLayout mContainer;//布局指向
    private Context mContext;

    private ImageView mArrowImageView;//箭头
    private ImageView mProgressBar;//刷新图标
    private TextView mStatusTextView;//状态文字
    private TextView mHeaderTimeView;//刷新时间文本

    public final static int STATE_NORMAL = 0;// 初始状态
    public final static int STATE_RELEASE_TO_REFRESH = 1;	// 释放刷新
    public final static int STATE_REFRESHING = 2;	// 正在刷新
    private int mState = STATE_NORMAL;  // 当前状态（临时保存用）
    //箭头指向动画
    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;
    //匀速旋转动画
    private RotateAnimation rotateAnimation;

    private final int ROTATE_ANIM_DURATION = 180;//旋转的角度
    public int mMeasuredHeight;//布局的原始高度，用来做状态改变标志

    //用来存储当前下拉刷新的时间，这个时间的存储以后可以交给activity来完成
    private static final String STORAGE_DATE = "storage_date";
    private static final String STORAGE_TIME = "storage_time";

    public RecyclerViewHeader(Context context) {
        super(context);
        initView(context);
    }

    public RecyclerViewHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecyclerViewHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context){
        mContext = context;
        mContainer = (LinearLayout) ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_header, null);
        LayoutParams mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mLayoutParams.setMargins(0, 0, 0, 0);
        this.setLayoutParams(mLayoutParams);
        this.setPadding(0, 0, 0, 0);

        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, 0));
        setGravity(Gravity.BOTTOM);

        mArrowImageView = (ImageView) findViewById(R.id.listview_header_arrow);
        mProgressBar = (ImageView) findViewById(R.id.listview_header_progressbar);
        mStatusTextView = (TextView) findViewById(R.id.refresh_status_list);
        mHeaderTimeView = (TextView) findViewById(R.id.last_refresh_time);

        //下拉刷新设置箭头动画效果
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);//设置动画旋转角度
        mRotateUpAnim.setFillAfter(true);//设置动画结束后不变
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);

        //匀速旋转动画
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(mContext, R.anim.rotating);
        LinearInterpolator interpolator = new LinearInterpolator();
        rotateAnimation.setInterpolator(interpolator);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
    }

    /**
     * 设置状态
     * @param state
     */
    public void setState(int state){
        if (state == mState){
            return;
        }
        //判断显示
        switch (state){
            case STATE_NORMAL:
            case STATE_RELEASE_TO_REFRESH:
                mProgressBar.clearAnimation();
                mArrowImageView.setVisibility(VISIBLE);
                mProgressBar.setVisibility(INVISIBLE);
                break;
            case STATE_REFRESHING:
                mArrowImageView.clearAnimation();
                mArrowImageView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(VISIBLE);
                break;
            default:
                break;
        }
        //判断动画的添加
        switch (state){
            case STATE_NORMAL:
                if (mState == STATE_RELEASE_TO_REFRESH){
                    mArrowImageView.startAnimation(mRotateDownAnim);
                }
                if (mState == STATE_REFRESHING){
                    mArrowImageView.clearAnimation();
                }
                mStatusTextView.setText(R.string.pull_to_refresh);
                break;
            case STATE_RELEASE_TO_REFRESH:
                if (mState != STATE_RELEASE_TO_REFRESH){
                    mArrowImageView.clearAnimation();
                    mArrowImageView.startAnimation(mRotateUpAnim);
                    mStatusTextView.setText(R.string.release_to_refresh);
                }
                break;
            case STATE_REFRESHING:
                mProgressBar.startAnimation(rotateAnimation);
                mStatusTextView.setText(R.string.refreshing);
                break;
            default:
                break;
        }
        mState = state;
    }
    /**
     * 返回当前状态
     */
    public int getState(){
        return mState;
    }

    //完成刷新
    public void refreshComplate(){
        //注释的是时间文本  有需要可以去掉  也是在布局里改可见属性
//        SharedPreferences sp = mContext.getSharedPreferences(STORAGE_DATE, Context.MODE_APPEND);
//        long time = sp.getLong(STORAGE_TIME, System.currentTimeMillis());
//        mHeaderTimeView.setText(friendlyTime(time));
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    /**
     * 刷新头滑动改变
     * @param dalta
     */
    public void onMove(float dalta){
        if (getVisiableHeight() > 0 || dalta > 0){
            setVisiableHeight((int) dalta + getVisiableHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH){ //未处于刷新状态，更新箭头
                if (getVisiableHeight() > mMeasuredHeight){
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }

    public boolean releaseAction(){
        boolean isOnRefresh = false;
        int height = getVisiableHeight();
        if (height == 0){
            isOnRefresh = false;
        }
        //刷新时改变状态
        if (getVisiableHeight() > mMeasuredHeight && mState < STATE_REFRESHING){
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        //刷新时回滚到原始高度
        int destHeight = 0;
        if (mState == STATE_REFRESHING){
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);
        return isOnRefresh;
    }

    /**
     * 回滚到顶部
     * @param destHeight
     */
    private void smoothScrollTo(int destHeight){
        ValueAnimator animator = ValueAnimator.ofInt(getVisiableHeight(), destHeight);
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
     * 设置刷新头可见高度
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

    /**
     * 设置下拉刷新的时间
     * @param time
     * @return
     */
    public String friendlyTime(long time){
        long timeMillis = System.currentTimeMillis();
//        SharedPreferences sp = mContext.getSharedPreferences(STORAGE_DATE, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putLong(STORAGE_TIME, timeMillis);
//        editor.commit();

        //获取time距离当前的秒数
        int ct = (int)((timeMillis - time) / 1000);

        if (ct == 0){
            return "刚刚";
        }
        if (ct > 0 && ct < 60){
            return ct + "秒前";
        }
        if (ct >= 60 && ct < 3600){
            return Math.max(ct / 60, 1) + "分钟前";
        }
        if(ct >= 3600 && ct < 86400)
            return ct / 3600 + "小时前";
        if(ct >= 86400 && ct < 2592000){ //86400 * 30
            int day = ct / 86400 ;
            return day + "天前";
        }
        if(ct >= 2592000 && ct < 31104000) { //86400 * 30
            return ct / 2592000 + "月前";
        }
        return ct / 31104000 + "年前";
    }

}
