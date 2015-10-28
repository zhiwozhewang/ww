package com.longyuan.qm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MagazineAttentionBean;

import java.util.List;

public class MyMagazineAttentionAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<MagazineAttentionBean> mList = null;
    private Context mContext;
//    private int p = 0;

    public MyMagazineAttentionAdapter(Context context,
                                      List<MagazineAttentionBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public List<MagazineAttentionBean> getmList() {
        return mList;
    }

    public void setmList(List<MagazineAttentionBean> mList) {
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
//        p = position;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.mag_att_item_gridview, null);
            holder.mag_att_caver = (ImageView) convertView
                    .findViewById(R.id.mag_att_imageView_cover);
            holder.mag_att_name = (TextView) convertView
                    .findViewById(R.id.mag_att_tvMagName);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mag_att_name.setText(mList.get(position).getMag_name());
        // holder.mag_att_caver
        BitmapUtils bitmapUtils = new BitmapUtils(mContext);
        bitmapUtils.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
        bitmapUtils
                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
        bitmapUtils.display(holder.mag_att_caver, mList.get(position)
                .getMag_cover());
        return convertView;
    }

    class ViewHolder {
        private ImageView mag_att_caver;
        private TextView mag_att_name;
    }
}