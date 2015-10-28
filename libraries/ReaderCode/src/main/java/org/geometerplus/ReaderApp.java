/**
 * Created by YuGang Yang on October 09, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
package org.geometerplus;

import android.content.Context;

import android.text.TextUtils;
import org.geometerplus.android.fbreader.FBReaderApplication;

public class ReaderApp extends FBReaderApplication {

  private static ReaderApp mInstance;
  private static Context sAppContext;

  private String defaultPackageName;

  @Override
  public void onCreate() {
    mInstance = this;
    super.onCreate();
    initialize();
  }

  private void initialize() {
    sAppContext = getApplicationContext();
  }

  /**
   * @return current application instance
   */
  public static ReaderApp getInstance() {
    return mInstance;
  }

  /**
   * @return current application context
   */
  public static Context getAppContext() {
    return sAppContext;
  }

  // 可以用于更新阅读进度
  // DownloadUtil.updateRead(downloadId, current, total)
  public void updateRead(long downloadId, long current, long total) {
  }

  public String getDefaultPackageName() {
    if (TextUtils.isEmpty(defaultPackageName)) {
      defaultPackageName = getInternalPackageName();
    }
    return defaultPackageName;
  }

  public String getInternalPackageName() {
    return "";
  }

}
