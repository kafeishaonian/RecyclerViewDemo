package com.recycleview.client.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.recycleview.client.R;

/**
 * Created by Hongmingwei on 2017/9/1.
 * Email: 648600445@qq.com
 */

public class DefaultImageTools {
    /**
     * TAG
     */
    private static final String TAG = DefaultImageTools.class.getSimpleName();
    /**
     * params
     */
    private static Bitmap mNoticeErrorBitmap;
    private static Bitmap mNoticeEmptyBitmap;

    /**
     * 没网的情况下显示图片
     * @param context
     * @return
     */
    public static Bitmap getNoticeErrorBitmap(Context context){
        if (mNoticeErrorBitmap == null){
            mNoticeErrorBitmap = getBitmapFromRes(context, R.mipmap.icon_no_network);
        }
        return mNoticeErrorBitmap;
    }

    /**
     * 没有数据的情况下提示
     * @param context
     * @return
     */
    public static Bitmap getNoticeEmptyBitmap(Context context){
        if (mNoticeEmptyBitmap == null){
            mNoticeEmptyBitmap = getBitmapFromRes(context, R.mipmap.icon_empty);
        }
        return mNoticeEmptyBitmap;
    }


    public static Bitmap getBitmapFromRes(Context context, int resId){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        } catch (OutOfMemoryError e){
            Log.e(TAG, "getBitmapFromRes: "+ e.getMessage());
        }
        return bitmap;
    }

}
