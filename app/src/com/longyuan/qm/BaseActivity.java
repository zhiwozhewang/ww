/**
 * @Title: BaseActivity.java
 * @Package com.longyan.qm
 * @Description: Activity的基类(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-24 上午10:56:50
 * @version V1.0
 */
package com.longyuan.qm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.longyuan.qm.receiver.ExitAppReceiver;
import com.longyuan.qm.utils.ActivityUtil;

import org.json.JSONObject;

import java.util.Set;

/**
 * @author dragonsource
 * @ClassName: BaseActivity
 * @Description: 所有Activity都将继承自该BaseActivity(这里用一句话描述这个类的作用)
 * @date 2014-9-24 上午10:56:50
 */
public abstract class BaseActivity extends FragmentActivity {
    protected SharedPreferences mSp;
    protected Context mContext;
    /**
     * Activity结束时是否滑动退出
     */
    private boolean isRightOut = true;
    private BroadcastReceiver mExitReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSp = ActivityUtil.getSharedPreferences(this);
        mContext = this;
        setContentView();
        findViewByIds();
        init();
        setListeners();
        registerReceivers();
    }

    /**
     * 设置布局文件
     */
    protected void setContentView() {
    }

    /**
     * 初始化控件
     */
    protected void findViewByIds() {
    }

    ;

    /**
     * 设置事件监听
     */
    protected void setListeners() {
    }

    ;

    /**
     * 初始化Activity数据
     */
    protected void init() {
    }

    ;

    /**
     * 注册广播接收者
     */
    protected void registerReceivers() {
        mExitReceiver = ActivityUtil.registerReceiver(mContext,
                ExitAppReceiver.class, ConstantsAmount.Action.EXIT_APPLICATION);
    }

    /**
     * 取消广播接收
     */
    protected void unRegisterReceivers() {
        ActivityUtil.unRegisterReceiver(mContext, mExitReceiver);
    }

    /**
     * 是否滑动退出Activity
     *
     * @param isRightOut
     */
    public void setRightOut(boolean isRightOut) {
        this.isRightOut = isRightOut;
    }

    @Override
    public void finish() {
        super.finish();
        if (isRightOut) {
            overridePendingTransition(R.anim.push_right_out,
                    R.anim.push_right_in);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // AndroidApplication.isHome = false;
    }

    @Override
    protected void onDestroy() {
        unRegisterReceivers();
        super.onDestroy();
    }

    /**
     * 启动activity-standard
     *
     * @param
     * @param clazz
     */
    protected void startActivity(Class<? extends Activity> clazz) {
        ActivityUtil.startActivity(mContext, clazz);
    }

    /**
     * 启动activity-自定义启动方式
     *
     * @param
     * @param clazz
     */
    protected void startActivity(Class<? extends Activity> clazz, int flags) {
        ActivityUtil.startActivity(mContext, clazz, flags);
    }

    /**
     * 启动activity并传值
     *
     * @param
     * @param clazz
     * @param data
     */
    protected void startActivity(Class<? extends Activity> clazz, Bundle data) {
        ActivityUtil.startActivity(mContext, clazz, data);
    }

    /**
     * 启动activity并传值-standard
     *
     * @param
     * @param clazz
     * @param data
     */
    protected void startActivity(Class<? extends Activity> clazz, Bundle data,
                                 int flags) {
        ActivityUtil.startActivity(mContext, clazz, data, flags);
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, boolean value) {
        Editor editor = mSp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, float value) {
        Editor editor = mSp.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, String value) {
        Editor editor = mSp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, int value) {
        Editor editor = mSp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, long value) {
        Editor editor = mSp.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    /**
     * 往SharedPreferences写入数据
     *
     * @param key
     * @param value
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void put(String key, Set<String> value) {
        Editor editor = mSp.edit();
        editor.putStringSet(key, value);
        editor.commit();
    }

    /**
     * 从SharedPreferences移除数据
     *
     * @param key
     * @param
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void remove(String key) {
        Editor editor = mSp.edit();
        editor.remove(key);
        editor.commit();
    }

    /**
     * 从SharedPreferences清除所有数据
     *
     * @param
     * @param
     * @author
     * @create 2014-6-27 下午3:22:22
     */
    protected void clear() {
        Editor editor = mSp.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, event)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: isInternet
     * @Description: 判断当前是否有网络(这里用一句话描述这个方法的作用)
     */
    public boolean isInternet() {
        ConnectivityManager conManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();

        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: isWifiConnection
     * @Description: 判断是否为连接wifi状态(这里用一句话描述这个方法的作用)
     */
    public boolean isWifiConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifi.isAvailable())
            return true;
        else
            return false;
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: is3GConnection
     * @Description: 判断是否为3G网络状态(这里用一句话描述这个方法的作用)
     */
    public boolean is3GConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile.isAvailable())
            return true;
        else
            return false;
    }

    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: checkRequestCode
     * @Description: 判断Code是否为1(这里用一句话描述这个方法的作用)
     */
    public String checkRequestCode(JSONObject json) {
        return json.optString("Code");
    }

    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: jsonMessageParser
     * @Description: message信息(这里用一句话描述这个方法的作用)
     */
    public String jsonMessageParser(JSONObject json) {
        if (json.optString("Message").equals("") || json.optString("Message").equals("null")) {
            return "";
        } else {
            return json.optString("Message");
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
