/**
 * Created by YuGang Yang on August 27, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Utilities and constants related to app preferences.
 */
public class ReaderPrefUtils {

    public static final String AllowScreenBrightnessAdjustment = "AllowScreenBrightnessAdjustment";
    public static final String ScreenBrightnessLevel = "ScreenBrightnessLevel";

    public static final String BACKGROUND_COLOR_NIGHT = "background_color_night";
    public static final String BACKGROUND_COLOR_DAY = "background_color_day";
    public static final int BACKGROUND_COLOR_INDEX_0 = 0;
    public static final int BACKGROUND_COLOR_INDEX_1 = 1;
    public static final int BACKGROUND_COLOR_INDEX_2 = 2;
    public static final int BACKGROUND_COLOR_INDEX_3 = 3;
    public static final int BACKGROUND_COLOR_INDEX_4 = 4;
    public static final int BACKGROUND_COLOR_INDEX_5 = 5;

    public static final String LINE_SPACING = "line_spacing";
    public static final int LINE_SPACING_INDEX_0 = 0;
    public static final int LINE_SPACING_INDEX_1 = 1;
    public static final int LINE_SPACING_INDEX_2 = 2;
    public static final int LINE_SPACING_VALUE_0 = 12;
    public static final int LINE_SPACING_VALUE_1 = 16;
    public static final int LINE_SPACING_VALUE_2 = 18;

    public static final String MARGIN_SPACING = "margin_spacing";
    public static final int MARGIN_SPACING_INDEX_0 = 0;
    public static final int MARGIN_SPACING_INDEX_1 = 1;
    public static final int MARGIN_SPACING_INDEX_2 = 2;

    public static final String SECTION_SPACING = "section_spacing";
    public static final int SECTION_SPACING_INDEX_0 = 0;
    public static final int SECTION_SPACING_INDEX_1 = 1;
    public static final int SECTION_SPACING_INDEX_2 = 2;

    public static final String SCREEN_INDEX = "screen_index";
    public static final int SCREEN_INDEX_0 = 0;
    public static final int SCREEN_INDEX_1 = 1;
    public static final int SCREEN_INDEX_2 = 2;
    public static final int SCREEN_INDEX_3 = 3;

    public static final String SCREEN_VALUE = "screen_value";
    public static final int SCREEN_VALUE_0 = 2 * 60 * 1000;
    public static final int SCREEN_VALUE_1 = 5 * 60 * 1000;
    public static final int SCREEN_VALUE_2 = 10 * 60 * 1000;
    public static final int SCREEN_VALUE_3 = 20 * 60 * 1000;

    private static final String SYSTEM_LOCAL_TIME_VALUE = "system_local_time_value";

    public static final String ANIMATION_SPACING = "animation";
    public static final int ANIMATION_INDEX_0 = 0;
    public static final int ANIMATION_INDEX_1 = 1;
    public static final int ANIMATION_INDEX_2 = 2;
    public static final int ANIMATION_INDEX_3 = 3;

    private static final String RECENTLY_READ = "recently_read";

    public static void init(final Context context) {
        //
    }

    public static boolean isAllowScreenBrightnessAdjustment(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(AllowScreenBrightnessAdjustment, true);
    }

    public static void markAllowScreenBrightnessAdjustment(final Context context, boolean isBrightnessAdjustmentInProgress) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(AllowScreenBrightnessAdjustment, isBrightnessAdjustmentInProgress).apply();
    }

    public static int getScreenBrightnessLevel(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(ScreenBrightnessLevel, 50);
    }
    public static void markScreenBrightnessLevel(final Context context, int screenBrightnessLevel) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(ScreenBrightnessLevel, screenBrightnessLevel).apply();
    }

    public static int getBackgroundColorDay(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(BACKGROUND_COLOR_DAY, BACKGROUND_COLOR_INDEX_0);
    }
    public static void markBackgroundColorDay(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(BACKGROUND_COLOR_DAY, index).apply();
    }

    public static int getBackgroundColorNight(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(BACKGROUND_COLOR_NIGHT, BACKGROUND_COLOR_INDEX_0);
    }
    public static void markBackgroundColorNight(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(BACKGROUND_COLOR_NIGHT, index).apply();
    }

    public static int getLineSpacingIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(LINE_SPACING, LINE_SPACING_INDEX_0);
    }
    public static void markLineSpacingIndex(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(LINE_SPACING, index).apply();
    }

    public static int getMarginSpacingIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(MARGIN_SPACING, MARGIN_SPACING_INDEX_0);
    }
    public static void markMarginSpacingIndex(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(MARGIN_SPACING, index).apply();
    }
    public static int getAnimationIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(ANIMATION_SPACING, ANIMATION_INDEX_1);
    }
    public static void markAnimationIndex(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(ANIMATION_SPACING, index).apply();
    }

    public static boolean isRecentlyRead(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(RECENTLY_READ, true);
    }
    public static void markRecentlyRead(final Context context, boolean isRecentlyRead) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putBoolean(RECENTLY_READ, isRecentlyRead).apply();
    }

    public static int getSectionSpacingIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SECTION_SPACING, SECTION_SPACING_INDEX_0);
    }
    public static void markSectionSpacingIndex(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(SECTION_SPACING, index).apply();
    }

    public static int getScreenIndex(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SCREEN_INDEX, SCREEN_INDEX_2);
    }
    public static void markScreenIndex(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(SCREEN_INDEX, index).apply();
    }

    public static int getScreenValue(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SCREEN_VALUE, SCREEN_VALUE_2);
    }
    public static void markScreenValue(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(SCREEN_VALUE, index).apply();
    }

    public static int getSystemLocalScreenTimeValue(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getInt(SYSTEM_LOCAL_TIME_VALUE, SCREEN_VALUE_2);
    }
    public static void markSystemLocalScreenTimeValue(final Context context, int index) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt(SYSTEM_LOCAL_TIME_VALUE, index).apply();
    }

}
