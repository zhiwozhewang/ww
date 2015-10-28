/**
 * @Title: LeftMenuFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-25 下午3:22:33
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.HomeActivity;

/**
 * @author dragonsource
 * @ClassName: LeftMenuFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-25 下午3:22:33
 */
public class LeftMenuFragment extends BaseFragment {
    FragmentActivity mActivity;
    private ListView listView;
    private Button setting_button;

    /**
     * (非 Javadoc) Title: onCreate Description:
     *
     * @param savedInstanceState
     * @see com.longyuan.qm.BaseFragment#onCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    /**
     * (非 Javadoc) Title: setContentView Description:
     *
     * @param inflater
     * @return
     * @see com.longyuan.qm.BaseFragment#setContentView(LayoutInflater)
     */
    @Override
    protected View setContentView(LayoutInflater inflater) {
        View mView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_leftmenu, null);
        findViewByIds(mView);
//        setting_button = (Button) mView.findViewById(R.id.setting_button);
//        setting_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                SettingFragment settingFragment = new SettingFragment();
//                FragmentTransaction beginTransaction = getFragmentManager()
//                        .beginTransaction();
//                beginTransaction.replace(R.id.fl_main,settingFragment);
//                beginTransaction.commit();
//            }
//        });
        return mView;
    }

    /**
     * (非 Javadoc) Title: findViewByIds Description:
     *
     * @param view
     * @see com.longyuan.qm.BaseFragment#findViewByIds(View)
     */
    @Override
    protected void findViewByIds(View view) {
        listView = (ListView) view.findViewById(R.id.sliding_listview);

    }

    /**
     * (非 Javadoc) Title: setListeners Description:
     *
     * @see com.longyuan.qm.BaseFragment#setListeners()
     */
    @Override
    protected void setListeners() {
        if (mActivity instanceof HomeActivity) {
            HomeActivity homeActivity = (HomeActivity) mActivity;
            if (homeActivity.getOnLeftMenuItemClickListener() != null) {
                listView.setOnItemClickListener(homeActivity
                        .getOnLeftMenuItemClickListener());
            }
        }
    }

    /**
     * (非 Javadoc) Title: init Description:
     *
     * @see com.longyuan.qm.BaseFragment#init()
     */
    @Override
    protected void init() {
        if (mActivity instanceof HomeActivity) {
            HomeActivity homeActicity = (HomeActivity) mActivity;
            if (homeActicity.getLeftMenuAdapter() != null) {
                listView.setAdapter(homeActicity.getLeftMenuAdapter());
            }
        }
    }
}
