/**
 * @Title: ArticalActivity.java
 * @Package com.longyuan.qm.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author Android
 * @date 2014-10-19 下午4:18:24
 * @version V1.0
 */
package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.ArticleListItemBean;
import com.longyuan.qm.bean.FavListDataBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.DeviceUtils;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshScrollView;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Android
 * @ClassName: ArticalActivity
 * @Description: 文章阅读页面(这里用一句话描述这个类的作用)
 * @date 2014-10-19 下午4:18:24
 */
public class ArticleActivity extends BaseActivity {
    private static String GETDETAIL_URL = null;
    private static String ISFAV_URL = null;
    private static String ADDFAV_URL = null;
    private static String DELFAV_URL = null;
    private ImageView icon;
    private Button fontSize, fav_button, left_button;
    private TextView title_text, text_auth;
    private WebView webView;
    private WebSettings wb;
    private String FavoriteID = null;
    private ArrayList<ArticleListItemBean> list = null;
    private ArrayList<FavListDataBean> fList = null;

    private int mListPostion = 0;

    //	private static final String LISTDATA_URL = ConstantsAmount.BASEURL
    //		+ "getMagazineArticle.ashx?";
//    private static final String ADDFAV_URL = ConstantsAmount.BASEURL
//            + "AddArticleFavorite.ashx?";
//    private static final String DELFAV_URL = ConstantsAmount.BASEURL
//            + "DeleteArticleFavorite.ashx?";
    private PullToRefreshScrollView mPullRefreshScrollView;
    private ScrollView mScrollView;
    private boolean isBig = false, isFav;
    private String title_id, previous_TitleId, next_TitleId, content, author, summary, introduction, baseContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Utils.getHttpRequestHeader();
        GETDETAIL_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "compilation/article/GetDetail?";
        ISFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/Favorite/IsFavorite?";
        ADDFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/add?";
        DELFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/remove?";

        mContext = this;
        icon = (ImageView) findViewById(R.id.m_icon);
        fontSize = (Button) findViewById(R.id.button_fontsize);
        fav_button = (Button) findViewById(R.id.fav_button);
        left_button = (Button) findViewById(R.id.back_button);
        title_text = (TextView) findViewById(R.id.txtTitle);
        text_auth = (TextView) findViewById(R.id.text_auth);
        webView = (WebView) findViewById(R.id.webView1);
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setMode(Mode.DISABLED);

        wb = webView.getSettings();

        // FIXME 获取ScrollView布局;
        mScrollView = mPullRefreshScrollView.getRefreshableView();

