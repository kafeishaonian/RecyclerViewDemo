package com.recycleview.client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.recycleview.client.R;
import com.recycleview.client.model.DataModel;

import java.util.ArrayList;

/**
 * Created by Hongmingwei on 2017/12/19.
 * Email: 648600445@qq.com
 */

public class GridViewAdapter extends BaseAdapter {
    /**
     * params
     */
    private Context mContext;
    private ArrayList<DataModel> lists = new ArrayList<>();
    private LayoutInflater mLayoutInflater;

    public GridViewAdapter(Context context){
        this.mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAll(ArrayList<DataModel> models){
        lists.clear();
        lists.addAll(models);
        notifyDataSetChanged();
    }

    public void addMore(ArrayList<DataModel> models){
        lists.addAll(models);
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.listitem_footview, parent, false);
            holder.id = (TextView) convertView.findViewById(R.id.id);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DataModel model = lists.get(position);
        holder.id.setText(model.getId() + "");
        holder.name.setText(model.getName());
        holder.title.setText(model.getTitle());
        return convertView;
    }

    class ViewHolder{
        private TextView id;
        private TextView name;
        private TextView title;
    }
}
