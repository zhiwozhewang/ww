package com.longyuan.qm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MagazineClassifyBean;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;

import java.util.List;

public class MyMagazineListAdapter extends BaseAdapter {
    private MagazineListAdapter_ViewHolder holder;
    private List<MagazineClassifyBean> mList;
    private Context mContext;
    private PullToRefreshGridView mGridView;

    public MyMagazineListAdapter(Context context,
                                 List<MagazineClassifyBean> list, PullToRefreshGridView gridView) {
        this.mContext = context;
        this.mList = list;
        this.mGridView = gridView;
    }

    public void setListData(List<MagazineClassifyBean> list) {
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
            holder = new MagazineListAdapter_ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.bookshop_item_gridview, null);
            holder.mCover = (ImageView) convertView
                    .findViewById(R.id.imageView_cover);
            holder.mBookName = (TextView) convertView
                    .findViewById(R.id.tvBookName);
            convertView.setTag(holder);
        } else {
            holder = (MagazineListAdapter_ViewHolder) convertView.getTag();
        }

        holder.mBookName.setText(mList.get(position).getMagazineName());
        BitmapUtils bitmapUtil = new BitmapUtils(mContext);
        bitmapUtil.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .display(holder.mCover, mList.get(position).getCoverPicList());
        mGridView.setOnScrollListener(new PauseOnScrollListener(bitmapUtil,
                false, true));
        return convertView;
    }

    class MagazineListAdapter_ViewHolder {
        private ImageView mCover;
        private TextView mBookName;
    }
}