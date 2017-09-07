package com.recycleview.client.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * Created by Hongmingwei on 2017/9/4.
 * Email: 648600445@qq.com
 */

public class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Bitmap getBitmapFormRes(Context context, int resId){
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            bitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        } catch (OutOfMemoryError e){
            Log.e(TAG, "getBitmapFormRes: =========", e);
        }
        return bitmap;
    }

}
