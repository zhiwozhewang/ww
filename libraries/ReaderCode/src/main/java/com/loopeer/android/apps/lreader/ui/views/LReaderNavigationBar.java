/**
 * Created by YuGang Yang on August 16, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopeer.android.apps.lreader.ui.activities.ContentsActivity;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.OrientationUtil;
import org.geometerplus.android.fbreader.R;
import org.geometerplus.android.fbreader.api.FBReaderIntents;
import org.geometerplus.fbreader.book.Bookmark;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;

public class LReaderNavigationBar extends RelativeLayout implements View.OnClickListener {

    private LReaderNavigationBarPopup mReaderNavigationBarPopup;
    private ImageView mBookmarkIcon;
    private RelativeLayout mReaderNavigationBar;

    private Bookmark mCurrentPageBookmark;

    @SuppressWarnings("unused") public LReaderNavigationBar(Context context) {
        this(context, null);
    }

    public LReaderNavigationBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LReaderNavigationBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context);
    }

    private void initialize(Context context) {
        LayoutInflater.from(context).inflate(R.layout.l_reader_navigation_bar, this);
        mBookmarkIcon = UiUtilities.getView(this, R.id.image_reader_navigationbar_bookmark);
        mReaderNavigationBar = UiUtilities.getView(this, R.id.wrapper_reader_navifation_bar);

        UiUtilities.getView(this, R.id.view_reader_navigationbar_back).setOnClickListener(this);
        UiUtilities.getView(this, R.id.view_reader_navigationbar_toc).setOnClickListener(this);
        UiUtilities.getView(this, R.id.view_reader_navigationbar_font).setOnClickListener(this);
        UiUtilities.getView(this, R.id.view_reader_navigationbar_bookmark).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.view_reader_navigationbar_back) {
            onBackPressed();
        } else if (id == R.id.view_reader_navigationbar_toc) {
            onTocPressed();
        } else if (id == R.id.view_reader_navigationbar_font) {
            onFontPressed(v);
        } else if (id == R.id.view_reader_navigationbar_bookmark) {
            onBookmarkPressed(v);
        }
    }

    public void onBackPressed() {
        ((Activity)getContext()).finish();
    }

    public void onTocPressed() {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp == null) return;

        Activity activity = (Activity) getContext();
        final Intent intent = new Intent(activity.getApplicationContext(), ContentsActivity.class);
        FBReaderIntents.putBookExtra(intent, fbReaderApp.getCurrentBook());
        FBReaderIntents.putBookmarkExtra(intent, fbReaderApp.createBookmark(20, true));
        OrientationUtil.startActivity(activity, intent);
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void onFontPressed(View view) {
        if (mReaderNavigationBarPopup == null) {
            mReaderNavigationBarPopup = new LReaderNavigationBarPopup(getContext());
        }
        mReaderNavigationBarPopup.show((Activity)getContext(), view);
    }

    public void onBookmarkPressed(View view) {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp == null) return;

        if (mCurrentPageBookmark == null) {
            mCurrentPageBookmark = fbReaderApp.addCurrentPage2Bookmark();
            mBookmarkIcon.setImageResource(R.drawable.ic_l_reader_navigation_bar_bookmark_checked_day);
        } else {
            fbReaderApp.deleteBookmark(mCurrentPageBookmark);
            mBookmarkIcon.setImageResource(R.drawable.ic_l_reader_navigation_bar_bookmark_default_day);
            mCurrentPageBookmark = null;
        }
    }

    public void updateBackground() {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp != null) {
            if (ColorProfile.DAY.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                mReaderNavigationBar.setBackgroundResource(R.color.color_efefef);
            } else if (ColorProfile.NIGHT.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                mReaderNavigationBar.setBackgroundResource(R.color.color_434853);
            }
        }
    }

    public void show() {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp != null) {
            updateBackground();

            Bookmark bookmark = fbReaderApp.getCurrentBookmark();
            if (bookmark != null) {
                mCurrentPageBookmark = bookmark;
                mBookmarkIcon.setImageResource(R.drawable.ic_l_reader_navigation_bar_bookmark_checked_day);
            } else {
                mBookmarkIcon.setImageResource(R.drawable.ic_l_reader_navigation_bar_bookmark_default_day);
            }
        }
        setVisibility(View.VISIBLE);
    }

    public void hide() {
        mCurrentPageBookmark = null;
        setVisibility(View.GONE);
        if (mReaderNavigationBarPopup != null) {
            mReaderNavigationBarPopup.dismiss();
        }
    }

}
