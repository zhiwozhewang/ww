/**
 * Created by YuGang Yang on August 18, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package org.geometerplus.android.fbreader;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import org.geometerplus.fbreader.fbreader.FBReaderApp;

public class SelectionSearchAction extends FBAndroidAction {
    SelectionSearchAction(FBReader baseActivity, FBReaderApp fbreader) {
        super(baseActivity, fbreader);
    }

    @Override
    protected void run(Object ... params) {
        final String text = Reader.getTextView().getSelectedText();
        Reader.getTextView().clearSelection();

        Bundle bundle = null;
        BaseActivity.startActivity(
                new Intent(Intent.ACTION_SEARCH)
                        .setClass(BaseActivity, FBReader.class)
                        .putExtra(SearchManager.QUERY, text)
                        .putExtra(SearchManager.APP_DATA, bundle)
        );
    }
}