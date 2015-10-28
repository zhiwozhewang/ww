/**
 * @Title: OnMenuItemClickListener.java
 * @Package com.longyuan.zgg.listener
 * @author imhzwen@gmail.com   
 * @date 2014-8-14 上午11:40:35 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.longyuan.qm.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.FavActivity;
import com.longyuan.qm.activity.OffLineActivity;
import com.longyuan.qm.activity.SettingActivity;
import com.longyuan.qm.adapter.MyLeftSlidingListViewAdapter;
import com.longyuan.qm.adapter.MyRightSlidingListViewAdapter;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.MenuBeanRight;

import java.util.List;

/**
 * @author imhzwen@gmail.com
 * @ClassName: OnMenuItemClickListener
 * @Description: 侧滑菜单点击监听回调
 * @date 2014-8-14 上午11:40:35
 */
public class OnRightMenuItemClickListener implements OnItemClickListener {
    Handler mHandler = new Handler();

    List<MenuBeanRight> menuBeans;
    SlidingFragmentActivity activity;
    Fragment lastFragment;
    Context mContext;

    public OnRightMenuItemClickListener(SlidingFragmentActivity activity,
                                        List<MenuBeanRight> activityLists, Context context) {
        this.mContext = context;
        this.menuBeans = activityLists;
        this.activity = activity;
    }

    /**
     * (non-Javadoc)
     *
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
     * android.view.View, int, long)
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, View view,
                            final int position, long id) {
       /*if(position == 0) {
           Intent intent = new Intent(mContext, FavActivity.class);
           mContext.startActivity(intent);
       } else if(position == 1) {
           Intent intent = new Intent(mContext, OffLineActivity.class);
           mContext.startActivity(intent);
       } else if(position == 2) {
           Intent intent = new Intent(mContext, SettingActivity.class);
           mContext.startActivity(intent);
       }*/
        Intent intent = new Intent(mContext, menuBeans.get(position).getActivity().getClass());
        mContext.startActivity(intent);





//        activity.getSlidingMenu().showContent();




        /*FragmentTransaction ft = obtainFragmentTransaction();
        Fragment mFragment = menuBeans.get(position).getFragment();
        if (mFragment != null && mFragment.isAdded()) {
            mFragment.onResume();
        } else {
            ft.replace(R.id.fl_main, mFragment);
        }
        ft.show(mFragment);
        if (lastFragment != null && !lastFragment.equals(mFragment)) {
            lastFragment.onPause();
            ft.setCustomAnimations(R.anim.push_left_out,R.anim.push_left_out);
            ft.hide(lastFragment);
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < menuBeans.size(); i++) {
                    MenuBean item = menuBeans.get(i);
                    if (i == position) {
                        item.setCurrentResource(item.getCheckedResource());
                    } else {
                        item.setCurrentResource(item.getNormalResource());
                    }
                }
                MyRightSlidingListViewAdapter menuAdapter = (MyRightSlidingListViewAdapter) parent.getAdapter();
                menuAdapter.notifyDataSetChanged();
            }
        }, 500);
        lastFragment = mFragment;
        ft.commit();*/
    }

    /*private FragmentTransaction obtainFragmentTransaction() {
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();

        return ft;
    }*/
}
