/**
 * @Title: MagazineFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-30 下午2:45:18
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.R;

/**
 * @author dragonsource
 * @ClassName: MagazineFragment
 * @Description: 杂志页面载体(这里用一句话描述这个类的作用)
 * @date 2014-9-30 下午2:45:18
 */
public class MagazineFragment extends BaseFragment {
    private RelativeLayout layout;
    private Button head_LeftBtn, magazineShelfBtn, attentionBtn;
    private MagazineShopFragment magazineShopFragment;
    private MagazineAttentionFragment magazineAttentionFragment;
    private int ischanged = 0;
    private int isShop = 1;
    private int isAtt = 2;
    private int checkColor = 0, normalColor = 0;

    /**
     * (非 Javadoc) Title: onCreateView Description:
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see com.longyuan.qm.BaseFragment#onCreateView(LayoutInflater,
     * ViewGroup, Bundle)
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        checkColor = mContext.getResources().getColor(R.color.system_top_nav);
        normalColor = mContext.getResources().getColor(R.color.system_top);

        View mView = inflater.inflate(R.layout.magazine_fragment_normal, null);
        layout = (RelativeLayout) mView.findViewById(R.id.relativeLayout1);
        magazineShelfBtn = (Button) mView.findViewById(R.id.change_magazine);
        attentionBtn = (Button) mView.findViewById(R.id.change_book);
        head_LeftBtn = (Button) mView
                .findViewById(R.id.head_offline_layout_showLeft);
        magazineShelfBtn.setText("杂志库");
        attentionBtn.setText("关注");
        Bundle bundle = getArguments();

        if (bundle == null) {
            Log.e("bundle", "bundle==null");
            initPage();
        } else {
            Log.e("bundle", "bundle!=null");
            initPageAtt();
        }

        magazineShelfBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                magazineShelfBtn
                        .setBackgroundResource(R.drawable.offline_left_pitchup);
                magazineShelfBtn.setTextColor(checkColor);

                attentionBtn
                        .setBackgroundResource(R.drawable.offline_right_unpitchup);
                attentionBtn.setTextColor(normalColor);
                if (ischanged != isShop) {
                    int height = layout.getLayoutParams().height;
                    Bundle bundle = new Bundle();
                    bundle.putInt("headheight", height);
                    magazineShopFragment = new MagazineShopFragment();
                    FragmentTransaction beginTransaction = getFragmentManager()
                            .beginTransaction();
                    magazineShopFragment.setArguments(bundle);
                    beginTransaction.add(R.id.magazine_fragment_relativelayout,
                            magazineShopFragment);
                    beginTransaction.commit();
                    ischanged = isShop;
                }
            }
        });

        attentionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                attentionBtn
                        .setBackgroundResource(R.drawable.offline_right_pitchup);
                attentionBtn.setTextColor(checkColor);
                magazineShelfBtn
                        .setBackgroundResource(R.drawable.offline_left_unpitchup);
                magazineShelfBtn.setTextColor(normalColor);
                if (ischanged != isAtt) {
                    magazineAttentionFragment = new MagazineAttentionFragment();
                    FragmentTransaction beginTransaction = getFragmentManager()
                            .beginTransaction();
                    beginTransaction.add(R.id.magazine_fragment_relativelayout,
                            magazineAttentionFragment);
                    beginTransaction.commit();
                    ischanged = isAtt;
                }
            }
        });

        return mView;
    }

    private void initPageAtt() {
        attentionBtn.setBackgroundResource(R.drawable.offline_right_pitchup);
        attentionBtn.setTextColor(checkColor);

        magazineShelfBtn
                .setBackgroundResource(R.drawable.offline_left_unpitchup);
        magazineShelfBtn.setTextColor(normalColor);
        if (ischanged != isAtt) {
            magazineAttentionFragment = new MagazineAttentionFragment();
            FragmentTransaction beginTransaction = getFragmentManager()
                    .beginTransaction();
            beginTransaction.add(R.id.magazine_fragment_relativelayout,
                    magazineAttentionFragment);
            beginTransaction.commit();
            ischanged = isAtt;
        }
    }

    private void initPage() {
        magazineShelfBtn.setBackgroundResource(R.drawable.offline_left_pitchup);
        magazineShelfBtn.setTextColor(checkColor);
        attentionBtn.setBackgroundResource(R.drawable.offline_right_unpitchup);
        attentionBtn.setTextColor(normalColor);
        Bundle bundle = new Bundle();
        int height = layout.getLayoutParams().height;
        bundle.putInt("headheight", height);
        magazineShopFragment = new MagazineShopFragment();
        magazineShopFragment.setArguments(bundle);
        FragmentTransaction beginTransaction = getFragmentManager()
                .beginTransaction();
        beginTransaction.add(R.id.magazine_fragment_relativelayout,
                magazineShopFragment);
        beginTransaction.commit();
        ischanged = isShop;
    }
}
