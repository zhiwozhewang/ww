package com.longyuan.qm;

import android.content.Intent;
import android.net.ConnectivityManager;

import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.UnitBean;

import java.util.ArrayList;
import java.util.List;

public class ConstantsAmount {
    /**
     * SharedPreferences缓存名
     */
    public static final String SHAREDPREFERENCES_CACHE_NAME = "config";
    /**
     * 文件缓存名
     */
    public static final String FILE_CACHE_NAME = "cache";
    public static final String BASEURL = "http://pread.vip.qikan.com/appservice/";
    //    public static final String BASEUnit = "dragongroup.qikan.com";
//    public static final String APPTOKEN = "C/DYejbl2iw2Cy7Xdragon2012EoKpTv6cr9ofZKf3IXfH0O9v13Ieev3rJUBgJlK7aRzPFryauSxS833Wfsdragon2014";
    public static final String BASEUnit = "dps.qikan.com";
    public static final String BASEURL_DEFAULT = "http://" + BASEUnit + "/api/";
    public static final String APPTOKEN_BASE = "528ed763-f895-4c3f-92ec-85c4b8a33278|22|";
    public static final String APPTOKEN_LOCAL = "C%2fDYejbl2iw2Cy7X%2bEoKpTv6cr9ofZKf3IXfH0O9v136hmTAv6bUq%2fF8cSbxO6Qd2I8KALtPYlI%3d";
    public static final String APPGUID = "528ed763-f895-4c3f-92ec-85c4b8a33278";
    public static String GETLATEST_URL, GETUNITSERVICES_URL, GETSERVICETICKET_URL, GETMENUITEM_URL, TICKET, BASEURL_UNIT, UNITBASEURL_REQUESTHEADER;
    public static List<MenuBean> MENUBEANLIST = new ArrayList<MenuBean>();
    public static List<UnitBean> UNITBEANLIST = new ArrayList<UnitBean>();
    public static int MENUPOSITION = 0;
    public static byte[] sKey = new byte[]{83, 90, 37, 105, (byte) 167, 94,
            125, 19};
    public static boolean LOGININTERNETSTATE = false;
    public static final int DEFAULT_CONN_TIMEOUT = 1000 * 35;
    /**
     * show Toast Content;
     */
    public static final String BAD_NETWORK_CONNECTION = "当前网络不可用,请检测网络!";
    public static final String REQUEST_ONFAILURE = "请求失败!";
    public static final String SDCARKERROR = "sd卡不可用，请检测sd卡安装是否正确!";

    public interface Cache {
        /**
         * 项目缓存根目录保存名
         */
        public static final String ROOT_DIR_NAME = "rootDir";
    }

    public interface What {
        /**
         * 网络断开
         */
        public static final int NET_WORK_BREAK = -1;
        /**
         * 网络连接
         */
        public static final int NET_WORK_CONNECTED = -2;
        /**
         * 网络请求网络错误
         */
        public static final int NET_WORK_ERROR = -3;
    }

    public interface Action {
        /**
         * 退出应用程序
         */
        public static final String EXIT_APPLICATION = "com.up72.android.EXIT_APPLICATION";
        /**
         * 网络状态改变
         */
        public static final String NET_WORK_CHANGE = "com.up72.android.NET_WORK_CHANGE";
        /**
         * 系统网络状态改变
         */
        public static final String NET_WORK_CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION;
        /**
         * Home键监听
         */
        public static final String CLOSE_APPLICATION = Intent.ACTION_CLOSE_SYSTEM_DIALOGS;
        /**
         * 下载杂志
         */
        public static final String DOWNLOAD_MAGAZINE = "com.longyuan.zgg.download.magazine";
    }
}
