package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.longyuan.qm.adapter.MyUnitListAdapter;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.UnitBean;
import com.longyuan.qm.fragment.ArticleTabFragment;
import com.longyuan.qm.fragment.BookShopFragment;
import com.longyuan.qm.fragment.MagazineFragment;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.utils.VersionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Android on 2014/12/18.
 */
public class ChooseUnitActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    private ListView listView;
    private ImageView back;
    private HttpUtils httpUtils = null;
    private ApplicationInfo appInfo;
    private String unitName = "", name = "", phoneNumber = "", logoUrl = "", backgroundUrl = "", appId = "";

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_unit_choose);

        back = (ImageView) findViewById(R.id.back);
        listView = (ListView) findViewById(R.id.unit_list);
        listView.setAdapter(new MyUnitListAdapter(ConstantsAmount.UNITBEANLIST, ChooseUnitActivity.this));
        back.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
    }

    @Override
    public void onClick(View view) {
        remove("username");
        remove("password");
        remove("authToken");
        Intent intent = new Intent(ChooseUnitActivity.this, SplashActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ConstantsAmount.BASEURL_UNIT = ConstantsAmount.UNITBEANLIST.get(i).getUnitBaseUrl();
        unitName = ConstantsAmount.UNITBEANLIST.get(i).getUnitName();
//        Log.e("ChooseUnitActivity : ConstantsAmount.BASEURL_UNIT", "" + ConstantsAmount.BASEURL_UNIT);

        Utils.getHttpRequestHeader();

        ConstantsAmount.GETLATEST_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "ClientApp/GetLatest?";
        ConstantsAmount.GETUNITSERVICES_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "unit/GetServices?";
        ConstantsAmount.GETSERVICETICKET_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "unit/GetServiceTicket?";
        ConstantsAmount.GETMENUITEM_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "menu/GetAll?";
        getUnitServices();
    }

    private void getUnitServices() {
//        String path = GETUNITSERVICES_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket=" + ConstantsAmount.TICKET;
        String path = ConstantsAmount.GETUNITSERVICES_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;
        httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                String serviceID = "";
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);

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
                                        Intent intent = new Intent(ChooseUnitActivity.this, SplashActivity.class);
                                        ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                        ConstantsAmount.GETLATEST_URL = null;
                                        ConstantsAmount.GETUNITSERVICES_URL = null;
                                        ConstantsAmount.GETSERVICETICKET_URL = null;
                                        ConstantsAmount.GETMENUITEM_URL = null;
                                        ConstantsAmount.MENUPOSITION = 0;
                                        ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                        ConstantsAmount.BASEURL_UNIT = null;
                                        LyApplication.authToken = null;
                                        startActivity(intent);
                                        finish();
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getServiceTicket(serviceID);
            }

            @Override
            public void onFailure(HttpException e, String s) {
            }
        });
    }

    private void getServiceTicket(String serviceId) {
        String path = ConstantsAmount.GETSERVICETICKET_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&chooseserviceid=" + serviceId;
        httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
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
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> stringResponseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(stringResponseInfo.result);
                    if (jsonObject.optString("Data").equals("null")) {
                        getMenuItem();
                    } else {
                        if (checkRequestCode(jsonObject).equals("1")) {
                            String localVersionName = Utils.getVersionName(ChooseUnitActivity.this);
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
                                            Intent intent = new Intent(ChooseUnitActivity.this, SplashActivity.class);
                                            ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                            ConstantsAmount.GETLATEST_URL = null;
                                            ConstantsAmount.GETUNITSERVICES_URL = null;
                                            ConstantsAmount.GETSERVICETICKET_URL = null;
                                            ConstantsAmount.GETMENUITEM_URL = null;
                                            ConstantsAmount.MENUPOSITION = 0;
                                            ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                            ConstantsAmount.BASEURL_UNIT = null;
                                            LyApplication.authToken = null;
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).setCancelable(false).create();
                            alertDialog.show();
                        } else {
                            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                    Toast.LENGTH_LONG).show();
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
            }
        });
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

    private void getMenuItem() {
        String path = ConstantsAmount.GETMENUITEM_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET;

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {

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
                                bean = new MenuBean(jo.optString("MenuName"), R.drawable.icon_mgnz, new MagazineFragment(), R.drawable.icon_art_press, R.drawable.icon_mgnz);
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
                                        Intent intent = new Intent(ChooseUnitActivity.this, SplashActivity.class);
                                        ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
                                        ConstantsAmount.GETLATEST_URL = null;
                                        ConstantsAmount.GETUNITSERVICES_URL = null;
                                        ConstantsAmount.GETSERVICETICKET_URL = null;
                                        ConstantsAmount.GETMENUITEM_URL = null;
                                        ConstantsAmount.MENUPOSITION = 0;
                                        ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                        ConstantsAmount.BASEURL_UNIT = null;
                                        LyApplication.authToken = null;
                                        startActivity(intent);
                                        finish();
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(ChooseUnitActivity.this, jsonMessageParser(jsonObject), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                Toast.makeText(ChooseUnitActivity.this, ConstantsAmount.REQUEST_ONFAILURE, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void doIntent() {
        Intent intent = new Intent(mContext, HomeActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("phoneNumber", phoneNumber);
        intent.putExtra("unitName", unitName);
        intent.putExtra("logoUrl", logoUrl);
        intent.putExtra("backgroundUrl", backgroundUrl);
        intent.putExtra("isInternet", isInternet());
        startActivity(intent);
        setRightOut(false);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConstantsAmount.UNITBEANLIST = new ArrayList<UnitBean>();
    }
}
