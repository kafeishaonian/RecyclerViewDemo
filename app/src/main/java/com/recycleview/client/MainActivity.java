package com.recycleview.client;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.recycleview.client.adapter.RecyclerViewAdapter;
import com.recycleview.client.model.DataModel;
import com.recycleview.client.ui.ErrorView;
import com.recycleview.client.ui.PullListMaskController;
import com.recycleview.client.ui.RecyclerListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * view
     */
    private RecyclerListView mLsitView;
    private ErrorView mErrorView;
    private PullListMaskController mController;

    /**
     * params
     */
    private static final int MESSAGE_PARAM_INFO = 0x001;
    private static final int DELAYMILLIS = 200;
    private InnerHandler mHandler = new InnerHandler(this);
    private LinearLayoutManager manager;
    private ArrayList<DataModel> list = new ArrayList<>();
    private RecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initData();
    }


    private void initView(){
        mLsitView = (RecyclerListView) findViewById(R.id.listView);
        mErrorView = (ErrorView) findViewById(R.id.maskView);
        mController = new PullListMaskController(mLsitView, mErrorView);

        mLsitView.setLayoutManager(new LinearLayoutManager(this));
//        mLsitView.setLayoutManager(new GridLayoutManager(this, 2));
//        mLsitView.setLayoutManager(new StaggeredGridLayoutManager(2, RecyclerView.VERTICAL));
        adapter = new RecyclerViewAdapter(this);
        mLsitView.setAdapter(adapter);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Main3Activity.class));
                //首次加载
//                mController.showViewStatus(PullListMaskController.ListViewState.LIST_REFRESHING_AND_REFRESH);
            }
        });
    }

    private void initListener(){
        //首次加载
        mController.showViewStatus(PullListMaskController.ListViewState.EMPTY_LOADING);
//        mController.showViewStatus(PullListMaskController.ListViewState.LIST_NO_MORE);
        //下拉刷新
        mController.setOnRefreshListener(new RecyclerListView.LoadingRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "onRefresh: ==============");
                //避免出现重复请求
                mHandler.removeCallbacks(refreshRunnable);
                mHandler.postDelayed(refreshRunnable, DELAYMILLIS);
            }
        });
        //没有网络点击重试
        mController.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(retryRunnable);
                mHandler.postDelayed(retryRunnable, DELAYMILLIS);
            }
        });
        //没有数据点击重试
        mController.setOnEmptyClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(emptyRunnable);
                mHandler.postDelayed(emptyRunnable, DELAYMILLIS);
            }
        });

        //加载更多
        mController.setOnLoadMoreListener(new RecyclerListView.LoadingLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "onLoadMore: ==============");
                mHandler.removeCallbacks(loadMoreRunnable);
                mHandler.postDelayed(loadMoreRunnable, DELAYMILLIS);
            }
        });
    }

    /**
     * 模拟数据
     */
    private void initData(){
        try {
            Thread.sleep(1000);
            //headerView数据
            Log.e(TAG, "initData: ==============");
            list.clear(); // 10条
            list.add(new DataModel(1, "张三"));
            list.add(new DataModel(1, "李四"));
            list.add(new DataModel(1, "王五"));
            //footView数据
            list.add(new DataModel(2, "张三", "我是标题一"));
            list.add(new DataModel(2, "李四", "我是标题二"));
            list.add(new DataModel(2, "王五", "我是标题三"));

            list.add(new DataModel(1, "小二"));
            list.add(new DataModel(1, "小三"));

            list.add(new DataModel(3, "张三", "我是标题一", "我是标题一"));
            list.add(new DataModel(3, "李四", "我是标题二", "我是标题一"));
            list.add(new DataModel(3, "王五", "我是标题三", "我是标题一"));

            list.add(new DataModel(2, "小二", "我是标题二"));
            list.add(new DataModel(2, "小三", "我是标题三"));
            adapter.addAll(list);
            mController.showViewStatus(PullListMaskController.ListViewState.LIST_NORMAL_HAS_MORE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            //请求接口
            mController.showViewStatus(PullListMaskController.ListViewState.LIST_REFRESH_COMPLETE);
            initData();
        }
    };

    private Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                mHandler.removeMessages(MESSAGE_PARAM_INFO);
                mHandler.sendEmptyMessage(MESSAGE_PARAM_INFO);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable emptyRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                mHandler.removeMessages(MESSAGE_PARAM_INFO);
                mHandler.sendEmptyMessage(MESSAGE_PARAM_INFO);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private Runnable loadMoreRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "run: ==================");
            //请求接口
            //footView数据
            try {
                Thread.sleep(1000);
                mController.showViewStatus(PullListMaskController.ListViewState.LIST_NO_MORE);
//                list.clear(); //7
//                list.add(new DataModel(2, "张三", "我是标题一"));
//                list.add(new DataModel(2, "李四", "我是标题二"));
//                list.add(new DataModel(2, "王五", "我是标题三"));
//
//                list.add(new DataModel(1, "小二"));
//                list.add(new DataModel(1, "小三"));
//
//                list.add(new DataModel(2, "小二", "我是标题二"));
//                list.add(new DataModel(2, "小三", "我是标题三"));
//                adapter.addMore(list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    private static class InnerHandler extends Handler{

        private WeakReference<MainActivity> fragmentReference;

        public InnerHandler(MainActivity fragment){
            fragmentReference = new WeakReference<MainActivity>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final MainActivity activity = fragmentReference.get();
            if (activity == null){
                return;
            }
            switch (msg.what){
                case MESSAGE_PARAM_INFO:
                    activity.mController.showViewStatus(PullListMaskController.ListViewState.EMPTY_BLANK);
                    //重新加载
                    return;
            }
        }
    }
}
