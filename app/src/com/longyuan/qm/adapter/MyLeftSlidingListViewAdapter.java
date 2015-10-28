/**
 * @Title: MyLeftSlidingListViewAdapter.java
 * @Package com.longyuan.qm.adapter
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-26 上午9:34:10
 * @version V1.0
 */
package com.longyuan.qm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.longyuan.qm.R;
import com.longyuan.qm.bean.MenuBean;

import java.util.List;

/**
 * @author dragonsource
 * @ClassName: MyLeftSlidingListViewAdapter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-26 上午9:34:10
 */
public class MyLeftSlidingListViewAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<MenuBean> menus;
    private int checkColor = 0, normalColor = 0;

    public MyLeftSlidingListViewAdapter(Context context, List<MenuBean> menus) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.menus = menus;
        checkColor = this.context.getResources().getColor(R.color.color_b30f13);
        normalColor = this.context.getResources().getColor(R.color.black);
    }

    /**
     * (非 Javadoc) Title: getCount Description:
     *
     * @return
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return menus.size();
    }

    /**
     * (非 Javadoc) Title: getItem Description:
     *
     * @param position
     * @return
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * (非 Javadoc) Title: getItemId Description:
     *
     * @param position
     * @return
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * (非 Javadoc) Title: getView Description:
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     * @see android.widget.Adapter#getView(int, View,
     * ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.adapter_menu, null);
        RelativeLayout layout = (RelativeLayout) convertView
                .findViewById(R.id.sliding_item_layout);
        ImageView icon = (ImageView) convertView
                .findViewById(R.id.left_list_image);
        TextView name = (TextView) convertView
                .findViewById(R.id.left_list_text);
        MenuBean menuBean = menus.get(position);
        if(position==0) {
            name.setTextColor(checkColor);
            layout.setBackgroundColor(Color.parseColor("#A6E2E7"));
        }
        if (menuBean.getCurrentResource() != 0) {
            icon.setImageResource(menuBean.getCurrentResource());
        }
        if (!TextUtils.isEmpty(menuBean.getName())) {
            if (menuBean.getCurrentResource() == menuBean.getCheckedResource()) {
                name.setTextColor(checkColor);
                layout.setBackgroundColor(Color.parseColor("#A6E2E7"));
            } else {
                name.setTextColor(normalColor);
                layout.setBackgroundColor(Color.parseColor("#00000000"));
            }
            name.setText(menuBean.getName());
        }
        return convertView;
    }
}
