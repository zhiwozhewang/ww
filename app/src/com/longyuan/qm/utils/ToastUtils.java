package com.longyuan.qm.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * 快速Toast工具
 *
 * @author wader
 * @editor adrainy
 * @email: wangjunliang@120.net,wangjunliang15@126.com
 */
public class ToastUtils {

    private static Toast toast;

    public static void showToastLong(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            toast.setView(toast.getView());
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public static void showToastLong(Context context, int stringId) {
        if (toast == null) {
            toast = Toast.makeText(context, context.getString(stringId),
                    Toast.LENGTH_LONG);
        } else {
            toast.setView(toast.getView());
            toast.setText(context.getString(stringId));
            toast.setDuration(Toast.LENGTH_LONG);
        }
        toast.show();
    }

    public static void showToastShort(Context context, String msg) {
        if (toast == null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setView(toast.getView());
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }

    public static void showToastShort(Context context, int stringId) {
        if (toast == null) {
            toast = Toast.makeText(context, context.getString(stringId),
                    Toast.LENGTH_SHORT);
        } else {
            toast.setView(toast.getView());
            toast.setText(context.getString(stringId));
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}
