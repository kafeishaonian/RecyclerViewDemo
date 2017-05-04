package com.zhihu.client;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by hongmingwei on 2017/5/4 0004.
 */
public class MainActivity extends Activity {
    /**
     * TAG
     */
    private static final String TAG = MainActivity.class.getSimpleName();
    /**
     * View
     */
    private RecyclerView mRecycler;
    private Button mButton;

    /**
     * params
     */
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
        mRecycler = (RecyclerView) findViewById(R.id.recycler);
        mButton = (Button) findViewById(R.id.button);
    }

    private void initListener(){
        mRecycler.setHasFixedSize(true);
        manager = new LinearLayoutManager(this);
        manager.setOrientation(OrientationHelper.VERTICAL);
        mRecycler.setLayoutManager(manager);

        adapter = new RecyclerViewAdapter(this);
        mRecycler.setAdapter(adapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(4, new DataModel(3, "MidView", "我是插入的数据标题", "我是插入数据的内容"));
                adapter.addAll(list);
            }
        });
    }


    /**
     * 模拟数据
     */
    private void initData(){
        //headerView数据
        list.add(new DataModel(1, "张三"));
        list.add(new DataModel(1, "李四"));
        list.add(new DataModel(1, "王五"));
        //footView数据
        list.add(new DataModel(2, "张三", "我是标题一"));
        list.add(new DataModel(2, "李四", "我是标题二"));
        list.add(new DataModel(2, "王五", "我是标题三"));

        list.add(new DataModel(1, "小二"));
        list.add(new DataModel(1, "小三"));

        list.add(new DataModel(2, "小二", "我是标题二"));
        list.add(new DataModel(2, "小三", "我是标题三"));
        adapter.addAll(list);
    }


}
