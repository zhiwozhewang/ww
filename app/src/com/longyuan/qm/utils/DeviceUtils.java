package com.longyuan.qm.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;

import com.longyuan.qm.bean.Constance;

public class DeviceUtils {

    /*
     * 获取设备信息
     */
    public static String getDeviceID(Context con) {

        TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        Log.e("info", sb.toString());

        return tm.getDeviceId();
    }

    public static String getDeviceIMEI(Context con) {
        TelephonyManager tm = (TelephonyManager) con.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    public static boolean getSettingValue(String key, Context c) {// MODE_WORLD_READABLE
        SharedPreferences sf = c.getSharedPreferences(Constance.SETTINGNAME, Activity.MODE_PRIVATE);
        boolean test = sf.getBoolean(key, false);
        return test;
    }

    public static void setSettingBooleanValue(String key, boolean isBig, Context c) {// MODE_WORLD_READABLE
        SharedPreferences sf = c.getSharedPreferences(Constance.SETTINGNAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit = sf.edit();
        edit.putBoolean("FONTSIZE", isBig);
        edit.commit();
    }

    public static boolean hasDeskShortCut(Context context) {// MODE_WORLD_READABLE
        SharedPreferences setting = context.getSharedPreferences("DESKICON", Activity.MODE_PRIVATE);
        boolean flag = setting.getBoolean("ICON", false);
        Log.i("====", "判断ICON是否放置:" + flag);
        if (flag == false) {
            // createSystemSwitcherShortCut(context);
            // 写入
            SharedPreferences.Editor edit = setting.edit();
            edit.putBoolean("ICON", true);
            edit.commit();
        }
        return flag;
    }

    public static void doRecycleImage(ImageView imageView) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        // 如果图片还未回收，先强制回收该图片
        if (bitmapDrawable.getBitmap().isRecycled()) {
            bitmapDrawable.getBitmap().recycle();
            imageView = null;
        }
    }

    // public static void createSystemSwitcherShortCut(Context context) {
    //
    // String label = "";
    // PackageInfo info;
    // int iconIdentifier = 0;
    //
    // try {
    // info =
    // context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    // ApplicationInfo appinfo = info.applicationInfo;
    // label =
    // context.getPackageManager().getApplicationLabel(appinfo).toString();
    // iconIdentifier = appinfo.icon;
    // // String picName =
    // //
    // context.getPackageManager().getApplicationIcon(info.packageName).toString();
    // // id = context.getResources().getIdentifier(picName == null ?
    // // "no_picture" : picName, "drawable",
    // // info.packageName);
    // // 根据包名寻找MainActivity
    // PackageManager pkgMag = context.getPackageManager();
    // Intent queryIntent = new Intent(Intent.ACTION_MAIN, null);
    // queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    // List<ResolveInfo> list = pkgMag.queryIntentActivities(queryIntent,
    // PackageManager.GET_ACTIVITIES);
    // for (int i = 0; i < list.size(); i++) {
    // ResolveInfo ff = list.get(i);
    // if (ff.activityInfo.packageName.equals(info.packageName)) {
    // iconIdentifier = ff.activityInfo.applicationInfo.icon;
    // break;
    // }
    // }
    // } catch (NameNotFoundException e) {
    // e.printStackTrace();
    // }
    //
    // final Intent addIntent = new
    // Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    // Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
    // iconIdentifier); // 获取快捷键的图标
    //
    // addIntent.putExtra("duplicate", false);
    // Intent myIntent = new Intent(context, SplashActivity.class);
    // myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);// 快捷方式的标题
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
    // context.sendBroadcast(addIntent);
    // }

    // public static void createTSSwitcherShortCut(Context context) {
    //
    // String label = "";
    // PackageInfo info;
    // int iconIdentifier = 0;
    //
    // try {
    // info =
    // context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
    // ApplicationInfo appinfo = info.applicationInfo;
    // label =
    // context.getPackageManager().getApplicationLabel(appinfo).toString();
    // iconIdentifier = appinfo.icon;
    // // String picName =
    // //
    // context.getPackageManager().getApplicationIcon(info.packageName).toString();
    // // id = context.getResources().getIdentifier(picName == null ?
    // // "no_picture" : picName, "drawable",
    // // info.packageName);
    // // 根据包名寻找MainActivity
    // PackageManager pkgMag = context.getPackageManager();
    // Intent queryIntent = new Intent(Intent.ACTION_MAIN, null);
    // queryIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    // List<ResolveInfo> list = pkgMag.queryIntentActivities(queryIntent,
    // PackageManager.GET_ACTIVITIES);
    // for (int i = 0; i < list.size(); i++) {
    // ResolveInfo ff = list.get(i);
    // if (ff.activityInfo.packageName.equals(info.packageName)) {
    // iconIdentifier = ff.activityInfo.applicationInfo.icon;
    // break;
    // }
    // }
    // } catch (NameNotFoundException e) {
    // e.printStackTrace();
    // }
    //
    // final Intent addIntent = new
    // Intent("com.android.launcher.action.INSTALL_SHORTCUT");
    // Parcelable icon = Intent.ShortcutIconResource.fromContext(context,
    // iconIdentifier); // 获取快捷键的图标
    //
    // addIntent.putExtra("duplicate", false);
    // Intent myIntent = new Intent(context, SplashActivity.class);
    // myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, label);// 快捷方式的标题
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
    // addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
    // context.sendBroadcast(addIntent);
    // }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        // LogUtils.i("========dip2px===========" + (int) (dpValue * scale +
        // 0.5f));
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /*
     * 判断是否联网
     */
    public static boolean isInternet(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /*
     * 获取Reader的版本号z
     */
    public static String getVerionName(Context context) {
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        String ver = "";
        try {
            packInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            if (packInfo != null) {
                Log.i("DeviceUtils", packInfo.versionName);
                ver = packInfo.versionName;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return ver;
    }

    public static int getVerionInt(Context context) {
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        int ver = 0;
        try {
            packInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            if (packInfo != null) {
                Log.i("DeviceUtils  Ver Code：", packInfo.versionCode + "");
                ver = packInfo.versionCode;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return ver;
    }

    public static String getVerionString(Context context) {
        PackageManager mPackageManager = context.getPackageManager();
        PackageInfo packInfo = null;
        String ver = "";
        try {
            packInfo = mPackageManager.getPackageInfo(context.getPackageName(), 0);
            if (packInfo != null) {
                Log.i("DeviceUtils  Ver Code：", packInfo.versionName + "");
                ver = packInfo.versionName;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return ver;
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            Constance.isDeveloper = (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
            return Constance.isDeveloper;
        } catch (Exception e) {

        }
        return false;
    }

    public static boolean isWifiConnection(Context context) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isAvailable())
            return true;
        else
            return false;
    }

    public boolean hasShortCut(Context context) {
        boolean result = false;
        // 获取当前应用名称
        String title = null;
        try {
            final PackageManager pm = context.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {
        }

        final String uriStr;
        if (android.os.Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = context.getContentResolver().query(CONTENT_URI, null, "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }
}
