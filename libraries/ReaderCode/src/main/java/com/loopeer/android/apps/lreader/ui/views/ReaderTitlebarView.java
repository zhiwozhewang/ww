/**
 * Created by YuGang Yang on Aug 9, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.R;

public class ReaderTitlebarView extends LinearLayout implements View.OnClickListener {

	private RelativeLayout mLeftView;
	private ImageView mLeftIcon;

	private TextView mTitle;
	

	private OnTitlebarIconClickListener mClickListener;
	
	public static interface OnTitlebarIconClickListener {
		void OnLeftIconClick();
		void OnRightIconClick();
	}
	
	/**
	 * @param context
	 */
	public ReaderTitlebarView(Context context) {
		this(context, null);
	}
	
	/**
	 * @param context
	 * @param attrs
	 */
	public ReaderTitlebarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public ReaderTitlebarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs, defStyleAttr);
	}
	
	private void setupViews() {
		mLeftView = UiUtilities.getView(this, R.id.view_titlebar_left);
		mLeftIcon = UiUtilities.getView(this, R.id.image_titlebar_left);

        mTitle = UiUtilities.getView(this, R.id.text_titlebar_title);

		mLeftView.setOnClickListener(this);
	}
	
	private void initialize(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater.from(context).inflate(R.layout.reader_titlebar, this);

        setupViews();
        
        if (attrs == null) return;

        // Load attributes
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.ReaderTitlebar, defStyle, 0);

        if (a == null) return;

        setTitle(a.getString(R.styleable.ReaderTitlebar_readerTitlebarTitle));
        setLeftIcon(a.getDrawable(R.styleable.ReaderTitlebar_readerTitlebarLeftIcon));
        a.recycle();
    }

	/**
	 * @param drawable
	 */
	private void setLeftIcon(Drawable drawable) {
		if (drawable == null) {
			mLeftIcon.setVisibility(View.GONE);
			mLeftView.setVisibility(View.GONE);
            return;
        }
        UiUtilities.setBackgroundCompat(mLeftIcon, drawable);
        mLeftIcon.setVisibility(View.VISIBLE);
		mLeftView.setVisibility(View.VISIBLE);
	}

	/**
	 * @param title
	 */
	private void setTitle(CharSequence title) {
		if (TextUtils.isEmpty(title)) {
			mTitle.setVisibility(View.GONE);
            return;
        }
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setText(title);
	}
	
	public void setOnTitlebarIconClickListener(OnTitlebarIconClickListener l) {
		mClickListener = l;
	}
	
	@Override
	public void onClick(View v) {
        if (mClickListener == null) return;
        final int id = v.getId();
        if (id == R.id.view_titlebar_left) {
            mClickListener.OnLeftIconClick();
        }
	}
	
}
