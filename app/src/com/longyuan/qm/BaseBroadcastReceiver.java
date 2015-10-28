package com.longyuan.qm;

import android.content.BroadcastReceiver;
import android.os.Handler;

/**
 * @author 谭杰 E-mail: tanjie9012@163.com
 * @version 1.0.0
 * @description
 * @create 2014-8-1 下午3:48:34
 * @company 北京开拓明天科技有限公司 Copyright: 版权所有 (c) 2014
 */
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

    protected Handler mHandler;

    public BaseBroadcastReceiver() {

    }

    public BaseBroadcastReceiver(Handler handler) {
        this.mHandler = handler;
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }
}
