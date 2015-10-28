/*
 * Copyright (C) 2009-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.android.fbreader;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.loopeer.android.apps.lreader.ui.activities.SettingReadActivity;
import com.loopeer.android.apps.lreader.utilities.ReaderPrefUtils;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.fbreader.bookmodel.TOCTree;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.resources.ZLResource;
import org.geometerplus.zlibrary.core.util.ZLColor;
import org.geometerplus.zlibrary.text.view.ZLTextView;
import org.geometerplus.zlibrary.text.view.ZLTextWordCursor;

final class NavigationPopup {
	private PopupWindow myWindow;
	private ZLTextWordCursor myStartPosition;
	private final FBReaderApp myFBReader;
	private Button myResetButton;
    private FBReader BaseActivity;


	NavigationPopup(FBReader BaseActivity, FBReaderApp fbReader) {
		myFBReader = fbReader;
        this.BaseActivity = BaseActivity;
	}

	public void runNavigation(FBReader activity, RelativeLayout root) {
		createControlPanel(activity, root);
		myStartPosition = new ZLTextWordCursor(myFBReader.getTextView().getStartCursor());
		myWindow.show();
		setupNavigation();
	}

	public void update() {
		if (myWindow != null) {
			setupNavigation();
		}
	}

	public void stopNavigation() {
		if (myWindow == null) {
			return;
		}

		if (myStartPosition != null &&
			!myStartPosition.equals(myFBReader.getTextView().getStartCursor())) {
			myFBReader.addInvisibleBookmark(myStartPosition);
			myFBReader.storePosition();
		}

        ReaderPrefUtils.markScreenBrightnessLevel(BaseActivity, myStartBrightness);

        myWindow.hide();
		myWindow = null;
	}

    private View mWrapperActions;
    private View mProgressWrapper;
    private ImageView mColorProfileIcon;
    private TextView mColorProfileText;
    private ImageView mScreenIcon;
    private TextView mScreenText;

    private boolean mIsShowProgress = false;
    private void showProgress() {
        if (mIsShowProgress) {
            return;
        }

        updateProgressWrapper();

        mWrapperActions.setVisibility(View.GONE);
        mProgressWrapper.setVisibility(View.VISIBLE);

        mIsShowProgress = true;
    }

    private void updateProgressWrapper() {
        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            wrapperNavigateToc.setBackgroundResource(R.drawable.ic_l_reader_progress_day_bg);
            sliderWrapper.setBackgroundResource(R.color.color_efefef);
            slider.setProgressDrawable(BaseActivity.getResources().getDrawable(R.drawable.seekbar_progress_drawable));
            slider.setThumb(BaseActivity.getResources().getDrawable(R.drawable.ic_l_reader_seekbar_thumb_day));
        } else if (ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            wrapperNavigateToc.setBackgroundResource(R.drawable.ic_l_reader_progress_night);
            sliderWrapper.setBackgroundResource(R.color.color_434853);
            slider.setProgressDrawable(BaseActivity.getResources().getDrawable(R.drawable.seekbar_progress_drawable_night));
            slider.setThumb(BaseActivity.getResources().getDrawable(R.drawable.ic_l_reader_seekbar_thumb_night));
        }
    }

    private void hideProgress() {
        if (!mIsShowProgress) {
            return;
        }
        mWrapperActions.setVisibility(View.VISIBLE);
        mProgressWrapper.setVisibility(View.GONE);
        mIsShowProgress = false;
    }

	public void createControlPanel(FBReader activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.BottomFlat);

		final View layout = activity.getLayoutInflater().inflate(R.layout.navigate, myWindow, false);

        setupViews(layout);
        setupCheckbox();
        updateViewBackground();
        updateLineSpacing(ReaderPrefUtils.getLineSpacingIndex(BaseActivity));

        updateBackground();
        initColorProfile();
        initOrientation();

        UiUtilities.getView(layout, R.id.wrapper_reader_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                NavUtil.startSearchActivity(BaseActivity);
                BaseActivity.hideBars();
                BaseActivity.onSearchRequested();
            }
        });

        UiUtilities.getView(layout, R.id.wrapper_reader_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSettings();
            }
        });

        UiUtilities.getView(layout, R.id.wrapper_reader_color_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeColorProfile();
            }
        });

        UiUtilities.getView(layout, R.id.wrapper_reader_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeOrientation();
            }
        });

        UiUtilities.getView(layout, R.id.wrapper_reader_progress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress();
            }
        });

		final SeekBar slider = (SeekBar)layout.findViewById(R.id.navigation_slider);
		final TextView progressTextView = (TextView)layout.findViewById(R.id.text_navigation_progress);
		final TextView tocTextView = (TextView)layout.findViewById(R.id.text_navigation_toc);

		slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private void gotoPage(int page) {
                final ZLTextView view = myFBReader.getTextView();
                if (page == 1) {
                    view.gotoHome();
                } else {
                    view.gotoPage(page);
                }
                myFBReader.getViewWidget().reset();
                myFBReader.getViewWidget().repaint();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    final int page = progress + 1;
                    final int pagesNumber = seekBar.getMax() + 1;
                    gotoPage(page);
                    progressTextView.setText(makeProgressText(page, pagesNumber));
                    tocTextView.setText(getCurrentToc());
                }
            }
        });

		myResetButton = (Button)layout.findViewById(R.id.navigation_reset_button);
		myResetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (myStartPosition != null) {
					myFBReader.getTextView().gotoPosition(myStartPosition);
				}
				myFBReader.getViewWidget().reset();
				myFBReader.getViewWidget().repaint();
				update();
			}
		});
		final ZLResource buttonResource = ZLResource.resource("dialog").getResource("button");
		myResetButton.setText(buttonResource.getResource("resetPosition").getValue());

		myWindow.addView(layout);
	}


    private LinearLayout mSettingsWrapper;
    private RelativeLayout mSettingsWrapper1;
    private LinearLayout mSettingsWrapper2;
    private LinearLayout mSettingsWrapper3;
    private LinearLayout mSettingsWrapper4;
    private LinearLayout mSettingsWrapper5;

    private CheckBox mCheckboxBrightness;
    private TextView mBrightnessTitle;
    private ImageView mBrightnessLeft;
    private ImageView mBrightnessRight;
    private SeekBar mBrightnessSeekbar;

    private LinearLayout[] mLineSpaingLinearLayouts = new LinearLayout[3];
    private LinearLayout[] mCircleLinearLayouts = new LinearLayout[6];
    private View[] mCircleViews = new View[6];
    private ImageView[] mLineSpaingLinearViews = new ImageView[3];
    private int[] mCircleViewBackground = new int[] {
            R.drawable.circle_white,
            R.drawable.circle_ffe0b2,
            R.drawable.circle_f1b3bc,
            R.drawable.circle_00695c,
            R.drawable.circle_795548,
            R.drawable.circle_2a2e36
    };
    private int[] mCircleViewBackgroundChecked = new int[] {
            R.drawable.circle_white_checked,
            R.drawable.circle_ffe0b2_checked,
            R.drawable.circle_f1b3bc_checked,
            R.drawable.circle_00695c_checked,
            R.drawable.circle_795548_checked,
            R.drawable.circle_2a2e36_checked
    };
    private int[] mBackgroundColors = new int[] {
            android.R.color.white,
            R.color.color_ffe0b2,
            R.color.color_f1b3bc,
            R.color.color_00695c,
            R.color.color_795548,
            R.color.color_5f382e
    };
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

    private int myStartBrightness;

    private void setupViews(View layout) {
        mProgressWrapper = UiUtilities.getView(layout, R.id.view_reader_progress);
        mWrapperActions = UiUtilities.getView(layout, R.id.wrapper_navigation_popup);

        mColorProfileIcon = UiUtilities.getView(layout, R.id.image_reader_color_profile);
        mColorProfileText = UiUtilities.getView(layout, R.id.text_reader_color_profile);
        mScreenIcon = UiUtilities.getView(layout, R.id.image_reader_screen);
        mScreenText = UiUtilities.getView(layout, R.id.text_reader_screen);

        mSettingsWrapper = UiUtilities.getView(layout, R.id.view_reader_setting);
        mSettingsWrapper1 = UiUtilities.getView(layout, R.id.wrapper_reader_settings_1);
        mSettingsWrapper2 = UiUtilities.getView(layout, R.id.wrapper_reader_settings_2);
        mSettingsWrapper3 = UiUtilities.getView(layout, R.id.wrapper_reader_settings_3);
        mSettingsWrapper4 = UiUtilities.getView(layout, R.id.wrapper_reader_settings_4);
        mSettingsWrapper5 = UiUtilities.getView(layout, R.id.wrapper_reader_settings_5);

        mCheckboxBrightness = UiUtilities.getView(layout, R.id.checkbox_settings_brightness);
        mBrightnessTitle = UiUtilities.getView(layout, R.id.text_brightness_title);
        mBrightnessLeft = UiUtilities.getView(layout, R.id.image_settings_brightness_left);
        mBrightnessRight = UiUtilities.getView(layout, R.id.image_settings_brightness_right);
        mBrightnessSeekbar = UiUtilities.getView(layout, R.id.seekbar_brightness);

        LinearLayout spacingLinearLayout = UiUtilities.getView(layout, R.id.wrapper_setting_line_spacing1);
        ImageView spacingImageView  = UiUtilities.getView(layout, R.id.image_setting_line_spacing1);
        mLineSpaingLinearLayouts[0] = spacingLinearLayout;
        mLineSpaingLinearViews[0] = spacingImageView;

        spacingLinearLayout = UiUtilities.getView(layout, R.id.wrapper_setting_line_spacing2);
        spacingImageView  = UiUtilities.getView(layout, R.id.image_setting_line_spacing2);
        mLineSpaingLinearLayouts[1] = spacingLinearLayout;
        mLineSpaingLinearViews[1] = spacingImageView;

        spacingLinearLayout = UiUtilities.getView(layout, R.id.wrapper_setting_line_spacing3);
        spacingImageView  = UiUtilities.getView(layout, R.id.image_setting_line_spacing3);
        mLineSpaingLinearLayouts[2] = spacingLinearLayout;
        mLineSpaingLinearViews[2] = spacingImageView;

        LinearLayout circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_1);
        View circleView  = UiUtilities.getView(layout, R.id.view_reader_settings_circle_1);
        mCircleViews[0] = circleView;
        mCircleLinearLayouts[0] = circleLinearLayout;

        circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_2);
        circleView = UiUtilities.getView(layout, R.id.view_reader_settings_circle_2);
        mCircleViews[1] = circleView;
        mCircleLinearLayouts[1] = circleLinearLayout;

        circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_3);
        circleView = UiUtilities.getView(layout, R.id.view_reader_settings_circle_3);
        mCircleViews[2] = circleView;
        mCircleLinearLayouts[2] = circleLinearLayout;

        circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_4);
        circleView = UiUtilities.getView(layout, R.id.view_reader_settings_circle_4);
        mCircleViews[3] = circleView;
        mCircleLinearLayouts[3] = circleLinearLayout;

        circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_5);
        circleView = UiUtilities.getView(layout, R.id.view_reader_settings_circle_5);
        mCircleViews[4] = circleView;
        mCircleLinearLayouts[4] = circleLinearLayout;

        circleLinearLayout = UiUtilities.getView(layout, R.id.wrapper_reader_settings_circle_6);
        circleView = UiUtilities.getView(layout, R.id.view_reader_settings_circle_6);
        mCircleViews[5] = circleView;
        mCircleLinearLayouts[5] = circleLinearLayout;

        mBrightnessSeekbar.setProgress(ReaderPrefUtils.getScreenBrightnessLevel(BaseActivity));

        toggleSettings();
        toggleMoreSettings();

        for (LinearLayout ll : mCircleLinearLayouts) {
            ll.setOnClickListener(onClickListener);
        }

        for (LinearLayout ll : mLineSpaingLinearLayouts) {
            ll.setOnClickListener(onClickListener);
        }

        mSettingsWrapper5.setOnClickListener(onClickListener);
        mCheckboxBrightness.setOnCheckedChangeListener(onCheckedChangeListener);
        mBrightnessSeekbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        UiUtilities.getView(layout, R.id.wrapper_reader_settings_more).setOnClickListener(onClickListener);
    }

    private void setupCheckbox() {
        if (ReaderPrefUtils.isAllowScreenBrightnessAdjustment(BaseActivity)) {
            mCheckboxBrightness.setChecked(false);
            mBrightnessSeekbar.setEnabled(true);
            mBrightnessSeekbar.setProgressDrawable(BaseActivity.getResources().getDrawable(R.drawable.seekbar_progress_drawable));
            mBrightnessSeekbar.setThumb(BaseActivity.getResources().getDrawable(R.drawable.ic_l_reader_seekbar_thumb_day));
            mBrightnessTitle.setTextColor(BaseActivity.getResources().getColor(R.color.color_bbbbbb));
            mBrightnessLeft.setImageResource(R.drawable.ic_l_settings_brightness_enable_day);
            mBrightnessRight.setImageResource(R.drawable.ic_l_settings_brightness_enable_large_day);

            myStartBrightness = ReaderPrefUtils.getScreenBrightnessLevel(BaseActivity);
            BaseActivity.setScreenBrightness(myStartBrightness);
            BaseActivity.setButtonLight(false);
        } else {
            mCheckboxBrightness.setChecked(true);
            mBrightnessSeekbar.setEnabled(false);
            mBrightnessSeekbar.setProgressDrawable(BaseActivity.getResources().getDrawable(R.drawable.seekbar_progress_drawable_disable));
            mBrightnessSeekbar.setThumb(BaseActivity.getResources().getDrawable(R.drawable.ic_l_reader_seekbar_thumb_day));
            mBrightnessTitle.setTextColor(BaseActivity.getResources().getColor(R.color.color_5fb7e7));
            mBrightnessLeft.setImageResource(R.drawable.ic_l_settings_brightness_disable_day);
            mBrightnessRight.setImageResource(R.drawable.ic_l_settings_brightness_disable_large_day);

            BaseActivity.setScreenBrightnessAuto();
        }
    }



    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            myStartBrightness = progress;
            BaseActivity.setScreenBrightness(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            ReaderPrefUtils.markAllowScreenBrightnessAdjustment(BaseActivity, !isChecked);
            setupCheckbox();
        }
    };

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int id = v.getId();
            if (id == R.id.wrapper_reader_settings_5) {
                toggleMoreSettings();
            } else if (id == R.id.wrapper_reader_settings_more) {
                BaseActivity.startActivity(new Intent(BaseActivity, SettingReadActivity.class));
            } else if (id == R.id.wrapper_reader_settings_circle_1) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_0);
            } else if (id == R.id.wrapper_reader_settings_circle_2) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_1);
            } else if (id == R.id.wrapper_reader_settings_circle_3) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_2);
            } else if (id == R.id.wrapper_reader_settings_circle_4) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_3);
            } else if (id == R.id.wrapper_reader_settings_circle_5) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_4);
            } else if (id == R.id.wrapper_reader_settings_circle_6) {
                changeBackground(ReaderPrefUtils.BACKGROUND_COLOR_INDEX_5);
            } else if (id == R.id.wrapper_setting_line_spacing1) {
                changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_0);
            } else if (id == R.id.wrapper_setting_line_spacing2) {
                changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_1);
            } else if (id == R.id.wrapper_setting_line_spacing3) {
                changeLineSpacing(ReaderPrefUtils.LINE_SPACING_INDEX_2);
            }
        }
    };

    private void updateLineSpacing(int index) {
        for (int i=0; i<mLineSpaingLinearLayouts.length; i++) {
            if (index == i) {
                mLineSpaingLinearViews[index].setImageResource(mLineSpacingResChecked[index]);
            } else {
                mLineSpaingLinearViews[i].setImageResource(mLineSpacingRes[i]);
            }
        }
    }

    private void changeLineSpacing(int index) {
        updateLineSpacing(index);
        ReaderPrefUtils.markLineSpacingIndex(BaseActivity, index);
        final ZLIntegerRangeOption spaceOption = myFBReader.ViewOptions.getTextStyleCollection().getBaseStyle().LineSpaceOption;
        spaceOption.setValue(mLineSpacingValues[index]);

        // update view
        myFBReader.clearTextCaches();
        myFBReader.getViewWidget().repaint();
    }

    private void updateViewBackground() {
        int index;
        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            index = ReaderPrefUtils.getBackgroundColorDay(BaseActivity);
        } else {
            index = ReaderPrefUtils.getBackgroundColorNight(BaseActivity);
        }
        int len = mCircleViews.length;
        for (int i=0; i<len; i++) {
            if (index == i) {
                mCircleViews[i].setBackgroundResource(mCircleViewBackgroundChecked[index]);
            } else {
                mCircleViews[i].setBackgroundResource(mCircleViewBackground[i]);
            }
        }
    }

    private void changeBackground(int index) {
      if (ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
        changeColorProfile();
      }
        int len = mCircleViews.length;
        for (int i=0; i<len; i++) {
            if (index == i) {
                mCircleViews[i].setBackgroundResource(mCircleViewBackgroundChecked[index]);
            } else {
                mCircleViews[i].setBackgroundResource(mCircleViewBackground[i]);
            }
        }

        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            ColorProfile backgroundOption = ColorProfile.get(ColorProfile.DAY);
            backgroundOption.BackgroundOption.setValue(new ZLColor(BaseActivity.getResources().getColor(mBackgroundColors[index])));
            ReaderPrefUtils.markBackgroundColorDay(BaseActivity, index);
        } /*else {
           ColorProfile backgroundOption = ColorProfile.get(ColorProfile.NIGHT);
           backgroundOption.BackgroundOption.setValue(new ZLColor(BaseActivity.getResources().getColor(mBackgroundColors[index])));
            ReaderPrefUtils.markBackgroundColorNight(BaseActivity, index);
        }*/

        // update view
        myFBReader.getViewWidget().reset();
        myFBReader.getViewWidget().repaint();
    }

    private void toggleMoreSettings() {
        if (mSettingsWrapper4.getVisibility() == View.GONE) {
            UiUtilities.setVisibilitySafe(mSettingsWrapper3, View.VISIBLE);
            UiUtilities.setVisibilitySafe(mSettingsWrapper4, View.VISIBLE);
            UiUtilities.setVisibilitySafe(mSettingsWrapper5, View.GONE);
        } else {
            UiUtilities.setVisibilitySafe(mSettingsWrapper3, View.GONE);
            UiUtilities.setVisibilitySafe(mSettingsWrapper4, View.GONE);
            UiUtilities.setVisibilitySafe(mSettingsWrapper5, View.VISIBLE);
        }
    }

    private void toggleSettings() {
        if (mSettingsWrapper.getVisibility() == View.GONE) {
            UiUtilities.setVisibilitySafe(mSettingsWrapper, View.VISIBLE);
        } else {
            UiUtilities.setVisibilitySafe(mSettingsWrapper, View.GONE);
        }
    }

    private SeekBar slider;
    private LinearLayout sliderWrapper;
    private LinearLayout wrapperNavigateToc;
	private void setupNavigation() {
		slider = (SeekBar)myWindow.findViewById(R.id.navigation_slider);
        sliderWrapper = (LinearLayout)myWindow.findViewById(R.id.seekbar_wrapper);
        wrapperNavigateToc = (LinearLayout)myWindow.findViewById(R.id.wrapper_navigate_toc);
		final TextView textProgress = (TextView)myWindow.findViewById(R.id.text_navigation_progress);
		final TextView textToc = (TextView)myWindow.findViewById(R.id.text_navigation_toc);

		final ZLTextView textView = myFBReader.getTextView();
		final ZLTextView.PagePosition pagePosition = textView.pagePosition();

		if (slider.getMax() != pagePosition.Total - 1 || slider.getProgress() != pagePosition.Current - 1) {
			slider.setMax(pagePosition.Total - 1);
			slider.setProgress(pagePosition.Current - 1);
            textProgress.setText(makeProgressText(pagePosition.Current, pagePosition.Total));
            textToc.setText(getCurrentToc());
		}

		myResetButton.setEnabled(
			myStartPosition != null &&
			!myStartPosition.equals(myFBReader.getTextView().getStartCursor())
		);
	}

	private String makeProgressText(int page, int pagesNumber) {
		final StringBuilder builder = new StringBuilder();
		builder.append(page);
		builder.append("/");
		builder.append(pagesNumber);
		return builder.toString();
	}


    private String getCurrentToc() {
        final TOCTree tocElement = myFBReader.getCurrentTOCElement();
        if (tocElement != null) {
            return tocElement.getText();
        }

        return null;
    }

    private void updateBackground() {
        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mWrapperActions.setBackgroundResource(R.color.color_efefef);
        } else if (ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mWrapperActions.setBackgroundResource(R.color.color_434853);
        }
    }

    private void initColorProfile() {
        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mColorProfileIcon.setImageResource(R.drawable.ic_l_reader_night_day);
            mColorProfileText.setText("夜间");
        } else if (ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mColorProfileIcon.setImageResource(R.drawable.ic_l_reader_day_day);
            mColorProfileText.setText("白天");
        }
    }

    private void changeColorProfile() {
        if (ColorProfile.DAY.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mColorProfileIcon.setImageResource(R.drawable.ic_l_reader_day_day);
            mColorProfileText.setText("白天");
            myFBReader.ViewOptions.ColorProfileName.setValue(ColorProfile.NIGHT);

          // fix bug
          ColorProfile backgroundOption = ColorProfile.get(ColorProfile.NIGHT);
          if (backgroundOption.BackgroundOption.getValue().intValue() != BaseActivity.getResources().getColor(R.color.color_2a2e36)) {
            backgroundOption.BackgroundOption.setValue(new ZLColor(BaseActivity.getResources().getColor(R.color.color_2a2e36)));
          }
        } else if (ColorProfile.NIGHT.equals(myFBReader.ViewOptions.ColorProfileName.getValue())) {
            mColorProfileIcon.setImageResource(R.drawable.ic_l_reader_night_day);
            mColorProfileText.setText("夜间");
            myFBReader.ViewOptions.ColorProfileName.setValue(ColorProfile.DAY);
        }

        BaseActivity.updateNavigatinoBar();
        updateBackground();
        myFBReader.getViewWidget().reset();
        myFBReader.getViewWidget().repaint();
    }

    private void initOrientation() {
        if (ZLibrary.SCREEN_ORIENTATION_LANDSCAPE.equals(ZLibrary.Instance().getOrientationOption().getValue())) { // 横屏
            mScreenIcon.setImageResource(R.drawable.ic_l_reader_portrait_day);
            mScreenText.setText("竖屏");
        } else { // 竖屏
            mScreenIcon.setImageResource(R.drawable.ic_l_reader_landscape_day);
            mScreenText.setText("横屏");
        }
    }

    private void changeOrientation() {
        if (ZLibrary.SCREEN_ORIENTATION_LANDSCAPE.equals(ZLibrary.Instance().getOrientationOption().getValue())) { // 横屏
            mScreenIcon.setImageResource(R.drawable.ic_l_reader_landscape_day);
            mScreenText.setText("横屏");

            setOrientation(BaseActivity, ZLibrary.SCREEN_ORIENTATION_PORTRAIT);
            ZLibrary.Instance().getOrientationOption().setValue(ZLibrary.SCREEN_ORIENTATION_PORTRAIT);
        } else { // 竖屏
            mScreenIcon.setImageResource(R.drawable.ic_l_reader_portrait_day);
            mScreenText.setText("竖屏");

            setOrientation(BaseActivity, ZLibrary.SCREEN_ORIENTATION_LANDSCAPE);
            ZLibrary.Instance().getOrientationOption().setValue(ZLibrary.SCREEN_ORIENTATION_LANDSCAPE);
        }

        myFBReader.onRepaintFinished();
    }

    static void setOrientation(Activity activity, String optionValue) {
        int orientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        if (ZLibrary.SCREEN_ORIENTATION_SENSOR.equals(optionValue)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
        } else if (ZLibrary.SCREEN_ORIENTATION_PORTRAIT.equals(optionValue)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if (ZLibrary.SCREEN_ORIENTATION_LANDSCAPE.equals(optionValue)) {
            orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else if (ZLibrary.SCREEN_ORIENTATION_REVERSE_PORTRAIT.equals(optionValue)) {
            orientation = 9; // ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
        } else if (ZLibrary.SCREEN_ORIENTATION_REVERSE_LANDSCAPE.equals(optionValue)) {
            orientation = 8; // ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        }
        activity.setRequestedOrientation(orientation);
    }

    public boolean hasProgressShown() {
        return mIsShowProgress;
    }

}
