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

package org.geometerplus.fbreader.fbreader.options;

import org.geometerplus.fbreader.fbreader.FBView;
import org.geometerplus.zlibrary.core.library.ZLibrary;
import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;
import org.geometerplus.zlibrary.text.view.style.ZLTextStyleCollection;

public class ViewOptions {
	public final ZLBooleanOption TwoColumnView;
	public final ZLIntegerRangeOption LeftMargin;
	public final ZLIntegerRangeOption RightMargin;
	public final ZLIntegerRangeOption TopMargin;
	public final ZLIntegerRangeOption BottomMargin;
	public final ZLIntegerRangeOption SpaceBetweenColumns;
	public final ZLIntegerRangeOption ScrollbarType;
	public final ZLIntegerRangeOption FooterHeight;
	public final ZLIntegerRangeOption HeaderHeight;
	public final ZLStringOption ColorProfileName;

	private ColorProfile myColorProfile;
	private ZLTextStyleCollection myTextStyleCollection;
	private FooterOptions myFooterOptions;

	public ViewOptions() {
		final ZLibrary zlibrary = ZLibrary.Instance();

		final int dpi = zlibrary.getDisplayDPI();
		final int x = zlibrary.getWidthInPixels();
		final int y = zlibrary.getHeightInPixels();
		final int horMargin = Math.min(dpi / 5, Math.min(x, y) / 30);

		TwoColumnView =
			new ZLBooleanOption("Options", "TwoColumnView", x * x + y * y >= 42 * dpi * dpi);
		LeftMargin =
			new ZLIntegerRangeOption("Options", "LeftMargin", 0, 100, horMargin);
		RightMargin =
			new ZLIntegerRangeOption("Options", "RightMargin", 0, 100, horMargin);
		TopMargin =
			new ZLIntegerRangeOption("Options", "TopMargin", 0, 100, 15);
		BottomMargin =
			new ZLIntegerRangeOption("Options", "BottomMargin", 0, 100, 20);
		SpaceBetweenColumns =
			new ZLIntegerRangeOption("Options", "SpaceBetweenColumns", 0, 300, 3 * horMargin);
		ScrollbarType =
			new ZLIntegerRangeOption("Options", "ScrollbarType", 0, 3, FBView.SCROLLBAR_SHOW_AS_FOOTER);
		FooterHeight =
			new ZLIntegerRangeOption("Options", "FooterHeight", 4, dpi / 16, dpi / 40);
        HeaderHeight =
                new ZLIntegerRangeOption("Options", "HeaderHeight", dpi / 4, dpi / 3, dpi / 3);
		ColorProfileName =
			new ZLStringOption("Options", "ColorProfile", ColorProfile.DAY);
		ColorProfileName.setSpecialName("colorProfile");

	}

	public ColorProfile getColorProfile() {
		final String name = ColorProfileName.getValue();
		if (myColorProfile == null || !name.equals(myColorProfile.Name)) {
			myColorProfile = ColorProfile.get(name);
		}
		return myColorProfile;
	}

	public ZLTextStyleCollection getTextStyleCollection() {
		final String screen = "Base".intern();
		if (myTextStyleCollection == null || myTextStyleCollection.Screen != screen) {
			myTextStyleCollection = new ZLTextStyleCollection(screen);
		}
		return myTextStyleCollection;
	}

	public FooterOptions getFooterOptions() {
		final String screen = "Base".intern();
		if (myFooterOptions == null || myFooterOptions.Screen != screen) {
			myFooterOptions = new FooterOptions(screen);
		}
		return myFooterOptions;
	}
}