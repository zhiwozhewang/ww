/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
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

import android.view.View;
import android.widget.RelativeLayout;

import org.geometerplus.fbreader.fbreader.ActionCode;
import org.geometerplus.fbreader.fbreader.FBReaderApp;

class SelectionPopup extends ButtonsPopupPanel implements View.OnClickListener {
	final static String ID = "SelectionPopup";

	SelectionPopup(FBReaderApp fbReader) {
		super(fbReader);
	}

	@Override
	public String getId() {
		return ID;
	}

	@Override
	public void createControlPanel(FBReader activity, RelativeLayout root) {
		if (myWindow != null && activity == myWindow.getActivity()) {
			return;
		}

		myWindow = new PopupWindow(activity, root, PopupWindow.Location.Floating);

        final View layout = activity.getLayoutInflater().inflate(R.layout.l_selection_popup, myWindow, false);

        layout.findViewById(R.id.text_selection_popup_copy).setOnClickListener(this);
        layout.findViewById(R.id.text_selection_popup_share).setOnClickListener(this);
        layout.findViewById(R.id.text_selection_popup_search).setOnClickListener(this);

        myWindow.addView(layout);

//		addButton(ActionCode.SELECTION_COPY_TO_CLIPBOARD, true, R.drawable.selection_copy);
//		addButton(ActionCode.SELECTION_SHARE, true, R.drawable.selection_share);
//		addButton(ActionCode.SELECTION_TRANSLATE, true, R.drawable.selection_translate);
//		addButton(ActionCode.SELECTION_BOOKMARK, true, R.drawable.selection_bookmark);
//		addButton(ActionCode.SELECTION_CLEAR, true, R.drawable.selection_close);
	}

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.text_selection_popup_copy) {
            Application.runAction(ActionCode.SELECTION_COPY_TO_CLIPBOARD);
        } else if (id == R.id.text_selection_popup_search) {
            Application.runAction(ActionCode.SEARCH);
        } else if (id == R.id.text_selection_popup_share) {
            Application.runAction(ActionCode.SELECTION_SHARE);
        }
        storePosition();
        StartPosition = null;
        Application.hideActivePopup();
    }

    public void move(int selectionStartY, int selectionEndY) {
		if (myWindow == null) {
			return;
		}

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

		int verticalPosition;
		final int screenHeight = ((View)myWindow.getParent()).getHeight();
		final int diffTop = screenHeight - selectionEndY;
		final int diffBottom = selectionStartY;

        verticalPosition = RelativeLayout.CENTER_VERTICAL;
        if (diffTop > diffBottom) {
            if (diffTop > myWindow.getHeight() + 100) {
                layoutParams.bottomMargin = 150;
                verticalPosition = RelativeLayout.ALIGN_PARENT_BOTTOM;
            }

		} else {
            if (diffBottom > myWindow.getHeight() + 100) {
                layoutParams.topMargin = 150;
                verticalPosition = RelativeLayout.ALIGN_PARENT_TOP;
            }
		}

		layoutParams.addRule(verticalPosition);
		myWindow.setLayoutParams(layoutParams);
	}
}
