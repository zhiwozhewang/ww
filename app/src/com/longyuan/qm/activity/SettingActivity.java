package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Android on 2014/12/6.
 */
public class SettingActivity extends BaseActivity {
    private static  String LOGOUT_URL = null;
    public static SettingActivity instance_setting = null;
    private TextView tv_logout, tv_modification, tv_title_name, tv_account, tv_ver;
    private Button left_btn, right_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        Utils.getHttpRequestHeader();
        LOGOUT_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/Logout?";

        instance_setting = this;
        tv_title_name = (TextView) findViewById(R.id.head_layout_text);
        tv_logout = (TextView) findViewById(R.id.tv_logout);
        tv_modification = (TextView) findViewById(R.id.modification_pw);
        tv_account = (TextView) findViewById(R.id.tv_account);
        tv_ver = (TextView) findViewById(R.id.tv_about);
        left_btn = (Button) findViewById(R.id.head_layout_showLeft);
        left_btn.setBackgroundResource(R.drawable.button_back_selector);
        right_btn = (Button) findViewById(R.id.head_layout_showRight);
        right_btn.setVisibility(View.INVISIBLE);

        tv_account.setText(mSp.getString("username", ""));
        tv_title_name.setText("设置");
        tv_ver.setText("v " + Utils.getVersionName(mContext));

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_modification.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_modification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isInternet()) {
                    Intent intent = new Intent(instance_setting, PasswordModificationActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                }
            }
        });

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog.showDialog(mContext, "正在注销...");
                String path = LOGOUT_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
                HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        try {
                            JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                LoadingDialog.dissmissDialog();
                                // FIXME 清空无用的常量值；
                                remove("username");
                                remove("password");
                                remove("authToken");
                                ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                ConstantsAmount.GETLATEST_URL = null;
                                ConstantsAmount.GETUNITSERVICES_URL = null;
                                ConstantsAmount.GETSERVICETICKET_URL = null;
                                ConstantsAmount.GETMENUITEM_URL = null;
                                ConstantsAmount.MENUPOSITION = 0;
                                ConstantsAmount.BASEURL_UNIT = null;
                                ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                LyApplication.authToken = null;
                                Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                                HomeActivity.instance_home.finish();
                            } else if (checkRequestCode(jsonObject).equals("3")) {
                                LoadingDialog.dissmissDialog();
                                Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // FIXME 清空无用的常量值；
                                                remove("username");
                                                remove("password");
                                                remove("authToken");
                                                Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
                                                ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                                ConstantsAmount.GETLATEST_URL = null;
                                                ConstantsAmount.GETUNITSERVICES_URL = null;
                                                ConstantsAmount.GETSERVICETICKET_URL = null;
                                                ConstantsAmount.GETMENUITEM_URL = null;
                                                ConstantsAmount.MENUPOSITION = 0;
                                                LyApplication.authToken = null;
                                                startActivity(intent);
                                                finish();
                                                HomeActivity.instance_home.finish();
                                            }
                                        }).setCancelable(false).create();
                                alertDialog.show();
                            } else {
                                LoadingDialog.dissmissDialog();
                                Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        LoadingDialog.dissmissDialog();
                        Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        overridePendingTransition(R.anim.slide_in_from_right,
                R.anim.slide_out_form_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}