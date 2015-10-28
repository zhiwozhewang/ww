/**
 * Created by YuGang Yang on August 18, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package org.geometerplus.android.fbreader;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

public class HideBarsAction extends FBAndroidAction {
    HideBarsAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
    }

    @Override
    protected void run(Object ... params) {
        if (BaseActivity.barsAreShown()) {
            BaseActivity.hideBars();
        }
    }
}
