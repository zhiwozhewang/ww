package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Android on 2014/12/19.
 */
public class PasswordModificationActivity extends BaseActivity {
    private static  String CHANGEPASSWORD_URL = ConstantsAmount.BASEURL_DEFAULT + "user/ChangePassword";
    private EditText et_Old, et_New, et_Check;
    private Button btn_commit, btn_cancel;

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_password_modification);
        et_Old = (EditText) findViewById(R.id.user_old_password_edit);
        et_New = (EditText) findViewById(R.id.user_new_password_edit);
        et_Check = (EditText) findViewById(R.id.user_checked_password_edit);
        btn_commit = (Button) findViewById(R.id.commit_btn);
        btn_cancel = (Button) findViewById(R.id.back_btn);

        btn_commit.setText("提交");
        btn_cancel.setText("返回");

        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String old_pwd = et_Old.getText().toString();
                String new_pwd = et_New.getText().toString();
                String check_pwd = et_Check.getText().toString();

                if(old_pwd.length() == 0 || new_pwd.length() == 0 || check_pwd.length() == 0) {
                    Toast.makeText(mContext, "密码不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!new_pwd.equals(check_pwd)) {
                    Toast.makeText(PasswordModificationActivity.this, "两次输入的密码不一致，请认真核对!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!isPassword(old_pwd) || !isPassword(new_pwd) || !isPassword(check_pwd)) {
                    Toast.makeText(PasswordModificationActivity.this,
                            "密码由6-18位数字、字母组成！", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestParams params = new RequestParams();
                try {
                    params.setHeader("Content-Type", "application/json");
                    JSONObject obj = new JSONObject();
                    obj.put("apptoken", Utils.createAppToken());
                    obj.put("ticket", ConstantsAmount.TICKET);
                    obj.put("oldpassword", Utils.string2U8(old_pwd));
                    obj.put("newpassword", Utils.string2U8(new_pwd));
                    params.setBodyEntity(new StringEntity(obj.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                httpUtils.send(HttpRequest.HttpMethod.POST, CHANGEPASSWORD_URL, params, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {

                        try {
                            JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                Toast.makeText(PasswordModificationActivity.this, "修改成功，请重新登录!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PasswordModificationActivity.this, SplashActivity.class);
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
                                LyApplication.authToken = null;
                                startActivity(intent);
                                finish();
                                SettingActivity.instance_setting.finish();
                                HomeActivity.instance_home.finish();
                            } else if (checkRequestCode(jsonObject).equals("3")) {
                                Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // FIXME 清空无用的常量值；
                                                remove("username");
                                                remove("password");
                                                remove("authToken");
                                                Intent intent = new Intent(PasswordModificationActivity.this, SplashActivity.class);
                                                ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                                ConstantsAmount.GETLATEST_URL = null;
                                                ConstantsAmount.GETUNITSERVICES_URL = null;
                                                ConstantsAmount.GETSERVICETICKET_URL = null;
                                                ConstantsAmount.GETMENUITEM_URL = null;
                                                ConstantsAmount.MENUPOSITION = 0;
                                                ConstantsAmount.BASEURL_UNIT = null;
                                                ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                                LyApplication.authToken = null;
                                                startActivity(intent);
                                                finish();
                                                SettingActivity.instance_setting.finish();
                                                HomeActivity.instance_home.finish();
                                            }
                                        }).setCancelable(false).create();
                                alertDialog.show();
                            }  else {
                                Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {

                    }
                });
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public boolean isPassword(String mobiles) {
        Pattern p = Pattern.compile("^[a-zA-Z0-9_@]{6,18}");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
