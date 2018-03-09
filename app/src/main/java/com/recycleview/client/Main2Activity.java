package com.recycleview.client;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.recycleview.client.ui.RecyclerScrollView;

/**
 * Created by Hongmingwei on 2017/12/19.
 * Email: 648600445@qq.com
 */

public class Main2Activity extends Activity{
    private static final String TAG = Main2Activity.class.getSimpleName();
    private RecyclerScrollView refresh_scrollview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
    }

    private void initView() {
        refresh_scrollview = (RecyclerScrollView) findViewById(R.id.refresh_scrollview);
        refresh_scrollview.setPullRefreshEnabled(true);
        refresh_scrollview.setLoadingRefreshListener(new RecyclerScrollView.LoadingRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(){
                    public void run() {
                        try {
                            new Thread().sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            public void run() {
                                refresh_scrollview.refreshComplete();
                            }
                        });
                    };
                }.start();
            }
        });
    }
}
