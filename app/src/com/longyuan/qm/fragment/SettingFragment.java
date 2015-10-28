package com.longyuan.qm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import java.util.ArrayList;

/**
 * Created by myf on 14/10/24.
 */
public class SettingFragment extends BaseFragment {
    private TextView tv_logout, tv_title_name, tv_account, tv_ver;
    private static String LOGOUT_URL = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Utils.getHttpRequestHeader();
        LOGOUT_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/Logout?";

        View mView = inflater.inflate(R.layout.setting_layout, null);

        tv_title_name = (TextView) mView.findViewById(R.id.head_layout_text);
        tv_logout = (TextView) mView.findViewById(R.id.tv_logout);
        tv_account = (TextView) mView.findViewById(R.id.tv_account);
        tv_ver = (TextView) mView.findViewById(R.id.tv_about);

        tv_title_name.setText("设置");
        tv_account.setText(mSp.getString("username", ""));
        tv_ver.setText("v " + Utils.getVersionName(mContext));

        tv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog.showDialog(mContext, "正在注销...");
                String path = LOGOUT_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
                HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                        LoadingDialog.dissmissDialog();
                        // FIXME 清空无用的常量值；
                        remove("username");
                        remove("password");
                        remove("authToken");
                        ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                        ConstantsAmount.MENUPOSITION = 0;

                        LyApplication.authToken = null;
                        Intent intent = new Intent(getActivity(), SplashActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        LoadingDialog.dissmissDialog();
                        Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return mView;
    }
}
