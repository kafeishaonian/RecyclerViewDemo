package com.zhihu.client.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhihu.client.R;

/**
 * Created by Administrator on 2017/5/8 0008.
 */
public class RecyclerListViewFooter extends LinearLayout {

    private LinearLayout mContainer;//布局指向
    private Context mContext;//上下文引用

    public final static int STATE_LOADING = 0; //正在加载
    public final static int STATE_COMPLETE = 1;  //加载完成

    private ImageView mProgressBar;    // 正在刷新的图标

    // 均匀旋转动画
    private RotateAnimation refreshingAnimation;

    public RecyclerListViewFooter(Context context) {
        super(context);
        initView(context);
    }

    public RecyclerListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecyclerListViewFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //初始化
    private void initView(Context context) {
        mContext=context;
        mContainer= (LinearLayout) LayoutInflater.from(context).inflate(R.layout.listview_footer,null);
        LayoutParams lp=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,0);
        this.setLayoutParams(lp);
        this.setPadding(0, 0, 0, 0);
        addView(mContainer, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        setGravity(Gravity.CENTER);

        mProgressBar= (ImageView) findViewById(R.id.listview_foot_progress);

        ///添加匀速转动动画
        refreshingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(
                context, R.anim.rotating);
        LinearInterpolator lir = new LinearInterpolator();
        refreshingAnimation.setInterpolator(lir);

        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void  setState(int state) {
        switch (state){
            case STATE_LOADING:
                this.setVisibility(VISIBLE);
                mProgressBar.setAnimation(refreshingAnimation);
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
