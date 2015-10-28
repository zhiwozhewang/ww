/**
 * @Title: BaseFragment.java
 * @Package com.longyan.qm
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-24 上午11:53:23
 * @version V1.0
 */
package com.longyuan.qm;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.utils.ActivityUtil;

import org.json.JSONObject;

import java.util.Set;

/**
 * @author dragonsource
 * @ClassName: BaseFragment
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2014-9-24 上午11:53:23
 */
public abstract class BaseFragment extends Fragment {
    protected SharedPreferences mSp;

    protected Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSp = ActivityUtil.getSharedPreferences(getActivity());
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = setContentView(inflater);
        findViewByIds(view);
        init();
        registerReceivers();
        setListeners();
        return view;
    }

    /**
     * 设置布局文件
     */
    protected View setContentView(LayoutInflater inflater) {
        return null;
    }

    ;

    /**
     * 初始化控件
     */
    protected void findViewByIds(View view) {

    }

    ;

    /**
     * 设置事件监听
     */
    protected void setListeners() {

    }

    ;

    /**
     * 初始化Fragement数据
     */
    protected void init() {

    }

    ;

    /**
     * 注册广播接收者
     */
    protected void registerReceivers() {
    }

    ;

    /**
     * 取消广播接收
     */
    protected void unRegisterReceivers() {
    }

    ;

    @Override
    public void onDestroy() {
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
     * @author 谭杰
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
     * @author 谭杰
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
     * @author 谭杰
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
     * @author 谭杰
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
     * @author 谭杰
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
     * @author 谭杰
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
     * @param
     * @param
     * @author 谭杰
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
     * @author 谭杰
     * @create 2014-6-27 下午3:22:22
     */
    protected void clear() {
        Editor editor = mSp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: isInternet
     * @Description: 判断当前是否有网络(这里用一句话描述这个方法的作用)
     */
    public boolean isInternet() {
        ConnectivityManager conManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
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
        final ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
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
        final ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
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
}
