/**
 * Created by YuGang Yang on September 24, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.activities;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.loopeer.android.apps.lreader.ui.views.ReaderTitlebarView;
import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.R;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.fbreader.fbreader.options.ViewOptions;
import org.geometerplus.zlibrary.text.view.style.ZLTextBaseStyle;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;

import java.util.ArrayList;

public class SettingFontManageActivity extends BaseReaderActivity implements ReaderTitlebarView.OnTitlebarIconClickListener {

    private ReaderTitlebarView mTitlebarView;
    private ListView mListView;

    private ArrayList<Font> mFonts = new ArrayList<Font>();
    private FontAdapter mFontAdapter;

    public static class Font {
        public String mTitle;
        public String mValue;
        public String mFontPath;

        public int mType; // 0代表系统， 1代表自定义
        public boolean mIsChecked = false;
        public Font() {
        }

        public Font(String title, String value, String fontPath, int type) {
            mTitle = title;
            mValue = value;
            mFontPath = fontPath;
            mType = type;
        }
    }

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
        setContentView(R.layout.activity_setting_font);
        mTitlebarView = UiUtilities.getView(this, R.id.titlebar);
        mListView = UiUtilities.getView(this, android.R.id.list);
        mTitlebarView.setOnTitlebarIconClickListener(this);

        initFonts();
    }

    private void initFonts() {
        mFonts.add(new Font("系统", "sans-serif", null, 0));
        mFonts.add(new Font("宋体", "宋体", "fonts/LSun", 1));
        mFonts.add(new Font("仿宋", "仿宋", "fonts/LFang", 1));
        mFonts.add(new Font("黑体", "黑体", "fonts/LHei", 1));
        mFonts.add(new Font("华文行楷", "华文行楷", "fonts/LSTXingKai", 1));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        String value = baseStyle.FontFamilyOption.getValue();
        for (Font font : mFonts) {
            if (font.mValue.equals(value)) {
                font.mIsChecked = true;
                break;
            }
        }

        mFontAdapter = new FontAdapter(this);
        mListView.setAdapter(mFontAdapter);
    }

    @Override
    public void OnLeftIconClick() {
        finish();
    }

    @Override
    public void OnRightIconClick() {

    }


    private class FontAdapter extends BaseAdapter {
        private Context mContext;

        public FontAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return mFonts != null ? mFonts.size() : 0;
        }

        @Override
        public Font getItem(int position) {
            return mFonts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null || !(convertView.getTag() instanceof FontDownloadedHolder)) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.l_font_item_donwloaded, null);
                convertView.setTag(new FontDownloadedHolder(convertView));
            }
            FontDownloadedHolder fontDownloadedHolder = (FontDownloadedHolder) convertView.getTag();
            fontDownloadedHolder.setData(getItem(position));
            fontDownloadedHolder.layout();
            return convertView;
        }
    }

    class FontDownloadedHolder implements View.OnClickListener {
        public TextView mTitleTextView;
        public CheckBox mCheckbox;

        private Font mFont;

        FontDownloadedHolder(View convertView) {
            mTitleTextView = UiUtilities.getView(convertView, R.id.text_font_title);
            mCheckbox = UiUtilities.getView(convertView, R.id.checkbox_font);
            convertView.setOnClickListener(this);
        }

        public void setData(Font font) {
            mFont = font;
        }

        public void layout() {
            if (mFont == null) {
                return;
            }
            mTitleTextView.setText(mFont.mTitle);
            if (mFont.mIsChecked) {
                mCheckbox.setChecked(true);
            } else {
                mCheckbox.setChecked(false);
            }

            mCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        selectFonts(mFont);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (!mFont.mIsChecked) {
                selectFonts(mFont);
            }
        }
    }

    private void selectFonts(Font font) {
        boolean canNotify = !font.mIsChecked;
        changeFont(font);
        for (Font f : mFonts) {
            if (!f.mValue.equals(font.mValue)) {
                f.mIsChecked = false;
            } else {
                f.mIsChecked = true;
            }
        }

        if (canNotify) {
            mFontAdapter.notifyDataSetChanged();
        }
    }

    final ViewOptions viewOptions = new ViewOptions();
    final ZLTextStyleCollection collection = viewOptions.getTextStyleCollection();
    final ZLTextBaseStyle baseStyle = collection.getBaseStyle();
    private void changeFont(Font font) {
        baseStyle.FontFamilyOption.setValue(font.mValue);
    }


}
