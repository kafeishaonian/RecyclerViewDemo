package com.recycleview.client.main3.widget;

/**
 * 监听移动事件接口
 * Created by Hongmingwei on 2018/2/5.
 * Email: 648600445@qq.com
 */

public interface ItemTouchHelperAdapter {

    /**
     *  移动的item监听
     * @param fromPosition  移动item起始位置
     * @param toPosition    移动item的结束位置
     * @return
     */
    boolean onItemMove(int fromPosition, int toPosition);

    /**
     * 没有准确的位置就回到初始位置
     * @param position 起始位置
     */
    void onItemDismiss(int position);

}
