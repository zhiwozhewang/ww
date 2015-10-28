package com.longyuan.qm.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.longyuan.qm.BaseBroadcastReceiver;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;

import java.io.File;
import java.util.UUID;

/**
 * @author 谭杰 E-mail: tanjie9012@163.com
 * @version 1.0.0
 * @description Activity相关操作 跳转,分辨率,版本号,版本名,设备token,dip转px,系统SDK版本
 * @create 2014-2-21 上午11:40:27
 * @company 北京开拓明天科技有限公司 Copyright: 版权所有 (c) 2014
 */
public class ActivityUtil {

    public static final String TAG = "ActivityUtil";

    /**
     * 获取全局sharedPreferences
     *
     * @param context
     * @return
     * @author 谭杰
     * @create 2014-7-9 下午5:29:07
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(
                ConstantsAmount.SHAREDPREFERENCES_CACHE_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 启动activity-standard
     *
     * @param context
     * @param clazz
     */
    public static void startActivity(Context context,
                                     Class<? extends Activity> clazz) {
        Intent intent = new Intent(context, clazz);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    /**
     * 启动activity-自定义启动方式
     *
     * @param context
     * @param clazz
     */
    public static void startActivity(Context context,
                                     Class<? extends Activity> clazz, int flags) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(flags);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    /**
     * 启动activity并传值
     *
     * @param context
     * @param clazz
     * @param data
     */
    public static void startActivity(Context context,
                                     Class<? extends Activity> clazz, Bundle data) {
        Intent intent = new Intent(context, clazz);
        if (data != null && data.size() > 0)
            intent.putExtras(data);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    /**
     * 启动activity并传值-standard
     *
     * @param context
     * @param clazz
     * @param data
     */
    public static void startActivity(Context context,
                                     Class<? extends Activity> clazz, Bundle data, int flags) {
        Intent intent = new Intent(context, clazz);
        intent.setFlags(flags);
        if (data != null && data.size() > 0)
            intent.putExtras(data);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }

    /**
     * 注册广播
     *
     * @param context
     * @param receiver
     * @param filterActivon
     * @author 谭杰
     * @create 2014-8-1 下午3:37:03
     */
    public static BaseBroadcastReceiver registerReceiver(Context context,
                                                         Class<? extends BaseBroadcastReceiver> receiver,
                                                         String filterActivon) {
        return registerReceiver(context, receiver, filterActivon, null);
    }

    /**
     * 注册广播
     *
     * @param context
     * @param receiver
     * @param filterActivon
     * @author 谭杰
     * @create 2014-8-1 下午3:37:03
     */
    public static BaseBroadcastReceiver registerReceiver(Context context,
                                                         Class<? extends BaseBroadcastReceiver> receiver,
                                                         String filterActivon, Handler mHandler) {
        try {
            BaseBroadcastReceiver mReceiver = receiver.newInstance();
            mReceiver.setHandler(mHandler);
            IntentFilter mFilter = new IntentFilter(filterActivon);
            context.registerReceiver(mReceiver, mFilter);
            return mReceiver;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭广播接收
     *
     * @param context
     * @param receiver
     * @author 谭杰
     * @create 2014-8-1 下午3:41:40
     */
    public static void unRegisterReceiver(Context context,
                                          BroadcastReceiver receiver) {
        if (receiver != null)
            context.unregisterReceiver(receiver);
    }

    /**
     * 获取手机分辨率
     *
     * @return DisplayMetrics
     */
    public static DisplayMetrics getScreenPixel(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(dm);
        Log.e("DisplayMetrics", "分辨率：" + dm.widthPixels + "x" + dm.heightPixels
                + ",精度：" + dm.density + ",densityDpi=" + dm.densityDpi);
        return dm;
    }

    /**
     * 获取当前应用版本序号
     *
     * @param context
     * @return 当前应用版本序号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取当前应用版本名
     *
     * @param context
     * @return 当前应用版本名
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取手机系统版本
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public static int getAndroidSDKVersion() {
        int version;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
            return 0;
        }
        return version;
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue dp值
     * @return px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(Context context) {
        InputMethodManager im = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            IBinder binder = view.getApplicationWindowToken();
            if (binder != null && im != null && view != null) {
                im.hideSoftInputFromWindow(binder,
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 获取设备token
     *
     * @param
     * @return
     */
    public static String getDeviceToken(Activity activity) {
        TelephonyManager tm = (TelephonyManager) activity
                .getSystemService(Context.TELEPHONY_SERVICE);
        String tokenId = tm.getDeviceId();
        if (StringUtil.isEmpty(tokenId)) {
            tokenId = tm.getSubscriberId();
        }
        if (StringUtil.isEmpty(tokenId)) {
            tokenId = UUID.randomUUID().toString().replace("-", "");
        }
        return tokenId;
    } // Android获取一个用于打开APK文件的intent

    public static Intent getApkFileIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(new File(param));
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param phoneNumber
     * @author 谭杰
     * @create 2014-7-24 下午5:05:10
     */
    public static void dialPhone(Context context, String phoneNumber) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.DIAL");
        intent.setData(Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }
}
