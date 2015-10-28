/**
 * @Title: SplashActivity.java
 * @Package com.longyuan.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-24 上午10:59:43
 * @version V1.0
 */
package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.UnitBean;
import com.longyuan.qm.fragment.ArticleTabFragment;
import com.longyuan.qm.fragment.BookShopFragment;
import com.longyuan.qm.fragment.MagazineFragment;
import com.longyuan.qm.utils.DeviceUtils;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.utils.VersionUtil;
import com.longyuan.qm.view.KeyboardListenRelativeLayout;
import com.longyuan.qm.view.KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dragonsource
 * @ClassName: SplashActivity
 * @Description: 开机过渡页面(登陆页面)(这里用一句话描述这个类的作用)
 * @date 2014-9-24 上午10:59:43
 */
public class SplashActivity extends BaseActivity implements OnClickListener {
    private static String BASE_LOGIN_URL = ConstantsAmount.BASEURL
            + "Login.ashx?";
    private static String UPDATE_URL = ConstantsAmount.BASEURL + "GetAppVersion.ashx?";
    private static String BASE_LOGIN_URL_NEW = ConstantsAmount.BASEURL_DEFAULT + "user/login";
    private static String BASE_GetUnitList_URL = ConstantsAmount.BASEURL_DEFAULT + "user/GetUnitList?";
    private EditText et_UserName, et_Password;
    private View v1, v2, v3;
    private Button login;
    private float v1_y, v2_y, login_y, v3_y, window_height,
            window_height_third;
    private boolean isShow = false, isHide = true;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    loginAnimShow(isShow);
                    isShow = true;
                    isHide = false;
                    break;
                case 2:
                    loginAnimHide(isHide);
                    isHide = true;
                    isShow = false;
                    break;
                default:
                    break;
            }
        }
    };
    private String username, password;
    private String unitName = "", name = "", phoneNumber = "", logoUrl = "", backgroundUrl = "", appId;
    private KeyboardListenRelativeLayout relativeLayout;
    private InputMethodManager manager;
    private ApplicationInfo appInfo;
    private HttpUtils httpUtils = null;

    /**
     * (非 Javadoc) Title: setContentView Description:
     *
     * @see com.longyuan.qm.BaseActivity#setContentView()
     */
    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash_uy);
    }

    /**
     * (非 Javadoc) Title: findViewByIds Description:
     *
     * @see com.longyuan.qm.BaseActivity#findViewByIds()
     */
    @Override
    protected void findViewByIds() {
        mContext = SplashActivity.this;
        v1 = this.findViewById(R.id.relativeLayout00);
        v2 = this.findViewById(R.id.relativeLayout01);
        v3 = this.findViewById(R.id.splash_image_pic2);
        v1.setVisibility(View.INVISIBLE);
        v2.setVisibility(View.INVISIBLE);

        login = (Button) this.findViewById(R.id.button_login);
        login.setVisibility(View.INVISIBLE);

        et_UserName = (EditText) findViewById(R.id.name_editText);
        et_Password = (EditText) findViewById(R.id.pwd_editText);

        relativeLayout = (KeyboardListenRelativeLayout) this
                .findViewById(R.id.layoutbackground);
        relativeLayout
                .setOnKeyboardStateChangedListener(new IOnKeyboardStateChangedListener() {

                    @Override
                    public void onKeyboardStateChanged(int state) {
                        switch (state) {
                            case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE:
//							handler.sendEmptyMessage(2);
                                break;
                            case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW:
//							handler.sendEmptyMessage(1);
                                break;
                        }
                    }
                });
    }

    /**
     * (非 Javadoc) Title: setListeners Description:
     *
     * @see com.longyuan.qm.BaseActivity#setListeners()
     */
    @Override
    protected void setListeners() {
        login.setOnClickListener(this);
    }

    /**
     * (非 Javadoc) Title: init Description:
     *
     * @see com.longyuan.qm.BaseActivity#init()
     */
    @Override
    protected void init() {
        // 首先获得软键盘Manager

        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        username = mSp.getString("username", null);
        password = mSp.getString("password", null);
        if (username != null && password != null) {
            doLogin(username, password);
        } else {
            v1.setVisibility(View.VISIBLE);
            v2.setVisibility(View.VISIBLE);
            login.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            if (this.getCurrentFocus() != null
                    && this.getCurrentFocus().getWindowToken() != null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    manager.hideSoftInputFromWindow(SplashActivity.this
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS
                    );
                }
            } else {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

    public void loginAnimShow(boolean isAn) {
        if (!isAn) {

            v1_y = v1.getY();
            v2_y = v2.getY();
            login_y = login.getY();
            v3_y = v3.getY();

            AnimationSet set = new AnimationSet(true);

            final TranslateAnimation translateAnimation_et = new TranslateAnimation(
                    0, 0, 0, -window_height_third);
            final TranslateAnimation translateAnimation_logo = new TranslateAnimation(
                    0, 0, 0, -window_height);

            // 用户名输入框的动画监听;
            translateAnimation_et.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    v1.setY(v1_y - window_height_third);
                    v2.setY(v2_y - window_height_third);
                    login.setY(login_y - window_height_third);
                }
            });
            // logo的动画监听;
            translateAnimation_logo
                    .setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation arg0) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                            v3.setY(v3_y - window_height);
                        }
                    });
            // 设置动画过程时长;
            translateAnimation_et.setDuration(1000);
            translateAnimation_logo.setDuration(1000);
            // 添加动画;
            set.addAnimation(translateAnimation_et);
            set.addAnimation(translateAnimation_logo);
            // 执行动画;
            v1.startAnimation(translateAnimation_et);
            v2.startAnimation(translateAnimation_et);
            login.startAnimation(translateAnimation_et);
            v3.startAnimation(translateAnimation_logo);
        }
    }

    public void loginAnimHide(boolean isAn) {
        if (!isAn) {

            float y1 = v1.getY();
            float y2 = v2.getY();
            float yl = login.getY();
            float y3 = v3.getY();

            if (v1_y == y1 && v2_y == y2 && login_y == yl && v3_y == y3) {
                CountDownTimer timer = new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long arg0) {
                    }

                    @Override
                    public void onFinish() {
                        AnimationSet set = new AnimationSet(true);
                        final TranslateAnimation translateAnimation_et = new TranslateAnimation(
                                0, 0, 0, window_height_third);
                        final TranslateAnimation translateAnimation_logo = new TranslateAnimation(
                                0, 0, 0, window_height);

                        // 用户名输入框的动画监听;
                        translateAnimation_et
                                .setAnimationListener(new AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation arg0) {
                                        v1.setY(v1_y);
                                        v2.setY(v2_y);
                                        login.setY(login_y);
                                    }
                                });
                        // logo的动画监听;
                        translateAnimation_logo
                                .setAnimationListener(new AnimationListener() {

                                    @Override
                                    public void onAnimationStart(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation arg0) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation arg0) {
                                        v3.setY(v3_y);
                                    }
                                });
                        // 设置动画过程时长;
                        translateAnimation_et.setDuration(1000);
                        translateAnimation_logo.setDuration(1000);
                        // 添加动画;
                        set.addAnimation(translateAnimation_et);
                        set.addAnimation(translateAnimation_logo);
                        // 执行动画;
                        v1.startAnimation(translateAnimation_et);
                        v2.startAnimation(translateAnimation_et);
                        login.startAnimation(translateAnimation_et);
                        v3.startAnimation(translateAnimation_logo);
                    }
                }.start();
            } else {
                AnimationSet set = new AnimationSet(true);
                final TranslateAnimation translateAnimation_et = new TranslateAnimation(
                        0, 0, 0, window_height_third);
                final TranslateAnimation translateAnimation_logo = new TranslateAnimation(
                        0, 0, 0, window_height);

                // 用户名输入框的动画监听;
                translateAnimation_et
                        .setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                v1.setY(v1_y);
                                v2.setY(v2_y);
                                login.setY(login_y);
                            }
                        });
                // logo的动画监听;
                translateAnimation_logo
                        .setAnimationListener(new AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }

                            @Override
                            public void onAnimationEnd(Animation arg0) {
                                v3.setY(v3_y);
                            }
                        });
                // 设置动画过程时长;
                translateAnimation_et.setDuration(1000);
                translateAnimation_logo.setDuration(1000);
                // 添加动画;
                set.addAnimation(translateAnimation_et);
                set.addAnimation(translateAnimation_logo);
                // 执行动画;
                v1.startAnimation(translateAnimation_et);
                v2.startAnimation(translateAnimation_et);
                login.startAnimation(translateAnimation_et);
                v3.startAnimation(translateAnimation_logo);
            }
        }
    }

    /**
     * (非 Javadoc) Title: onClick Description:
     *
     * @param v
     * @see OnClickListener#onClick(View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_login:
                if (isInternet()) {
                    String mUserName = et_UserName.getText().toString().trim();
                    String mPassWord = et_Password.getText().toString().trim();
                    login.setClickable(false);
                    doLogin(mUserName, mPassWord);
                } else {
                    Toast.makeText(mContext,
                            ConstantsAmount.BAD_NETWORK_CONNECTION,
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: doLogin
     * @Description: 登陆(这里用一句话描述这个方法的作用)
     */
    private void doLogin(final String u_name, final String pwd) {

        if (!isInternet()) {
            doIntent();
            Toast.makeText(mContext, "当前无网络!", Toast.LENGTH_SHORT).show();
        } else {
            if (u_name.equals("")) {
                Toast toast = Toast.makeText(SplashActivity.this, "请输入用户名!",
                        Toast.LENGTH_SHORT);
                toast.show();
                login.setClickable(true);
                return;
            }

            if (pwd.equals("")) {
                Toast toast = Toast.makeText(SplashActivity.this, "请输入密码!",
                        Toast.LENGTH_SHORT);
                toast.show();
                login.setClickable(true);
                return;
            }

            if (!isName(u_name) && !isEmail(u_name)) {
                Toast toast = Toast.makeText(SplashActivity.this,
                        "用户名由5-18位的汉字、数字或字母组成！", Toast.LENGTH_SHORT);
                toast.show();
                login.setClickable(true);
                return;
            }

            if (!isPassword(pwd)) {
                Toast toast = Toast.makeText(SplashActivity.this,
                        "密码由6-18位数字、字母组成！", Toast.LENGTH_SHORT);
                toast.show();
                login.setClickable(true);
                return;
            }

            RequestParams params = new RequestParams();
            try {
//            Utils.createAppToken();
                params.setHeader("Content-Type", "application/json");
                JSONObject obj = new JSONObject();
                obj.put("apptoken", Utils.createAppToken());
                obj.put("loginname", Utils.string2U8(u_name));
                obj.put("password", Utils.string2U8(pwd));
                obj.put("deviceid", Utils.string2U8(DeviceUtils.getDeviceIMEI(SplashActivity.this)));
                params.setBodyEntity(new StringEntity(obj.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
            httpUtils.send(HttpMethod.POST, BASE_LOGIN_URL_NEW, params, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    try {
                        JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                        if (checkRequestCode(jsonObject).equals("1")) {
                            JSONObject jo = jsonObject.getJSONObject("Data");
                            ConstantsAmount.TICKET = jo.optString("Ticket");
                            name = jo.optString("Name");

                            put("username", u_name);
                            put("password", pwd);

                            chooseUnitURL();

                        } else if (checkRequestCode(jsonObject).equals("3")) {
                            Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // FIXME 清空无用的常量值；
                                            remove("username");
                                            remove("password");
                                            remove("authToken");

                                            v1.setVisibility(View.VISIBLE);
                                            v2.setVisibility(View.VISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                            login.setClickable(true);
                                        }
                                    }).setCancelable(false).create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                    Toast.LENGTH_LONG).show();
                            // FIXME 清空无用的常量值；
                            remove("username");
                            remove("password");
                            remove("authToken");

                            v1.setVisibility(View.VISIBLE);
                            v2.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            login.setClickable(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Toast.makeText(mContext, "登录失败!", Toast.LENGTH_LONG).show();
                    v1.setVisibility(View.VISIBLE);
                    v2.setVisibility(View.VISIBLE);
                    login.setVisibility(View.VISIBLE);
                    login.setClickable(true);
                }
            });
        }
    }

    private void chooseUnitURL() {

        final String path = BASE_GetUnitList_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
        httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);

        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        if (jsonObject.optString("Data").equals("null")) {
                            Toast.makeText(SplashActivity.this, "您的账号未绑定任何单位，请联系您的单位管理人员!", Toast.LENGTH_LONG).show();
                            v1.setVisibility(View.VISIBLE);
                            v2.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                            login.setClickable(true);
                            return;
                        }
                        JSONArray ja = (JSONArray) jsonObject.opt("Data");
                        if (ja.length() == 1) {
                            JSONObject jo = (JSONObject) ja.get(0);
                            unitName = jo.optString("UnitName");
                            ConstantsAmount.BASEURL_UNIT = jo.optString("Domain");

                            Utils.getHttpRequestHeader();
                            ConstantsAmount.GETLATEST_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "ClientApp/GetLatest?";
                            ConstantsAmount.GETUNITSERVICES_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "unit/GetServices?";
                            ConstantsAmount.GETSERVICETICKET_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "unit/GetServiceTicket?";
                            ConstantsAmount.GETMENUITEM_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "menu/GetAll?";
                            getUnitServices();
                        } else if (ja.length() > 1) {
                            UnitBean bean = null;
                            for (int i = 0; i < ja.length(); i++) {
                                bean = new UnitBean();
                                JSONObject jo = (JSONObject) ja.get(i);
                                bean.setUnitName(jo.optString("UnitName"));
                                bean.setUnitBaseUrl(jo.optString("Domain"));
                                ConstantsAmount.UNITBEANLIST.add(bean);
                            }
                            Intent intent = new Intent(SplashActivity.this, ChooseUnitActivity.class);
                            intent.putExtra("name", name);
//                            intent.putExtra("isInternet", isInternet());
                            login.setClickable(true);
                            startActivity(intent);
                            finish();
                        }
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");

                                        v1.setVisibility(View.VISIBLE);
                                        v2.setVisibility(View.VISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        login.setClickable(true);
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                        v1.setVisibility(View.VISIBLE);
                        v2.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);
                        login.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                login.setClickable(true);
            }
        });
    }

    private void getUnitServices() {
//        String path = GETUNITSERVICES_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket=" + ConstantsAmount.TICKET;
        String path = ConstantsAmount.GETUNITSERVICES_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
        httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                String serviceID = "";
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
//                {
//                    "Code": 1,
//                        "Message": null,
//                        "Data": [{
//                            "ChooseServiceID": "553d08e3-921d-4e51-ab43-7310e9b92486",
//                            "TemplateID": "528ed763-f895-4c3f-92ec-85c4b8a33278",
//                            "ServiceName": "中国全民阅读移动书库",
//                            "LogoURL": "http://uploadfile.qikan.com.cn/Files/PublicCompilation/dps/2014/12/19/ed5817b4-cce9-4e21-a3f4-fd2e1eaa011f.png",
//                            "BackgroundURL": "http://uploadfile.qikan.com.cn/Files/PublicCompilation/dps/2014/12/19/21853a5b-d241-48ef-9bb0-b8d59f195efb.jpg"
//                }]
//                }
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = (JSONArray) jsonObject.get("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = (JSONObject) ja.get(i);
                            //FIXME 转换为小写再比对；
                            if (jo.optString("TemplateID").toLowerCase().equals(ConstantsAmount.APPGUID.toLowerCase())) {
                                serviceID = jo.optString("ChooseServiceID");
                                logoUrl = jo.optString("LogoURL");
                                backgroundUrl = jo.optString("BackgroundURL");
                            }
                        }
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");

                                        v1.setVisibility(View.VISIBLE);
                                        v2.setVisibility(View.VISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        login.setClickable(true);
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                        login.setClickable(true);
                        v1.setVisibility(View.VISIBLE);
                        v2.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getServiceTicket(serviceID);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                login.setClickable(true);
            }
        });
    }

    private void getServiceTicket(String serviceId) {
        String path = ConstantsAmount.GETSERVICETICKET_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&chooseserviceid=" + serviceId;
        httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        ConstantsAmount.TICKET = jsonObject.optString("Data");
                        //登陆成功后检测版本更新;
                        checkUpdate();
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                        login.setClickable(true);
                        v1.setVisibility(View.VISIBLE);
                        v2.setVisibility(View.VISIBLE);
                        login.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                login.setClickable(true);
            }
        });
    }

    private void checkUpdate() {
        try {
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            appId = appInfo.metaData.getString("CLIENT_APPID");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        String path = UPDATE_URL+"channel=1&device=0&authToken="+ LyApplication.authToken + "&appid="+appId;
        String path = ConstantsAmount.GETLATEST_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    if (jsonObject.optString("Data").equals("null")) {
                        getMenuItem();
                    } else {
                        if (checkRequestCode(jsonObject).equals("1")) {
                            String localVersionName = Utils.getVersionName(SplashActivity.this);
                            int verName = Integer.parseInt(localVersionName.replace(".", ""));
                            JSONObject jo = jsonObject.getJSONObject("Data");
                            int appVersionID = jo.optInt("AppVersionID");
                            String appID = jo.optString("AppID");
                            String version = jo.optString("Version");
                            int platformType = jo.optInt("PlatformType");
                            int fileSize = jo.optInt("FileSize");
                            int state = jo.optInt("State");
                            String compatibility = jo.optString("Compatibility");
                            String downloadUrl = jo.optString("DownloadUrl");
                            String note = jo.optString("Note");
                            String imageList = jo.optString("ImageList");
                            String createDate = jo.optString("CreateDate");

                            int updateVersion = Integer.parseInt(version.replace(".", ""));

                            if (verName < updateVersion) {
                                if (state == 2) {
                                    showUpdateDialog(downloadUrl, version, Utils.replaceHtmlTag(note));
                                } else {
                                    showForceUpdateDialog(downloadUrl, version, Utils.replaceHtmlTag(note));
                                }
                            } else if (verName > updateVersion) {
                                if (state == 2) {
                                    showRollbackDialog(downloadUrl, version, Utils.replaceHtmlTag(note));
                                } else {
                                    showForceRollbackDialog(downloadUrl, version, Utils.replaceHtmlTag(note));
                                }
                            } else {
                                getMenuItem();
                            }
                        } else if (checkRequestCode(jsonObject).equals("3")) {
                            Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // FIXME 清空无用的常量值；
                                            remove("username");
                                            remove("password");
                                            remove("authToken");

                                            v1.setVisibility(View.VISIBLE);
                                            v2.setVisibility(View.VISIBLE);
                                            login.setVisibility(View.VISIBLE);
                                            login.setClickable(true);
                                        }
                                    }).setCancelable(false).create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                    Toast.LENGTH_LONG).show();
                            login.setClickable(true);
                            v1.setVisibility(View.VISIBLE);
                            v2.setVisibility(View.VISIBLE);
                            login.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
                login.setClickable(true);
            }
        });
    }

    private void getMenuItem() {
        String path = ConstantsAmount.GETMENUITEM_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                MenuBean bean = null;
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < ja.length(); i++) {
//                            bean = new MenuBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            jo.optString("MenuCode");
                            jo.optString("MenuName");
                            jo.optString("Level");
                            jo.optString("MenuValue");
                            jo.optString("ImageUrl");
                            jo.optString("ImageDisplayMode");
                            jo.optString("OpenMode");
                            jo.optString("OrderNumber");
                            jo.optString("Description");
                            jo.getJSONArray("Childs");

                            if (jo.optString("MenuValue").startsWith("article:")) {
//                                bean.setName(jo.optString("MenuName"));
//                                bean.setFragment(new ArticleTabFragment());
//                                bean.setNormalResource(R.drawable.icon_art);
//                                bean.setCheckedResource(R.drawable.icon_art_press);
//                                bean.setCurrentResource(R.drawable.icon_art_press);
                                bean = new MenuBean(jo.optString("MenuName"), R.drawable.icon_art, new ArticleTabFragment(), R.drawable.icon_art_press, R.drawable.icon_art);
                                bean.setMenuValue(jo.optString("MenuValue"));
                                ConstantsAmount.MENUBEANLIST.add(bean);
                            } else if (jo.optString("MenuValue").startsWith("magazine:")) {
//                                bean.setName(jo.optString("MenuName"));
//                                bean.setFragment(new MagazineFragment());
//                                bean.setNormalResource(R.drawable.icon_mgnz);
//                                bean.setCheckedResource(R.drawable.icon_art_press);
//                                bean.setCurrentResource(R.drawable.icon_mgnz);
                                bean = new MenuBean(jo.optString("MenuName"), R.drawable.icon_mgnz, new MagazineFragment(), R.drawable.icon_mgnz_press, R.drawable.icon_mgnz);
                                bean.setMenuValue(jo.optString("MenuValue"));
                                ConstantsAmount.MENUBEANLIST.add(bean);
//                                bean.setName(jo.optString("MenuName"));
                            } else if (jo.optString("MenuValue").startsWith("book:")) {
//                                bean.setFragment(new ArticleTabFragment());
//                                bean.setNormalResource(R.drawable.icon_book);
//                                bean.setCheckedResource(R.drawable.icon_book_press);
//                                bean.setCurrentResource(R.drawable.icon_book);
                                bean = new MenuBean(jo.optString("MenuName"), R.drawable.icon_book, new BookShopFragment(), R.drawable.icon_book_press, R.drawable.icon_book);
                                bean.setMenuValue(jo.optString("MenuValue"));
                                ConstantsAmount.MENUBEANLIST.add(bean);
                            }
                        }

                        doIntent();
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");

                                        v1.setVisibility(View.VISIBLE);
                                        v2.setVisibility(View.VISIBLE);
                                        login.setVisibility(View.VISIBLE);
                                        login.setClickable(true);
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(SplashActivity.this, jsonMessageParser(jsonObject), Toast.LENGTH_SHORT).show();
                        login.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(SplashActivity.this, ConstantsAmount.REQUEST_ONFAILURE, Toast.LENGTH_SHORT).show();
                login.setClickable(true);
            }
        });
    }

    /**
     * @param @param  mobiles
     * @param @return
     * @return boolean
     * @throws
     * @Title: isPassword/isName/isEmail
     * @Description: 正则表达式(验证用户名，密码，邮箱格式是否正确)(这里用一句话描述这个方法的作用)
     */
    public boolean isPassword(String mobiles) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9_@]{6,18}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public boolean isName(String mobiles) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9_\u4e00-\u9fa5]{5,18}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public boolean isEmail(String mobiles) {
        String s = "^[a-zA-Z0-9_.]+@[a-zA-Z0-9-]+[.]+[a-zA-Z]+";
        Pattern p = Pattern.compile(s);
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    // 弹出版本更新提示框
    private void showUpdateDialog(final String url, final String ver, final String content) {
        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("已检测到新版本：").setMessage("“" + content + "”")
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new VersionUtil(mContext, getResources().getString(R.string.app_name),
                                R.drawable.ic_launcher_qm).download(url,
                                ver);
                        getMenuItem();
//                        doIntent();
                    }
                })
                .setNeutralButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getMenuItem();