        left_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setRightOut(true);
                finish();
            }
        });

        fontSize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeTextSize();
            }
        });

        fav_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isInternet()) {
                    tofav();
                } else {
                    Toast.makeText(mContext,
                            ConstantsAmount.BAD_NETWORK_CONNECTION,
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        Intent intent = getIntent();
        if (intent.getStringExtra("title_id") != null) {
            title_id = intent.getStringExtra("title_id");
        } else {
//        fromtype = intent.getStringExtra("formType");
            mListPostion = intent.getIntExtra("position", 0);
            int code = intent.getIntExtra("code", 3);
            if (code == 1) {
                list = new ArrayList<ArticleListItemBean>();
                list = (ArrayList<ArticleListItemBean>) intent.getSerializableExtra("list");
                title_id = list.get(mListPostion).getTitleID();
            } else if (code == 2) {
                fList = new ArrayList<FavListDataBean>();
                fList = (ArrayList<FavListDataBean>) intent.getSerializableExtra("fList");
                title_id = fList.get(mListPostion).getTitleId();
            }
        }
        if (isInternet()) {
            if (title_id != null) {
                getArticalData(title_id);
            } else {
                Toast.makeText(mContext, "请求失败!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getArticalData
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void getArticalData(final String t_id) {
        LoadingDialog.showDialog(mContext, "正在加载...");

//		String path = LISTDATA_URL + "titleid=" + titleid + "&authToken="
//				+ LyApplication.authToken + "&fromtype="+fromtype;

        String path = GETDETAIL_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&articleid=" + t_id;
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                mPullRefreshScrollView.onRefreshComplete();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
                icon.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {

                LoadingDialog.dissmissDialog();
                mPullRefreshScrollView.onRefreshComplete();
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONObject jo = jsonObject.getJSONObject("Data");

                        title_id = jo.optString("ArticleID");

                        if (jo.optString("Author").equals("null")) {
                            author = "";
                            text_auth.setVisibility(View.GONE);
                        } else {
                            author = jo.optString("Author");
                            text_auth.setVisibility(View.VISIBLE);
                            text_auth.setText(author);
                        }

                        if (jo.optString("Introduction").equals("null")) {
                            introduction = "";
                        } else {
                            introduction = jo.optString("Introduction");
                        }

                        if (jo.optString("Summary").equals("null")) {
                            summary = "";
                        } else {
                            summary = jo.optString("Summary");
                        }

                        if (jo.optString("PreviousArticle").equals("null")) {
                            previous_TitleId = "";
                        } else {
                            JSONObject jo_n = jo.getJSONObject("PreviousArticle");
                            previous_TitleId = jo_n.optString("ArticleID");
                        }
                        if (jo.optString("NextArticle").equals("null")) {
                            next_TitleId = "";
                        } else {
                            JSONObject jo_n = jo.getJSONObject("NextArticle");
                            next_TitleId = jo_n.optString("ArticleID");
                        }
                        if (jo.optString("Content").equals("null")) {
                            content = "";
                            baseContent = "";
                        } else {
                            baseContent = jo.optString("Content");
                            content = Utils.getHtmlData(baseContent, introduction);
                        }
                        if (jo.optString("Title").equals("null")) {
                            title_text.setText("");
                        } else {
                            title_text.setText(jo.optString("Title"));
                        }
                        webView.loadDataWithBaseURL("", content,
                                "text/html", "utf-8", "");

                        getIsFavData(title_id);
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");
                                        Intent intent = new Intent(ArticleActivity.this, SplashActivity.class);
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
                                        HomeActivity.instance_home.finish();
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
            }
        });
    }

    private void getIsFavData(String titleId) {
        LoadingDialog.showDialog(mContext, "正在加载...");
        String path = ISFAV_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + titleId + "&year=0&issue=0";
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                LoadingDialog.dissmissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONObject jo = jsonObject.getJSONObject("Data");
                        FavoriteID = jo.optString("FavoriteID");
                        if (jo.optString("IsFavorite").equals("true")) {
                            fav_button
                                    .setBackgroundResource(R.drawable.button_fav_selector_press);
                            isFav = true;
                        } else {
                            fav_button
                                    .setBackgroundResource(R.drawable.button_fav_selector);
                            isFav = false;
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
                                        Intent intent = new Intent(ArticleActivity.this, SplashActivity.class);
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
                                        HomeActivity.instance_home.finish();
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
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LoadingDialog.dissmissDialog();

            }
        });
    }

    private void delFavArticle() {
        LoadingDialog.showDialog(mContext, "正在加载...");
        /*String path = DELFAV_URL + "authToken=" + LyApplication.authToken
                + "&titleid=" + title_id;*/
        RequestParams params = new RequestParams();
        try {
            params.setHeader("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("apptoken", Utils.createAppToken());
            obj.put("ticket", ConstantsAmount.TICKET);
            obj.put("id", FavoriteID);
            params.setBodyEntity(new StringEntity(obj.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.POST, DELFAV_URL, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, "删除失败!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        fav_button
                                .setBackgroundResource(R.drawable.button_fav_selector);
                        isFav = false;
                        Toast.makeText(mContext, "删除成功!", Toast.LENGTH_SHORT).show();
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");
                                        Intent intent = new Intent(ArticleActivity.this, SplashActivity.class);
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
                                        HomeActivity.instance_home.finish();
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
            }
        });
    }

    private void addFavAritcle() {
        LoadingDialog.showDialog(mContext, "正在加载...");
//        String path = ADDFAV_URL + "authToken=" + LyApplication.authToken
//                + "&fromtype=2&titleid=" + title_id;

//        String path = ADDFAV_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + title_id + "&resourcekind=6";

        RequestParams params = new RequestParams();
        try {
            params.setHeader("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("apptoken", Utils.createAppToken());
            obj.put("ticket", ConstantsAmount.TICKET);
            obj.put("resourceid", title_id);
            obj.put("year", "0");
            obj.put("issue", "0");
            obj.put("resourcekind", "103");
            params.setBodyEntity(new StringEntity(obj.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.POST, ADDFAV_URL, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, "收藏失败!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        Toast.makeText(mContext, "收藏成功!", Toast.LENGTH_LONG)
                                .show();
                        FavoriteID = jsonObject.optString("Data");
                        fav_button
                                .setBackgroundResource(R.drawable.button_fav_selector_press);
                        isFav = true;
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");
                                        Intent intent = new Intent(ArticleActivity.this, SplashActivity.class);
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
                                        HomeActivity.instance_home.finish();
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
            }
        });
    }

    private void changeTextSize() {
        if (!isBig) {
            fontSize.setBackgroundResource(R.drawable.button_font_selector_press);
            isBig = true;
        } else {
            fontSize.setBackgroundResource(R.drawable.button_font_selector);
            isBig = false;
        }

        if (isBig) {
//            wb.setDefaultFontSize(Constance.MAX_TEXT_SIZE);
            content = Utils.getHtmlDataBig(baseContent, introduction);
            webView.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
        } else {
//            wb.setDefaultFontSize(Constance.MIN_TEXT_SIZE);
            content = Utils.getHtmlDataSmall(baseContent, introduction);
            webView.loadDataWithBaseURL("", content, "text/html", "utf-8", "");
        }
        webView.postInvalidate();

        DeviceUtils.setSettingBooleanValue("FONTSIZE", isBig,
                ArticleActivity.this);
    }

    private void tofav() {
        if (isFav) {
            delFavArticle();
        } else {
            addFavAritcle();
        }
    }
}