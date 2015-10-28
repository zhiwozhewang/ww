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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.HomeActivity;

/**
 * @author dragonsource
 * @ClassName: LeftMenuFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-25 下午3:22:33
 */
public class RightMenuFragment extends BaseFragment {
    FragmentActivity mActivity;
    private ListView listView;
    private Button setting_button;
    private String name, phoneNumber, unitName, logoUrl, backgroundUrl;

    /**
     * (非 Javadoc) Title: onCreate Description:
     *
     * @param savedInstanceState
     * @see com.longyuan.qm.BaseFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    /**
     * f
     * (非 Javadoc) Title: setContentView Description:
     *
     * @param inflater
     * @return
     * @see com.longyuan.qm.BaseFragment#setContentView(android.view.LayoutInflater)
     */
    @Override
    protected View setContentView(LayoutInflater inflater) {
        View mView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_rightmenu, null);
        Bundle bundle = getArguments();
        name = bundle.getString("name");
//        phoneNumber = bundle.getString("phoneNumber");
        unitName = bundle.getString("unitName");
        logoUrl = bundle.getString("logoUrl");
        backgroundUrl = bundle.getString("backgroundUrl");
        phoneNumber = mSp.getString("username", "");

        listView = (ListView) mView.findViewById(R.id.sliding_listView_right);
        ImageView sliding_bg_right = (ImageView) mView.findViewById(R.id.sliding_bg_right);
        ImageView sliding_logo_right = (ImageView) mView.findViewById(R.id.sliding_logo_right);
        TextView text_name = (TextView) mView.findViewById(R.id.welcome_text);
        TextView text_pn = (TextView) mView.findViewById(R.id.phoneNumber_text);
        TextView text_unitName = (TextView) mView.findViewById(R.id.sliding_unitName_right);

        text_name.setText(name);
        text_pn.setText(phoneNumber);
        text_unitName.setText(unitName);

        BitmapUtils backgroundUtil = new BitmapUtils(mContext);
//        backgroundUtil.configDefaultLoadingImage(R.drawable.right_slider_bg);
//        backgroundUtil
//                .configDefaultLoadFailedImage(R.drawable.right_slider_bg);
        backgroundUtil.display(sliding_bg_right, backgroundUrl);

        BitmapUtils logoUtil = new BitmapUtils(mContext);
//        logoUtil.configDefaultLoadingImage(R.drawable.right_slider_logo);
//        logoUtil.configDefaultLoadFailedImage(R.drawable.right_slider_logo);
        logoUtil.display(sliding_logo_right, logoUrl);

        int width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth();
//        sliding_bg_right.getLayoutParams().height = width / 3;
//        sliding_bg_right.getLayoutParams().height = width * 3 / 2 / 2;
//        sliding_bg_right.getLayoutParams().height = width / 2 * 2 / 3;
        //FIXME 图片尺寸：736 * 438，算法为： width*168/100*2/3
        sliding_bg_right.getLayoutParams().height = width * 25 / 63;
//        sliding_bg_right.setImageResource(R.drawable.right_slider_bg);
//        img.setLayoutParams();
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
                        .getOnRightMenuItemClickListener());
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
            if (homeActicity.getRightMenuAdapter() != null) {
                listView.setAdapter(homeActicity.getRightMenuAdapter());
            }
        }
    }
}