//                        doIntent();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    // 弹出强制版本更新提示框
    private void showForceUpdateDialog(final String url, final String ver, final String content) {
        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("检测到新版本，强制更新！：").setMessage("“" + content + "”")
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new VersionUtil(mContext, getResources().getString(R.string.app_name),
                                R.drawable.ic_launcher_qm).download(url,
                                ver);
                        getMenuItem();
//                        doIntent();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    // 弹出版本回退提示框
    private void showRollbackDialog(final String url, final String ver, final String content) {
        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("版本回退提示：").setMessage("“" + content + "”")
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new VersionUtil(mContext, getResources().getString(R.string.app_name),
                                R.drawable.ic_launcher_qm).download(url,
                                ver);
                        getMenuItem();
//                        doIntent();
                    }
                })
                .setNeutralButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getMenuItem();
//                        doIntent();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    // 弹出强制版本回退提示框
    private void showForceRollbackDialog(final String url, final String ver, final String content) {
        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("版本回退提示：").setMessage("“" + content + "”")
                .setPositiveButton("立即下载", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new VersionUtil(mContext, getResources().getString(R.string.app_name),
                                R.drawable.ic_launcher_qm).download(url,
                                ver);
                        getMenuItem();
//                        doIntent();
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelable(false).create();
        alertDialog.show();
    }

    private void doIntent() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("unitName", unitName);
        intent.putExtra("logoUrl", logoUrl);
        intent.putExtra("backgroundUrl", backgroundUrl);
        intent.putExtra("isInternet", isInternet());
        login.setClickable(true);
        startActivity(intent);
        setRightOut(false);
        finish();
    }
}