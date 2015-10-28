package com.longyuan.qm.receiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.longyuan.qm.BaseBroadcastReceiver;


/**
 * 退出APP
 * <p/>
 * @author
 * @version 1.0.0
 * @create date 2013-7-11
 */
public class ExitAppReceiver extends BaseBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Activity act = (Activity) context;
        act.finish();
    }
}
