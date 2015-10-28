package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
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
import com.longyuan.qm.bean.MagazineReaderBean;
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
import java.util.List;

public class MagazineReaderActivity extends BaseActivity {
    /*    private static  String LISTURL = ConstantsAmount.BASEURL
                + "getMagazineArticle.ashx?";
        private static final String ADDFAV_URL = ConstantsAmount.BASEURL
                + "AddArticleFavorite.ashx?";
        private static final String DELFAV_URL = ConstantsAmount.BASEURL
                + "DeleteArticleFavorite.ashx?";*/

    private static  String GETDETAIL_URL = null;
    private static  String ISFAV_URL = null;
    private static  String ADDFAV_URL = null;
    private static  String DELFAV_URL = null;
    private TextView magreader_issue, magreader_name, magreader_title,
            magreader_author;
    private Button fontSize, fav_button;
    private PullToRefreshScrollView mPullRefreshScrollView;
    private WebView webView;
    private WebSettings wb;
    private boolean isBig = false, isFav = false;
    private List<MagazineReaderBean> listData;
    private String doFavorite = null;
    private String title_id = "", FavoriteID = null, htmlContent = "", introduction = "", content = "";

    private ScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazinereader);

        Utils.getHttpRequestHeader();
        GETDETAIL_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/article/GetDetail?";
        ISFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/Favorite/IsFavorite?";
        ADDFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/add?";
        DELFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/remove?";

        mContext = MagazineReaderActivity.this;

        magreader_issue = (TextView) findViewById(R.id.magreader_issue);
        magreader_name = (TextView) findViewById(R.id.magreader_name);
        magreader_title = (TextView) findViewById(R.id.magreader_title);
        magreader_author = (TextView) findViewById(R.id.magreader_author);
        webView = (WebView) findViewById(R.id.webView1);
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);

        fontSize = (Button) this.findViewById(R.id.magreader_button_fontsize);
        fav_button = (Button) this.findViewById(R.id.magreader_fav_button);

        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setMode(Mode.DISABLED);

        // FIXME 获取ScrollView布局;
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        /*mPullRefreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        LoadingDialog.showDialog(mContext, "正在加载...");
                        if (listData != null) {
                            if (listData.get(0).getPreviousTitleid().equals("")) {
                                LoadingDialog.dissmissDialog();
                                Toast.makeText(MagazineReaderActivity.this,
                                        "已是第一篇", Toast.LENGTH_SHORT).show();
                            } else {
                                getDataNetWork(listData.get(0)
                                        .getPreviousTitleid());
                                mScrollView.scrollTo(0, 0);
                                DataBase.getInstance(
                                        MagazineReaderActivity.this)
                                        .updateToMagDirectoryArticleList(
                                                "1",
                                                listData.get(0)
                                                        .getPreviousTitleid()
                                        );
                            }
                        } else {
                            Toast.makeText(MagazineReaderActivity.this,
                                    "网络不给力啊.....", Toast.LENGTH_LONG).show();
                            LoadingDialog.dissmissDialog();
                        }
                        mPullRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        LoadingDialog.showDialog(mContext, "正在加载...");
                        if (listData != null) {
                            if (listData.get(0).getNextTitleid().equals("")) {
                                Toast.makeText(MagazineReaderActivity.this,
                                        "已是最后一篇", Toast.LENGTH_LONG).show();
                                LoadingDialog.dissmissDialog();
                            } else {
                                getDataNetWork(listData.get(0).getNextTitleid());
                                mScrollView.scrollTo(0, 0);
                                DataBase.getInstance(
                                        MagazineReaderActivity.this)
                                        .updateToMagDirectoryArticleList(
                                                "1",
                                                listData.get(0)
                                                        .getNextTitleid()
                                        );
                            }
                        } else {
                            LoadingDialog.dissmissDialog();
                            Toast.makeText(MagazineReaderActivity.this,
                                    "网络不给力啊.....", Toast.LENGTH_LONG).show();
                        }
                        mPullRefreshScrollView.onRefreshComplete();
                    }
                });*/

        wb = webView.getSettings();
        wb.setJavaScriptEnabled(true);
        // 图片过大时显示适应屏幕的压缩版
        wb.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        LoadingDialog.showDialog(mContext, "正在加载...");
        Intent intent = getIntent();
        title_id = intent.getStringExtra("title_id");
        getDataNetWork(title_id);

    }

    private void initView(List<MagazineReaderBean> list) {
        if (list.size() > 0) {
            magreader_issue.setText("NO." + list.get(0).getIssue() + "."
                    + list.get(0).getYear());
            magreader_name.setText(list.get(0).getMagazineName());
            magreader_title.setText(list.get(0).getTitle());
            if (list.get(0).getTitle().equals("")) {
                magreader_author.setVisibility(View.INVISIBLE);
            } else {
                magreader_author.setText(list.get(0).getAuthor());
                magreader_author.setVisibility(View.VISIBLE);
            }
            content = list.get(0).getContent();
            htmlContent = Utils.getHtmlData(content, introduction);

            webView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", "");
//            doFavorite = list.get(0).getIsFavorite();
        }
//        if (doFavorite.equals("true")) {
//            fav_button
//                    .setBackgroundResource(R.drawable.button_fav_selector_press);
//        } else {
//            fav_button.setBackgroundResource(R.drawable.button_fav_selector);
//        }
    }

    private void getDataNetWork(String titleId) {
        if (!isInternet()) {
            LoadingDialog.dissmissDialog();
            Toast.makeText(MagazineReaderActivity.this, "当前网络不可用，请检测网络!",
                    Toast.LENGTH_LONG).show();
        } else {

            HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
//            String url = LISTURL + "titleid=" + titleid + "&authToken="
//                    + LyApplication.authToken;

            String path = GETDETAIL_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&articleid=" + titleId;

            httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    LoadingDialog.dissmissDialog();
                    Toast.makeText(MagazineReaderActivity.this, "网络不给力啊.....",
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    LoadingDialog.dissmissDialog();
                    Log.e("onSuccess", arg0.result);
                    try {
                        listData = getListData(arg0.result);
                        LoadingDialog.dissmissDialog();
                        if (listData.size() > 0) {
                            initView(listData);
                        } else {
                            Toast.makeText(MagazineReaderActivity.this,
                                    "数据加载失败", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    public void onfontSizeClick(View v) {
        this.changeTextSize();
    }

    public void onBackClick(View v) {
        setRightOut(true);
        finish();
    }

    public void onFavoriteClick(View v) {
        toFav();
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
            htmlContent = Utils.getHtmlDataBig(content, introduction);
            webView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", "");
        } else {
//            wb.setDefaultFontSize(Constance.MIN_TEXT_SIZE);
            htmlContent = Utils.getHtmlDataSmall(content, introduction);
            webView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8", "");
        }

        webView.postInvalidate();

        DeviceUtils.setSettingBooleanValue("FONTSIZE", isBig,
                MagazineReaderActivity.this);
    }

    private void toFav() {
        if (isFav) {
            delFavArticle();
        } else {
            addFavAritcle();
        }
    }

    private void getIsFavData(MagazineReaderBean info) {
        LoadingDialog.showDialog(this,"同步收藏信息...");
//        String path = ISFAV_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + title_id + "&year=0&issue=0";
        String path = ISFAV_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + title_id + "&year=" + info.getYear() + "&issue=" + info.getIssue();
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                Log.e("onSuccess", objectResponseInfo.result);
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
                                        Intent intent = new Intent(MagazineReaderActivity.this, SplashActivity.class);
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
                                        MagazineDirectoryActivity.instance_mda.finish();
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
                        Toast.makeText(mContext, "删除成功!", Toast.LENGTH_LONG).show();
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
            obj.put("year", listData.get(0).getYear());
            obj.put("issue", listData.get(0).getIssue());
            obj.put("resourcekind", "101");
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
//                        getIsFavData(info);
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

    /*private void delFavArticle() {
        LoadingDialog.showDialog(mContext, "正在加载...");
        String path = DELFAV_URL + "authToken=" + LyApplication.authToken
                + "&titleid=" + title_id;

        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Toast.makeText(mContext, "删除失败!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        fav_button
                                .setBackgroundResource(R.drawable.button_fav_selector);
                        isFav = false;
                        Toast.makeText(mContext, "删除收藏成功!", Toast.LENGTH_LONG)
                                .show();
                        LoadingDialog.dissmissDialog();
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    /*private void addFavAritcle() {
        LoadingDialog.showDialog(mContext, "正在加载...");
        String path = ADDFAV_URL + "authToken=" + LyApplication.authToken
                + "&fromtype=1&titleid=" + title_id;
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

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
                        Toast.makeText(mContext, "收藏成功!", Toast.LENGTH_LONG)
                                .show();
                        fav_button
                                .setBackgroundResource(R.drawable.button_fav_selector_press);
                        isFav = true;
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    private List<MagazineReaderBean> getListData(String json) throws JSONException {
        List<MagazineReaderBean> list = new ArrayList<MagazineReaderBean>();
        MagazineReaderBean info = new MagazineReaderBean();
        JSONObject jsonObject = new JSONObject(json);
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONObject jo = jsonObject.getJSONObject("Data");
            info.setTitleid(jo.optString("ArticleID"));
            info.setTitle(jo.optString("Title"));
            info.setSubTitile(jo.optString("SubTitle"));
            info.setAuthor(jo.optString("Author"));
            info.setIntroduction(jo.optString("Introduction"));
            info.setContent(jo.optString("Content"));
            info.setMagazineName(jo.optString("MagazineName"));
            info.setMagazineGUID(jo.optString("MagazineGuid"));
            info.setYear(jo.optString("Year"));
            info.setIssue(jo.optString("Issue"));
//            Log.e("", "" + jo.getJSONArray("KeyWordList").toString());

            info.setPreviousTitleid(jo.getJSONObject("PreviousArticle").optString("ArticleID"));
            info.setNextTitleid(jo.getJSONObject("NextArticle").optString("ArticleID"));
            list.add(info);
            getIsFavData(info);
        } else if (checkRequestCode(jsonObject).equals("3")) {
            Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // FIXME 清空无用的常量值；
                            remove("username");
                            remove("password");
                            remove("authToken");
                            Intent intent = new Intent(MagazineReaderActivity.this, SplashActivity.class);
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
                            MagazineDirectoryActivity.instance_mda.finish();
                            HomeActivity.instance_home.finish();
                            finish();
                        }
                    }).setCancelable(false).create();
            alertDialog.show();
        } else {
            Toast.makeText(MagazineReaderActivity.this, jsonMessageParser(jsonObject), Toast.LENGTH_SHORT).show();
        }
        return list;
    }

    /*private void judgeIsFavorite(MagazineReaderBean info) {


        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.GET, path , new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                LoadingDialog.dissmissDialog();
                Log.e("judgeIsFavorite : onSuccess",objectResponseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }
        });
    }*/

    /*private List<MagazineReaderBean> getListData(String json)
            throws JSONException {
        List<MagazineReaderBean> list = new ArrayList<MagazineReaderBean>();
        MagazineReaderBean info = new MagazineReaderBean();

        JSONObject jsonObject = new JSONObject(json);

        info.setMagazineName(jsonObject.optString("MagazineName"));
        info.setMagazineGUID(jsonObject.optString("MagazineGUID"));
        info.setIssue(jsonObject.optString("Issue"));
        info.setYear(jsonObject.optString("Year"));
        info.setTitle(jsonObject.optString("Title"));
        info.setAuthor(jsonObject.optString("Author"));
        info.setContent(jsonObject.optString("Content"));
        info.setIconList(jsonObject.optString("IconList"));
        info.setCategoryName(jsonObject.optString("CategoryName"));
        info.setSubTitile(jsonObject.optString("SubTitile"));
        info.setIntroduction(jsonObject.optString("Introduction"));
        info.setArticleImgList(jsonObject.optString("ArticleImgList"));
        info.setPageCount(jsonObject.optString("PageCount"));

        JSONObject PreviousJsonObject = jsonObject
                .getJSONObject("PreviousArticle");

        info.setPreviousTitle(PreviousJsonObject.optString("Title"));
        info.setPreviousTitleid(PreviousJsonObject.optString("Titleid"));
        JSONObject NextJsonObject = jsonObject.getJSONObject("NextArticle");
        info.setNextTitle(NextJsonObject.optString("Title"));
        info.setNextTitleid(NextJsonObject.optString("Titleid"));
        info.setIsFavorite(jsonObject.optString("IsFavorite"));
        if (info.getIsFavorite().equals("1")) {
            fav_button
                    .setBackgroundResource(R.drawable.button_fav_selector_press);
            isFav = true;
        } else {
            fav_button.setBackgroundResource(R.drawable.button_fav_selector);
            isFav = false;
        }
        list.add(info);
        return list;
    }*/
}