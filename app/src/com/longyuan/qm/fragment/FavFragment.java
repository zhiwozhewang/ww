/**
 * @Title: ArticleTabFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-26 上午10:42:47
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.ArticleActivity;
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyFavListAdapter;
import com.longyuan.qm.bean.FavListDataBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshListView;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dragonsource
 * @ClassName: ArticleTabFragment
 * @Description: 收藏页面(这里用一句话描述这个类的作用)
 * @date 2014-9-26 上午10:42:47
 */
public class FavFragment extends BaseFragment {
    /*private static final String LISTDATA_URL = ConstantsAmount.BASEURL
            + "GetFavoriteArticleList.ashx?";
    private static final String DELFAV_URL = ConstantsAmount.BASEURL
            + "DeleteArticleFavorite.ashx?";*/

    private static String GETFAVLIST_URL = null;
    private static String DELFAV_URL = null;
    private TextView title;
    private PullToRefreshListView mListView;
    private List<FavListDataBean> mList = null;
    private boolean delFlag = false;
    private MyFavListAdapter adapter;
    private int position = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = ((HomeActivity) getActivity());
        Utils.getHttpRequestHeader();
        GETFAVLIST_URL =ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/GetList?";
        DELFAV_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/favorite/remove?";

        View mView = inflater.inflate(R.layout.activity_collection_fragment,
                null);
        title = (TextView) mView.findViewById(R.id.head_layout_text);
        mListView = (PullToRefreshListView) mView
                .findViewById(R.id.catalog_listview);
        title.setText("收藏");
        getFavListData();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, ArticleActivity.class);
                intent.putExtra("title_id", mList.get(i - 1).getTitleId());
                intent.putExtra("formType", "2");
                startActivity(intent);
            }
        });
        ListView listView = mListView.getRefreshableView();
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i - 1;
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("提示:").setMessage("确定删除吗？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delFavArticle(mList.get(position).getID());
                            }
                        }
                ).show();
                return true;
            }
        });
        return mView;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getFavListData
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    private void getFavListData() {

        LoadingDialog.showDialog(mContext, "正在加载...");
       /* String path = LISTDATA_URL + "authToken=" + LyApplication.authToken
                + "&pageindex=1&pagesize=1000";*/

        String path = GETFAVLIST_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&resourcekind=100&pagesize=90&pageindex=1&itemcount=0";

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                mList = new ArrayList<FavListDataBean>();
                FavListDataBean bean = null;
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            bean = new FavListDataBean();
                            JSONObject jo = (JSONObject) jsonArray.get(i);
                            bean.setID(jo.optString("ID"));
                            bean.setTitle(jo.optString("Title"));
                            bean.setTitleId(jo.optString("ArticleID"));
                            if(jo.optString("Author").equals("null")) {
                                bean.setIntroduction("");
                            } else {
                                bean.setIntroduction(jo.optString("Introduction"));
                            }
                            if(jo.optString("Author").equals("null")) {
                                bean.setAuthor("");
                            } else {
                                bean.setAuthor(jo.optString("Author"));
                            }
                            if(jo.optString("SubTitle").equals("null")) {
                                bean.setMagazineName("");
                            } else {
                                bean.setMagazineName(jo.optString("SubTitle"));
                            }
                            /*JSONObject Info_jo = jo.getJSONObject("AddonesInfo");
                            bean.setMagazineName(Info_jo.optString("MagazineName"));
                            bean.setMagazineGuid(Info_jo.optString("MagazineGuid"));
                            bean.setDate(Info_jo.optString("MagazineName") + "  " + Info_jo.optString("Year") + "年 第" + Info_jo.optString("Issue") + "期");
                            bean.setAuthor(Info_jo.optString("Author"));*/
                            mList.add(bean);
                        }
                        adapter = new MyFavListAdapter(mContext, mList);
                        mListView.setAdapter(adapter);
                    }  else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");
                                        Intent intent = new Intent(mContext, SplashActivity.class);
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


                /*try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        mList = new ArrayList<FavListDataBean>();
                        FavListDataBean bean = null;
                        JSONArray jsonArray = jsonObject
                                .getJSONArray("Favorites");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            bean = new FavListDataBean();
                            JSONObject jo = (JSONObject) jsonArray.get(i);
                            bean.setTitle(jo.optString("Title"));
                            bean.setTitleId(jo.optString("TitleId"));
                            bean.setIntroduction(jo.optString("Introduction"));
                            JSONObject Info_jo = jo
                                    .getJSONObject("AddonesInfo");
                            bean.setMagazineName(Info_jo.optString("MagazineName"));
                            bean.setMagazineGuid(Info_jo.optString("MagazineGuid"));
                            bean.setDate(Info_jo.optString("MagazineName") + "  " + Info_jo.optString("Year") + "年 第" + Info_jo.optString("Issue") + "期");
                            bean.setAuthor(Info_jo.optString("Author"));
                            mList.add(bean);
                        }
                        adapter = new MyFavListAdapter(mContext, mList);
                        mListView.setAdapter(adapter);
                    } else {
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void delFavArticle(String id) {
        LoadingDialog.showDialog(mContext, "正在删除...");
        /*String path = DELFAV_URL + "authToken=" + LyApplication.authToken
                + "&titleid=" + titleId;*/
        RequestParams params = new RequestParams();
        try {
            params.setHeader("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("apptoken", Utils.createAppToken());
            obj.put("ticket", ConstantsAmount.TICKET);
            obj.put("id", id);
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
                delFlag = false;
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        Toast.makeText(mContext, "删除收藏成功！", Toast.LENGTH_LONG).show();
                        mList.remove(position);
                        adapter = new MyFavListAdapter(mContext, mList);
                        mListView.setAdapter(adapter);
                    } else if (checkRequestCode(jsonObject).equals("3")) {
                        Dialog alertDialog = new AlertDialog.Builder(mContext).setTitle("提示:").setMessage("“" + jsonMessageParser(jsonObject) + "”")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // FIXME 清空无用的常量值；
                                        remove("username");
                                        remove("password");
                                        remove("authToken");
                                        Intent intent = new Intent(mContext, SplashActivity.class);
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
}
