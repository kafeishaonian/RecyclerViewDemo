package com.zhihu.client;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hongmingwei on 2017/5/4 0004.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter {
    /**
     * TAG
     */
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName();

    /**
     * params
     */
    private Context mContext;
    private ArrayList<DataModel> lists = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public RecyclerViewAdapter(Context context){
        this.mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(ArrayList<DataModel> models){
        lists.clear();
        lists.addAll(models);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return lists.get(position).getId();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case 1:
                view = mLayoutInflater.inflate(R.layout.listitem_headerview, parent, false);
                holder = new HeaderViewHolder(view);
                break;
            case 2:
                view = mLayoutInflater.inflate(R.layout.listitem_footview, parent, false);
                holder = new FootViewHolder(view);
                break;
            case 3:
                view = mLayoutInflater.inflate(R.layout.listitem_midview, parent, false);
                holder = new MidViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HeaderViewHolder headerHolder = null;
        FootViewHolder footHolder = null;
        MidViewHolder midHolder = null;
        DataModel model = lists.get(position);
        switch (getItemViewType(position)){
            case 1:
                headerHolder = (HeaderViewHolder) holder;
                headerHolder.id.setText(model.getId() +"");
                headerHolder.name.setText(model.getName());
                break;
            case 2:
                footHolder = (FootViewHolder) holder;
                footHolder.id.setText(model.getId() + "");
                footHolder.name.setText(model.getName());
                footHolder.title.setText(model.getTitle());
                break;
            case 3:
                midHolder = (MidViewHolder) holder;
                midHolder.id.setText(model.getId() + "");
                midHolder.name.setText(model.getName());
                midHolder.title.setText(model.getTitle());
                midHolder.text.setText(model.getText());
                break;
        }
    }



    @Override
    public int getItemCount() {
        return lists.size();
    }


    public static class HeaderViewHolder extends RecyclerView.ViewHolder{

        private TextView id;
        private TextView name;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }
    public static class FootViewHolder extends RecyclerView.ViewHolder{

        private TextView id;
        private TextView name;
        private TextView title;

        public FootViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            title = (TextView) itemView.findViewById(R.id.title);
        }
    }
    public static class MidViewHolder extends RecyclerView.ViewHolder{

        private TextView id;
        private TextView name;
        private TextView title;
        private TextView text;

        public MidViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            title = (TextView) itemView.findViewById(R.id.title);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }


}
