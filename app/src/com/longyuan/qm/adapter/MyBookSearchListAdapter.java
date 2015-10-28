package com.longyuan.qm.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.BookListBean;
import com.longyuan.qm.bean.MagazineClassifyBean;

import java.util.List;

public class MyBookSearchListAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<BookClassifyBean> mList;
    private Context mContext;

    public MyBookSearchListAdapter(Context context,
                                   List<BookClassifyBean> list) {
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
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.bookshop_item_gridview, null);
            holder.mCover = (ImageView) convertView
                    .findViewById(R.id.imageView_cover);
            holder.mBookName = (TextView) convertView
                    .findViewById(R.id.tvBookName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.e("magazineListAdapter", mList.get(position).getBookName()
                + "\n" + mList.get(position).getBookCover());
        holder.mBookName.setText(mList.get(position).getBookName());
        BitmapUtils bitmapUtil = new BitmapUtils(mContext);
        bitmapUtil.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .display(holder.mCover, mList.get(position).getBookCover());

        return convertView;
    }

    class ViewHolder {
        private ImageView mCover;
        private TextView mBookName;
    }
}