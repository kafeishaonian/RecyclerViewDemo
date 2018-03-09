package com.recycleview.client.main3.widget;

/**
 * Created by Hongmingwei on 2018/2/5.
 * Email: 648600445@qq.com
 */

public interface ItemTouchHelperViewHolder {

    /**
     * 开始滑动的时候调用
     */
    void onItemSelected();


    /**
     * 完成滑动的时候调用
     */
    void onItemClear();

}
