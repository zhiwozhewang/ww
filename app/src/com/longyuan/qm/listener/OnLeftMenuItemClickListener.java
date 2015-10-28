/**
 * @Title: OnMenuItemClickListener.java
 * @Package com.longyuan.zgg.listener
 * @author imhzwen@gmail.com   
 * @date 2014-8-14 上午11:40:35 
 * @version V1.0
 * @encoding UTF-8   
 */
package com.longyuan.qm.listener;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyLeftSlidingListViewAdapter;
import com.longyuan.qm.bean.MenuBean;

import java.util.List;

/**
 * @author imhzwen@gmail.com
 * @ClassName: OnMenuItemClickListener
 * @Description: 侧滑菜单点击监听回调
 * @date 2014-8-14 上午11:40:35
 */
public class OnLeftMenuItemClickListener implements OnItemClickListener {
    Handler mHandler = new Handler();

    List<MenuBean> menuBeans;
    SlidingFragmentActivity activity;
    Fragment lastFragment;

    public OnLeftMenuItemClickListener(SlidingFragmentActivity activity,
                                       List<MenuBean> fragments) {
        this.menuBeans = fragments;
        this.activity = activity;
    }

    /**
     * (non-Javadoc)
     *
     * @see OnItemClickListener#onItemClick(AdapterView,
     * View, int, long)
     */
    @Override
    public void onItemClick(final AdapterView<?> parent, View view,
                            final int position, long id) {
        FragmentTransaction ft = obtainFragmentTransaction();
        Fragment mFragment = menuBeans.get(position).getFragment();
        //FIXME 将栏目的position存成静态值；
        ConstantsAmount.MENUPOSITION = position;

        if (mFragment != null && mFragment.isAdded()) {
            mFragment.onResume();
        } else {
            ft.replace(R.id.fl_main, mFragment);
        }
        ft.show(mFragment);
        if (lastFragment != null && !lastFragment.equals(mFragment)) {
            lastFragment.onPause();
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
                MyLeftSlidingListViewAdapter menuAdapter = (MyLeftSlidingListViewAdapter) parent.getAdapter();
                menuAdapter.notifyDataSetChanged();
            }
        }, 500);
        lastFragment = mFragment;
        ft.commit();
        activity.getSlidingMenu().showContent();
    }

    private FragmentTransaction obtainFragmentTransaction() {
        FragmentTransaction ft = activity.getSupportFragmentManager()
                .beginTransaction();
        return ft;
    }

    public void showDefaultFragment() {
        if (menuBeans.size() > 0) {
            Fragment defaultFragment = menuBeans.get(0).getFragment();
            FragmentTransaction ft = obtainFragmentTransaction();
            ft.add(R.id.fl_main, defaultFragment);
            lastFragment = defaultFragment;
            ft.commit();
        }
    }
}
