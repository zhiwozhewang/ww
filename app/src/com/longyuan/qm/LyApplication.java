/**
 * @Title: QMAplication.java
 * @Package com.longyan.qm
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-24 下午1:35:09
 * @version V1.0
 */
package com.longyuan.qm;


import org.geometerplus.ReaderApp;

/**
 * @author dragonsource
 * @ClassName: LyApplication
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-24 下午1:35:09
 */
public class LyApplication extends ReaderApp {
    public static String authToken, username, password;
    public static boolean isMagRefresh, isFavRefresh;

    /**
     * (非 Javadoc) Title: onCreate Description:
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public String getInternalPackageName() {
        return getPackageName();
    }
}
