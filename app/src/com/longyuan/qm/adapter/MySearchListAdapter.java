/**
 * @Title: MySearchListAdapter.java
 * @Package com.longyuan.qm.adapter
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Android
 * @date 2014-10-14 下午2:00:43
 * @version V1.0
 */
package com.longyuan.qm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.longyuan.qm.R;
import com.longyuan.qm.bean.SearchListItemBean;

import java.util.List;

/**
 * @author Android
 * @ClassName: MySearchListAdapter
 * @Description: 搜索列表的适配器(这里用一句话描述这个类的作用)
 * @date 2014-10-14 下午2:00:43
 */
public class MySearchListAdapter extends BaseAdapter {
    private List<SearchListItemBean> mList;
    private Context mContext;
    private SearchAdapter_ViewHodler holder = null;

    public MySearchListAdapter(Context context, List<SearchListItemBean> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new SearchAdapter_ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.search_list_item, null);
            holder.search_Title = (TextView) convertView
                    .findViewById(R.id.search_Title);
            holder.search_Info = (TextView) convertView
                    .findViewById(R.id.search_Info);
            convertView.setTag(holder);
        } else {
            holder = (SearchAdapter_ViewHodler) convertView.getTag();
        }
        String author = "", keyWord = "";
        if(!mList.get(position).getAuthor().equals("null")) {
            author = mList.get(position).getAuthor() + "  ";
        }

        if(mList.get(position).getKeyWord().equals("null")) {
            keyWord = "";
        }

//        String info = mList.get(position).getMagazineName() + "   "
//                + mList.get(position).getYear() + "年 第"
//                + mList.get(position).getIssue() + "期";
        holder.search_Title.setText(mList.get(position).getTitle());
        holder.search_Info.setText(author + keyWord);
        return convertView;
    }

    class SearchAdapter_ViewHodler {
        TextView search_Title, search_Info;
    }
}
