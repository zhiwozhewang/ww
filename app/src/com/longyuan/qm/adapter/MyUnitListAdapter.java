package com.longyuan.qm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longyuan.qm.R;
import com.longyuan.qm.bean.UnitBean;

import java.util.List;

/**
 * Created by Android on 2014/12/19.
 */
public class MyUnitListAdapter extends BaseAdapter {
    private List<UnitBean> mList;
    private Context mContext;
    private ViewHolder holder;

    public MyUnitListAdapter(List<UnitBean> list, Context context) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.unit_choose_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.unit_name);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.name.setText(mList.get(i).getUnitName());
        return view;
    }

    private class ViewHolder {
        private TextView name;
    }
}

