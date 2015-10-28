/**
 * Created by YuGang Yang on August 17, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package com.loopeer.android.apps.lreader.ui.views;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import org.geometerplus.android.fbreader.R;

public class LReaderSelectionPopup extends PopupWindow {

    private volatile RelativeLayout myRoot;

    public LReaderSelectionPopup(Context context, RelativeLayout root) {
        this(context, root, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public LReaderSelectionPopup(Context context, RelativeLayout root, int width, int height) {
        myRoot = root;

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setWidth(width);
        setHeight(height);
        setBackgroundDrawable(new BitmapDrawable());
        setAnimationStyle(R.style.PopupAnimation);

        setContentView(LayoutInflater.from(context).inflate(R.layout.l_selection_popup, null));
    }

    public void show(int selectionStartY, int selectionEndY){
        final int verticalPosition;
        final int screenHeight = myRoot.getHeight();
        final int diffTop = screenHeight - selectionEndY;
        final int diffBottom = selectionStartY;
        if (diffTop > diffBottom) {
            showAtLocation(myRoot, Gravity.TOP,
                    50, selectionStartY);
        } else {
            showAtLocation(myRoot, Gravity.BOTTOM,
                    50, selectionEndY);
        }
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

}
