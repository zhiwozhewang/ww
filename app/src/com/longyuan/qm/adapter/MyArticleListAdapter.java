/**
 * @Title: MyArticleListAdapter.java
 * @Package com.longyuan.qm.adapter
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-10-8 上午11:45:14
 * @version V1.0
 */
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
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.ArticleListItemBean;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshListView;

import java.util.List;

/**
 * @author dragonsource
 * @ClassName: MyArticleListAdapter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-10-8 上午11:45:14
 */
public class MyArticleListAdapter extends BaseAdapter {
    private Context mContext;
    private PullToRefreshListView mListView;
    private List<ArticleListItemBean> mList = null;
    private ArticleListAdapter_ViewHolder holder = null;

    public MyArticleListAdapter(List<ArticleListItemBean> list,
                                Context context, PullToRefreshListView listView) {
        this.mContext = context;
        this.mList = list;
        this.mListView = listView;
    }

    public void getMoreData(List<ArticleListItemBean> list) {
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
            //true
            if (mList.get(position).getArticleImgList().equals("")) {
                holder = new ArticleListAdapter_ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_main_listview_no_icon, null);
                holder.tv_title = (TextView) convertView
                        .findViewById(R.id.item_Title);
                holder.tv_pub = (TextView) convertView
                        .findViewById(R.id.item_Pub);
                holder.tv_date = (TextView) convertView
                        .findViewById(R.id.item_date);
//                holder.tv_content = (TextView) convertView
//                        .findViewById(R.id.item_Introduction);
            } else {
                holder = new ArticleListAdapter_ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_main_listview, null);
                holder.tv_title = (TextView) convertView
                        .findViewById(R.id.item_Title);
                holder.tv_pub = (TextView) convertView
                        .findViewById(R.id.item_Pub);
                holder.tv_date = (TextView) convertView
                        .findViewById(R.id.item_date);
//                holder.tv_content = (TextView) convertView
//                        .findViewById(R.id.item_Introduction);
                holder.img_logo = (ImageView) convertView
                        .findViewById(R.id.item_img);
            }
            convertView.setTag(holder);
        } else {
            holder = (ArticleListAdapter_ViewHolder) convertView.getTag();
        }
//		String date = mList.get(position).getYear() + "年 第"
//				+ mList.get(position).getIssue() + "期";
        String date = mList.get(position).getYear().substring(0, 10).replace("-", ".");
//        Log.e("date", date.substring(0, 10).replace("-", "."));

        // 没有图片  true
        if (mList.get(position).getArticleImgList().equals("")) {
            holder.tv_title.setText(mList.get(position).getTitle());
            if (mList.get(position).getAuthor().equals("")) {
                holder.tv_pub.setText("");
            } else {
                holder.tv_pub.setText(mList.get(position).getAuthor() + "    ");
            }
            holder.tv_date.setText(date);
            /*if (mList.get(position).getIntroduction().equals("null")) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(mList.get(position).getIntroduction());
            }*/
        } else {
            holder.tv_title.setText(mList.get(position).getTitle());
            if (mList.get(position).getAuthor().equals("")) {
                holder.tv_pub.setText("");
            } else {
                holder.tv_pub.setText(mList.get(position).getAuthor() + "    ");
            }
            holder.tv_date.setText(date);
            /*if (mList.get(position).getIntroduction().equals("null")) {
                holder.tv_content.setVisibility(View.GONE);
            } else {
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(mList.get(position).getIntroduction());
            }*/
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils
                    .configDefaultLoadingImage(R.drawable.empty_photo);
            bitmapUtils
                    .configDefaultLoadFailedImage(R.drawable.empty_photo);
            bitmapUtils.display(holder.img_logo, mList.get(position)
                    .getArticleImgList());
            mListView.setOnScrollListener(new
                    PauseOnScrollListener(bitmapUtils, false, true));
        }
        return convertView;
    }

    class ArticleListAdapter_ViewHolder {
        TextView tv_title, tv_pub, tv_content, tv_date;
        ImageView img_logo;
    }
}
