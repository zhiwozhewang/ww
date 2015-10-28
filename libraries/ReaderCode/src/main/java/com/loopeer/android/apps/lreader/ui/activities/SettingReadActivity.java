/**
 * Created by YuGang Yang on September 24, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.loopeer.android.apps.lreader.ui.views.ReaderTitlebarView;
import com.loopeer.android.apps.lreader.utilities.ReaderPrefUtils;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.R;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.fbreader.fbreader.options.PageTurningOptions;
import org.geometerplus.fbreader.fbreader.options.ViewOptions;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.text.view.style.ZLTextBaseStyle;
import org.geometerplus.zlibrary.text.view.style.ZLTextNGStyleDescription;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;

public class SettingReadActivity extends BaseReaderActivity implements ReaderTitlebarView.OnTitlebarIconClickListener,
        View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    ReaderTitlebarView mTitlebarView;
    ToggleButton mRecentlyToggleButton;
    TextView mFontTextView;
    TextView[] mAnimationTextViews = new TextView[4];
    TextView[] mScreenTextViews = new TextView[4];
    //ImageView[] mFirstLineTextViews = new ImageView[3];
    ImageView[] mLineSpaceTextViews = new ImageView[3];
    ImageView[] mSectionSpacingViews = new ImageView[3];
    ImageView[] mMarginSpacingViews = new ImageView[3];

    private int[] mLineSpacingRes = new int[] {
            R.drawable.ic_l_settings_read_line_spacing_1_day,
            R.drawable.ic_l_settings_read_line_spacing_2_day,
            R.drawable.ic_l_settings_read_line_spacing_3_day
    };
    private int[] mLineSpacingResChecked = new int[] {
            R.drawable.ic_l_settings_read_line_spacing_1_checked_day,
            R.drawable.ic_l_settings_read_line_spacing_2_checked_day,
            R.drawable.ic_l_settings_read_line_spacing_3_checked_day
    };
    private int[] mLineSpacingValues = new int[] {
            ReaderPrefUtils.LINE_SPACING_VALUE_0,
            ReaderPrefUtils.LINE_SPACING_VALUE_1,
            ReaderPrefUtils.LINE_SPACING_VALUE_2
    };

    private int[] mMarginSpacingRes = new int[] {
            R.drawable.ic_l_settings_read_margin_spacing_1_day,
            R.drawable.ic_l_settings_read_margin_spacing_2_day,
            R.drawable.ic_l_settings_read_margin_spacing_3_day
    };
    private int[] mMarginSpacingResChecked = new int[] {
            R.drawable.ic_l_settings_read_margin_spacing_1_checked_day,
            R.drawable.ic_l_settings_read_margin_spacing_2_checked_day,
            R.drawable.ic_l_settings_read_margin_spacing_3_checked_day
    };

    private int[] mSectionSpacingRes = new int[] {
            R.drawable.ic_l_settings_read_section_spacing_1_day,
            R.drawable.ic_l_settings_read_section_spacing_2_day,
            R.drawable.ic_l_settings_read_section_spacing_3_day
    };
    private int[] mSectionSpacingResChecked = new int[] {
            R.drawable.ic_l_settings_read_section_spacing_1_checked_day,
            R.drawable.ic_l_settings_read_section_spacing_2_checked_day,
            R.drawable.ic_l_settings_read_section_spacing_3_checked_day
    };

    private int[] LeftMargins = new int[3];
    private int[] RightMargins = new int[3];
    private int[] TopMargins = new int[3];
    private int[] BottomMargins = new int[3];

    private ZLView.Animation[] animations = new ZLView.Animation[]{
        ZLView.Animation.slide,
        ZLView.Animation.shift,
        ZLView.Animation.curl,
        ZLView.Animation.none,
    };

    private FBReaderApp myFBReaderApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        myFBReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (myFBReaderApp == null) {
            myFBReaderApp = new FBReaderApp(new BookCollectionShadow());
        }
        if (ColorProfile.DAY.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            setTheme(R.style.Theme_Reader);
        } else if (ColorProfile.NIGHT.equals(myFBReaderApp.ViewOptions.ColorProfileName.getValue())) {
            setTheme(R.style.Theme_Reader_Night);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_read);

        setupViews();
        setListeners();

        mTitlebarView.setOnTitlebarIconClickListener(this);

        // init
        initMargins();
    }

    private void setupViews() {
        mTitlebarView = UiUtilities.getView(this, R.id.titlebar);
        mRecentlyToggleButton = UiUtilities.getView(this, R.id.switch_settings_read_recently);
        mFontTextView = UiUtilities.getView(this, R.id.text_setting_read_font);

        mAnimationTextViews[0] = UiUtilities.getView(this, R.id.text_settings_read_animation_slide);
        mAnimationTextViews[1] = UiUtilities.getView(this, R.id.text_settings_read_animation_shift);
        mAnimationTextViews[2] = UiUtilities.getView(this, R.id.text_settings_read_animation_curl);
        mAnimationTextViews[3] = UiUtilities.getView(this, R.id.text_settings_read_animation_none);

        mScreenTextViews[0] = UiUtilities.getView(this, R.id.text_settings_read_screen_second_minutes);
        mScreenTextViews[1] = UiUtilities.getView(this, R.id.text_settings_read_screen_five_minutes);
        mScreenTextViews[2] = UiUtilities.getView(this, R.id.text_settings_read_screen_ten_minutes);
        mScreenTextViews[3] = UiUtilities.getView(this, R.id.text_settings_read_screen_none);

        //mFirstLineTextViews[0] = UiUtilities.getView(this, R.id.image_settings_read_first_line_indent_1);
        //mFirstLineTextViews[1] = UiUtilities.getView(this, R.id.image_settings_read_first_line_indent_2);
        //mFirstLineTextViews[2] = UiUtilities.getView(this, R.id.image_settings_read_first_line_indent_3);

        mLineSpaceTextViews[0] = UiUtilities.getView(this, R.id.image_settings_read_line_spacing_1);
        mLineSpaceTextViews[1] = UiUtilities.getView(this, R.id.image_settings_read_line_spacing_2);
        mLineSpaceTextViews[2] = UiUtilities.getView(this, R.id.image_settings_read_line_spacing_3);

        mSectionSpacingViews[0] = UiUtilities.getView(this, R.id.image_settings_read_section_spacing_1);
        mSectionSpacingViews[1] = UiUtilities.getView(this, R.id.image_settings_read_section_spacing_2);
        mSectionSpacingViews[2] = UiUtilities.getView(this, R.id.image_settings_read_section_spacing_3);

        mMarginSpacingViews[0] = UiUtilities.getView(this, R.id.image_settings_read_margin_spacing_1);
        mMarginSpacingViews[1] = UiUtilities.getView(this, R.id.image_settings_read_margin_spacing_2);
        mMarginSpacingViews[2] = UiUtilities.getView(this, R.id.image_settings_read_margin_spacing_3);

    }

    private void setListeners() {
        UiUtilities.getView(this, R.id.wrapper_reader_settings_font).setOnClickListener(this);
        mRecentlyToggleButton.setOnCheckedChangeListener(this);
        for (TextView textView : mAnimationTextViews) {
            textView.setOnClickListener(this);
        }

        for (TextView textView : mScreenTextViews) {
            textView.setOnClickListener(this);
        }

        UiUtilities.getView(this, R.id.wrapper_settings_read_line_spacing_1).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_line_spacing_2).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_line_spacing_3).setOnClickListener(this);

        UiUtilities.getView(this, R.id.wrapper_settings_read_margin_spacing_1).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_margin_spacing_2).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_margin_spacing_3).setOnClickListener(this);

        UiUtilities.getView(this, R.id.wrapper_settings_read_section_spacing_1).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_section_spacing_2).setOnClickListener(this);
        UiUtilities.getView(this, R.id.wrapper_settings_read_section_spacing_3).setOnClickListener(this);

        //UiUtilities.getView(this, R.id.wrapper_settings_read_first_line_indent_1).setOnClickListener(this);
        //UiUtilities.getView(this, R.id.wrapper_settings_read_first_line_indent_2).setOnClickListener(this);
        //UiUtilities.getView(this, R.id.wrapper_settings_read_first_line_indent_3).setOnClickListener(this);
    }

    private void initMargins() {
        final ZLibrary zlibrary = ZLibrary.Instance();

        final int dpi = zlibrary.getDisplayDPI();
        final int x = zlibrary.getWidthInPixels();
        final int y = zlibrary.getHeightInPixels();
        final int horMargin = Math.min(dpi / 5, Math.min(x, y) / 30);
        LeftMargins[0] = horMargin;
        LeftMargins[1] = horMargin + 20;
        LeftMargins[2] = horMargin + 40;
        RightMargins[0] = horMargin;
        RightMargins[1] = horMargin + 20;
        RightMargins[2] = horMargin + 40;

        TopMargins[0] = 15;
        TopMargins[1] = 35;
        TopMargins[2] = 55;

        BottomMargins[0] = 20;
        BottomMargins[1] = 40;
        BottomMargins[2] = 60;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        updateLineSpacing(ReaderPrefUtils.getLineSpacingIndex(this));
        updateMarginSpacing(ReaderPrefUtils.getMarginSpacingIndex(this));
        updateAnimation(ReaderPrefUtils.getAnimationIndex(this));
        updateScreen(ReaderPrefUtils.getScreenIndex(this));
        updateSectionSpacing(ReaderPrefUtils.getSectionSpacingIndex(this));

        mRecentlyToggleButton.setChecked(ReaderPrefUtils.isRecentlyRead(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ViewOptions viewOptions = new ViewOptions();
        final ZLTextStyleCollection collection = viewOptions.getTextStyleCollection();
        final ZLTextBaseStyle baseStyle = collection.getBaseStyle();
        String fontFamily = baseStyle.FontFamilyOption.getValue();
        if ("sans-serif".equals(fontFamily)) {
            fontFamily = "系统";
        }
        mFontTextView.setText(fontFamily);
    }


    private void changeLineSpacing(int index) {
        updateLineSpacing(index);
        ReaderPrefUtils.markLineSpacingIndex(this, index);

        final ZLIntegerRangeOption spaceOption = myFBReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption;
        spaceOption.setValue(mLineSpacingValues[index]);
    }

    private void updateLineSpacing(int index) {
        for (int i=0; i<mLineSpaceTextViews.length; i++) {
            if (index == i) {
                mLineSpaceTextViews[index].setImageResource(mLineSpacingResChecked[index]);
            } else {
                mLineSpaceTextViews[i].setImageResource(mLineSpacingRes[i]);
            }
        }
    }

    /**
     * 页边间距
     * @param index
     */
    private void changeMarginSpacing(int index) {
        updateMarginSpacing(index);
        ReaderPrefUtils.markMarginSpacingIndex(this, index);

        final ZLIntegerRangeOption rightMarginOption = myFBReaderApp.ViewOptions.RightMargin;
        final ZLIntegerRangeOption leftMarginOption = myFBReaderApp.ViewOptions.LeftMargin;
        final ZLIntegerRangeOption topMarginOption = myFBReaderApp.ViewOptions.TopMargin;
        final ZLIntegerRangeOption bottomMarginOption = myFBReaderApp.ViewOptions.BottomMargin;
        rightMarginOption.setValue(RightMargins[index]);
        leftMarginOption.setValue(LeftMargins[index]);
        topMarginOption.setValue(TopMargins[index]);
        bottomMarginOption.setValue(BottomMargins[index]);
    }
    private void updateMarginSpacing(int index) {
        for (int i=0; i<mMarginSpacingViews.length; i++) {
            if (index == i) {
                mMarginSpacingViews[index].setImageResource(mMarginSpacingResChecked[index]);
            } else {
                mMarginSpacingViews[i].setImageResource(mMarginSpacingRes[i]);
            }
        }
    }

    private void changeSectionSpacing(int index) {
        updateSectionSpacing(index);
        ReaderPrefUtils.markSectionSpacingIndex(this, index);

        // TODO
        final ZLTextStyleCollection collection = myFBReaderApp.ViewOptions.getTextStyleCollection();

        for (ZLTextNGStyleDescription description : collection.getDescriptionList()) {
                ZLStringOption option = description.MarginBottomOption;
                option.getValue();
                option.setValue(index * 20 + "px");
                option.setSpecialName("spaceAfter");
        }
    }
    private void updateSectionSpacing(int index) {
        for (int i=0; i<mSectionSpacingViews.length; i++) {
            if (index == i) {
                mSectionSpacingViews[index].setImageResource(mSectionSpacingResChecked[index]);
            } else {
                mSectionSpacingViews[i].setImageResource(mSectionSpacingRes[i]);
            }
        }
    }

    private void chanageAnimation(int index) {
        updateAnimation(index);
        ReaderPrefUtils.markAnimationIndex(this, index);

        // set prefs
        final PageTurningOptions pageTurningOptions = myFBReaderApp.PageTurningOptions;
        pageTurningOptions.Animation.setValue(animations[index]);
    }

    private void updateAnimation(int index) {
        for (int i=0; i<mAnimationTextViews.length; i++) {
            if (index == i) {
                mAnimationTextViews[i].setTextColor(getResources().getColor(UiUtilities.resolveAttributeToResourceId(getTheme(), R.attr.textReaderBody_4)));
            } else {
                mAnimationTextViews[i].setTextColor(getResources().getColor(UiUtilities.resolveAttributeToResourceId(getTheme(), R.attr.textReaderBody_3)));
            }
        }
    }

    private void chanageScreen(int index, int value) {
        updateScreen(index);
        ReaderPrefUtils.markScreenIndex(this, index);
        ReaderPrefUtils.markScreenValue(this, value);

        // TODO
    }

    private void updateScreen(int index) {
        for (int i=0; i<mScreenTextViews.length; i++) {
            if (index == i) {
                mScreenTextViews[i].setTextColor(getResources().getColor(UiUtilities.resolveAttributeToResourceId(getTheme(), R.attr.textReaderBody_4)));
            } else {
                mScreenTextViews[i].setTextColor(getResources().getColor(UiUtilities.resolveAttributeToResourceId(getTheme(), R.attr.textReaderBody_3)));
            }
        }
    }

    @Override
    public void OnLeftIconClick() {
        finish();
    }

    @Override
    public void OnRightIconClick() {

    }

    @Override public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.wrapper_reader_settings_font) {
            startActivity(new Intent(this, SettingFontManageActivity.class));
        } else if (id == R.id.wrapper_settings_read_line_spacing_1) {
            changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_0);
        } else if (id == R.id.wrapper_settings_read_line_spacing_2) {
            changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_1);
        } else if (id == R.id.wrapper_settings_read_line_spacing_3) {
            changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_2);
        } else if (id == R.id.wrapper_settings_read_margin_spacing_1) {
            changeMarginSpacing(ReaderPrefUtils.MARGIN_SPACING_INDEX_0);
        } else if (id == R.id.wrapper_settings_read_margin_spacing_2) {
            changeMarginSpacing(ReaderPrefUtils.MARGIN_SPACING_INDEX_1);
        } else if (id == R.id.wrapper_settings_read_margin_spacing_3) {
            changeMarginSpacing(ReaderPrefUtils.MARGIN_SPACING_INDEX_2);
        } else if (id == R.id.text_settings_read_animation_slide) {
            chanageAnimation(ReaderPrefUtils.ANIMATION_INDEX_0);
        } else if (id == R.id.text_settings_read_animation_shift) {
            chanageAnimation(ReaderPrefUtils.ANIMATION_INDEX_1);
        } else if (id == R.id.text_settings_read_animation_curl) {
            chanageAnimation(ReaderPrefUtils.ANIMATION_INDEX_2);
        } else if (id == R.id.text_settings_read_animation_none) {
            chanageAnimation(ReaderPrefUtils.ANIMATION_INDEX_3);
        } else if (id == R.id.wrapper_settings_read_section_spacing_1) {
            changeSectionSpacing(ReaderPrefUtils.SECTION_SPACING_INDEX_0);
        } else if (id == R.id.wrapper_settings_read_section_spacing_2) {
            changeSectionSpacing(ReaderPrefUtils.SECTION_SPACING_INDEX_1);
        } else if (id == R.id.wrapper_settings_read_section_spacing_3) {
            changeSectionSpacing(ReaderPrefUtils.SECTION_SPACING_INDEX_2);
        } else if (id == R.id.text_settings_read_screen_second_minutes) {
            chanageScreen(ReaderPrefUtils.SCREEN_INDEX_0, ReaderPrefUtils.SCREEN_VALUE_0);
        } else if (id == R.id.text_settings_read_screen_five_minutes) {
            chanageScreen(ReaderPrefUtils.SCREEN_INDEX_1, ReaderPrefUtils.SCREEN_VALUE_1);
        } else if (id == R.id.text_settings_read_screen_ten_minutes) {
            chanageScreen(ReaderPrefUtils.SCREEN_INDEX_2, ReaderPrefUtils.SCREEN_VALUE_2);
        } else if (id == R.id.text_settings_read_screen_none) {
            chanageScreen(ReaderPrefUtils.SCREEN_INDEX_3, ReaderPrefUtils.SCREEN_VALUE_3);
        }

        else if (id == R.id.wrapper_settings_read_first_line_indent_1) {

        } else if (id == R.id.wrapper_settings_read_first_line_indent_2) {

        } else if (id == R.id.wrapper_settings_read_first_line_indent_3) {

        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ReaderPrefUtils.markRecentlyRead(this, isChecked);
    }

}
