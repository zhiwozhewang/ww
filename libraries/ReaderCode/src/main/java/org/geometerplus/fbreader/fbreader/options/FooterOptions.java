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

import org.geometerplus.zlibrary.core.options.ZLBooleanOption;
import org.geometerplus.zlibrary.core.options.ZLStringOption;

public class FooterOptions {
	public final String Screen;

	public final ZLBooleanOption ShowTOCMarks;
	public final ZLBooleanOption ShowClock;
	public final ZLBooleanOption ShowBattery;
	public final ZLBooleanOption ShowProgress;
	public final ZLStringOption Font;

	public FooterOptions(String screen) {
		Screen = screen;
		final String prefix = "Base".equals(screen) ? "" : screen + ":";

//		ShowTOCMarks = new ZLBooleanOption("Options", prefix + "FooterShowTOCMarks", "Base".equals(screen));
		ShowTOCMarks = new ZLBooleanOption("Options", prefix + "FooterShowTOCMarks", false);
		ShowClock = new ZLBooleanOption("Options", prefix + "ShowClockInFooter", false);
		ShowBattery = new ZLBooleanOption("Options", prefix + "ShowBatteryInFooter", false);
		ShowProgress = new ZLBooleanOption("Options", prefix + "ShowProgressInFooter", false);
		Font = new ZLStringOption("Options", prefix + "FooterFont", "Droid Sans");
	}
}
