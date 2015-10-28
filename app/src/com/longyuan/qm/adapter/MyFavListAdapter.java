package com.longyuan.qm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longyuan.qm.R;
import com.longyuan.qm.bean.FavListDataBean;
import com.longyuan.qm.utils.Utils;

import java.util.List;

/**
 * Created by myf on 14/10/24.
 */
public class MyFavListAdapter extends BaseAdapter {

    private Context mContext;
    private List<FavListDataBean> mList;
    private ViewHolder holder;

    public MyFavListAdapter(Context context, List<FavListDataBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_favorite_listview_no_icon, null);
            holder.tv_Title = (TextView) convertView.findViewById(R.id.Item_Title);
            holder.tv_Auth = (TextView) convertView.findViewById(R.id.Item_Auth);
            holder.tv_Pub = (TextView) convertView.findViewById(R.id.Item_Pub);
            holder.tv_Content = (TextView) convertView.findViewById(R.id.Item_Content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_Title.setText(mList.get(position).getTitle());
        holder.tv_Auth.setText(mList.get(position).getAuthor());
        holder.tv_Pub.setText(mList.get(position).getDate());
        holder.tv_Content.setText(Utils.replaceHtmlTag(mList.get(position).getIntroduction()));
        return convertView;
    }

    class ViewHolder {
        private TextView tv_Title, tv_Auth, tv_Pub, tv_Content;
    }
}
