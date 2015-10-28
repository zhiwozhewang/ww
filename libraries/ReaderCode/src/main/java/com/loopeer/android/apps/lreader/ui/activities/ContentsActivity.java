/**
 * Created by YuGang Yang on August 14, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;

import com.loopeer.android.apps.lreader.ui.fragments.BookmarksFragment;
import com.loopeer.android.apps.lreader.ui.fragments.TOCFragment;
import com.loopeer.android.apps.lreader.ui.views.SegmentedGroup;
import com.loopeer.android.apps.lreader.utilities.FragmentPagerAdapter;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.fbreader.R;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;

/**
 * 正文书签
 */
public class ContentsActivity extends BaseReaderActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {

    private SegmentedGroup mSegmented;
    private ViewPager mViewPager;

    private ContentsPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp != null) {
            if (ColorProfile.DAY.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                setTheme(R.style.Theme_Reader);
            } else if (ColorProfile.NIGHT.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                setTheme(R.style.Theme_Reader_Night);
            }
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new org.geometerplus.zlibrary.ui.android.library.UncaughtExceptionHandler(this));

        setContentView(R.layout.activity_contents);
        mSegmented = UiUtilities.getView(this, R.id.segmented_contents);
        mViewPager = UiUtilities.getView(this, R.id.viewpager);

        UiUtilities.getView(this, R.id.btn_contents_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        mPagerAdapter = new ContentsPagerAdapter(getFragmentManager());
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setAdapter(mPagerAdapter);

        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        mViewPager.setPageMargin(pageMargin);

        mSegmented.setOnCheckedChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        OrientationUtil.setOrientation(this, getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        OrientationUtil.setOrientation(this, getIntent());
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_contents_catalog) {
            mViewPager.setCurrentItem(0);
        } else if (checkedId == R.id.radio_contents_bookmark) {
            mViewPager.setCurrentItem(1);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mSegmented.check(R.id.radio_contents_catalog);
                break;
            case 1:
                mSegmented.check(R.id.radio_contents_bookmark);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ContentsPagerAdapter extends FragmentPagerAdapter {

        private ContentsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TOCFragment.createCatalogFragment();
                case 1:
                    return BookmarksFragment.createBookmarkFragment(BaseReaderActivity.intentToFragmentArguments(getIntent()));
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
