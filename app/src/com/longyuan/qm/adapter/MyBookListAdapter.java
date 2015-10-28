package com.longyuan.qm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;

import java.util.List;

public class MyBookListAdapter extends BaseAdapter {
    private List<BookClassifyBean> mList;
    private Context mContext;
    private PullToRefreshGridView mGridView;
    private ViewHolder holder = null;

    public MyBookListAdapter(Context context, List<BookClassifyBean> list,
                             PullToRefreshGridView gridView) {
        this.mContext = context;
        this.mList = list;
        this.mGridView = gridView;
    }

    public void setListData(List<BookClassifyBean> list) {
        this.mList = list;
    }

    public List<BookClassifyBean> getmList() {
        return mList;
    }

    public void setmList(List<BookClassifyBean> mList) {
        this.mList = mList;
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
            holder.mBookAuthor = (TextView) convertView
                    .findViewById(R.id.tvBookProgress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mBookAuthor.setTextColor(Color.GRAY);
        holder.mBookName.setText(mList.get(position).getBookName());
        holder.mBookAuthor.setText(mList.get(position).getAuthor());
        BitmapUtils bitmapUtil = new BitmapUtils(mContext);
        bitmapUtil.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
        bitmapUtil.display(holder.mCover, mList.get(position).getBookCover());

        mGridView.setOnScrollListener(new PauseOnScrollListener(bitmapUtil,
                false, true));
        return convertView;
    }

    class ViewHolder {
        private ImageView mCover;
        private TextView mBookName, mBookAuthor;
    }
}