package com.recycleview.client.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.recycleview.client.R;
import com.recycleview.client.util.DefaultImageTools;
import com.recycleview.client.util.ImageUtils;
import com.recycleview.client.util.StringUtils;

/**
 * 出错显示页面
 * Created by Hongmingwei on 2017/7/11.
 * Email: 648600445@qq.com
 */

public class ErrorView extends RelativeLayout implements View.OnClickListener {
    /**
     * params
     */
    private Context mContext;

    private LinearLayout mTextLayout;
    private ImageView mIconImage;
    private TextView mTitleText;
    private TextView mSubTitleText;
    private TextView mRetryTitleText;

    private LinearLayout mPregrossLayout;
    private TextView mPregrossText;

    private int mStatus;

    private static final int STATUS_GONE = 0;
    private static final int STATUS_EMPTY = 1;
    private static final int STATUS_LOADING = 2;
    private static final int STATUS_ERROR = 3;

    private OnClickListener mEmptyClickListener;
    private OnClickListener mRetryClickListener;

    // icon 资源
    private int mIconResId = 0;


    public ErrorView(Context context) {
        super(context);
        initView(context);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.error_layout, this);
        mTextLayout = (LinearLayout) findViewById(R.id.textLayout);
        mIconImage = (ImageView) findViewById(R.id.icon);
        mTitleText = (TextView) findViewById(R.id.title);
        mSubTitleText = (TextView) findViewById(R.id.subTitle);
        mRetryTitleText = (TextView) findViewById(R.id.retryTitle);

        mPregrossLayout = (LinearLayout) findViewById(R.id.progressLayout);
        mPregrossText = (TextView) findViewById(R.id.progressTitle);

        hide();
        mRetryTitleText.setOnClickListener(this);

        mTextLayout.setOnClickListener(mEmptyListener);
    }

    /**
     * 没有网络的情况下显示
     * @param errorSubTitle
     */
    public void setErrorStatus(String errorSubTitle){
        show();
        mPregrossLayout.setVisibility(GONE);
        mTextLayout.setVisibility(VISIBLE);
        mTitleText.setVisibility(GONE);
        mIconImage.setImageBitmap(DefaultImageTools.getNoticeErrorBitmap(mContext));

        if (StringUtils.isNotBlank(errorSubTitle)){
            mSubTitleText.setVisibility(VISIBLE);
            mSubTitleText.setText(errorSubTitle);
        } else {
            mSubTitleText.setVisibility(GONE);
        }
        mRetryTitleText.setVisibility(VISIBLE);
        mStatus = STATUS_ERROR;
    }

    /**
     * 提示用户网络链接失败
     */
    public void setErrorStatus(){
        String errorTitle = mContext.getString(R.string.hint_network_error);
        setErrorStatus(errorTitle);
    }

    public void setErrorStatus(int resId){
        String errorTitle = mContext.getString(resId);
        setErrorStatus(errorTitle);
    }


    /**
     *  设置Loading显示的文字
     * @param loadingText
     */
    public void setLoadingStatus(String loadingText){
        show();
        mPregrossLayout.setVisibility(VISIBLE);
        mTextLayout.setVisibility(GONE);
        if (StringUtils.isNotBlank(loadingText)) {
            mPregrossText.setVisibility(VISIBLE);
            mPregrossText.setText(loadingText);
        } else {
            mPregrossText.setVisibility(GONE);
        }
        mStatus = STATUS_LOADING;

    }
    public void setLoadingStatus(int resId){
        String loadingText = mContext.getString(resId);
        setLoadingStatus(loadingText);
    }

    public void setLoadingStatus(){
        setLoadingStatus(mContext.getString(R.string.refreshing));
    }


    /**
     * 设置加载错误
     * @param emptyText
     */
    public void setEmptyStatus(String emptyText){
        show();
        mPregrossLayout.setVisibility(GONE);
        mTextLayout.setVisibility(VISIBLE);
        mTitleText.setVisibility(VISIBLE);
        mTitleText.setText(emptyText);
        mSubTitleText.setVisibility(GONE);
        mIconImage.setImageBitmap(getEmptyBitmap(mContext));
        mRetryTitleText.setVisibility(GONE);
        mStatus = STATUS_EMPTY;
    }

    /**
     * 没有数据的时候显示图片
     * @param context
     * @return
     */
    private Bitmap getEmptyBitmap(Context context){
        if (mIconResId == 0){
            return DefaultImageTools.getNoticeEmptyBitmap(mContext);
        } else {
            return ImageUtils.getBitmapFormRes(mContext, mIconResId);
        }
    }

    public void setEmptyStatus(int resId){
        String emptyText = mContext.getString(resId);
        setEmptyStatus(emptyText);
    }

    public void setEmptyStatus() {
        setEmptyStatus(mContext.getString(R.string.hint_empty_list));
    }

    /**
     * 设置显示
     */
    private void show(){
        if (getVisibility() != VISIBLE){
            setVisibility(VISIBLE);
        }
    }

    /**
     * 设置隐藏
     */
    private void hide(){
        if (getVisibility() != GONE){
            setVisibility(GONE);
        }
        mStatus = STATUS_GONE;
    }

    /**
     * 当出现错误时，点击回掉
     * @param listener
     */
    public void setOnRetryClickListener(OnClickListener listener){
        mRetryClickListener = listener;
    }

    /**
     * 数据为空时，点击回掉
     */
    public void setOnEmptyClickListener(OnClickListener listener){
        mEmptyClickListener = listener;
    }

    private OnClickListener mEmptyListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mEmptyClickListener != null){
                mEmptyClickListener.onClick(v);
            }
        }
    };

    public int getIconResId(){
        return mIconResId;
    }

    public void setIconResId(int mIconResId){
        this.mIconResId = 0;
        if (mIconResId != -1){
            this.mIconResId = mIconResId;
        }
    }


    @Override
    public void onClick(View v) {
        if (v == null || mStatus != STATUS_ERROR || mRetryClickListener == null){
            return;
        }
        setLoadingStatus();
        if (R.id.retryTitle == v.getId()){
            mRetryClickListener.onClick(v);
        }
    }
}
