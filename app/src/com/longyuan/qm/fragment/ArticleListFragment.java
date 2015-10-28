/**
 * @Title: ArticleListFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: 资讯列表(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-26 下午4:57:33
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
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
import com.longyuan.qm.adapter.MyArticleListAdapter;
import com.longyuan.qm.bean.ArticleListItemBean;
import com.longyuan.qm.bean.ArticletabItemBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author dragonsource
 * @ClassName: ArticleListFragment
 * @Description: 资讯页ListView列表(这里用一句话描述这个类的作用)
 * @date 2014-9-26 下午4:57:33
 */
@SuppressLint("ValidFragment")
public class ArticleListFragment extends BaseFragment implements
        OnItemClickListener {
    private static String LISTDATA_URL = ConstantsAmount.BASEURL
            + "GetEssentialArticleList.ashx?";
    private static String GETLISTDATAURL = null;
    private PullToRefreshListView mListView;
    private ArrayList<ArticletabItemBean> mBundleList = null;
    private int mPagePosition = 0;
    private List<ArticleListItemBean> mArticleListData = null;
    private int pagesize = 50, pageindex = 1, ordertype = 1, itemCount = 0;
    private String categoryCode = null;
    private MyArticleListAdapter adapter;
    private List<ArticleListItemBean> allList = new ArrayList<ArticleListItemBean>();
    private ListView listView;

    public ArticleListFragment(int position) {
        super();
        this.mPagePosition = position;
        mContext = getActivity();
    }

    public ArticleListFragment() {
    }

    public static String getTime(String user_time) {
        String str = null;
//		TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒SSS毫秒");
        Date d;
        try {
            d = sdf.parse(user_time);
            long l = d.getTime();
            str = String.valueOf(l);
            System.out.println(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.getHttpRequestHeader();
        GETLISTDATAURL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "compilation/article/catalog?";

        View mView = inflater.inflate(R.layout.article_list_layout, null);
        mListView = (PullToRefreshListView) mView
                .findViewById(R.id.Article_listview);

        listView = mListView.getRefreshableView();

        mListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                pageindex = 1;
                getArticleListData(pageindex, pagesize, ordertype);
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
//                getArticleListData(pageindex++, pagesize, ordertype);
            }
        });

        mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (itemCount > pageindex * pagesize) {
                    pageindex++;
                    getArticleListData(pageindex, pagesize, ordertype);
                }
            }
        });

        mListView.setOnItemClickListener(this);
        init();
        return mView;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {

//        String time = "2014年11月12日 09时16分32秒238毫秒";
//        System.out.println(time);
//        Log.e("time",time);
//        Log.e("time",getTime(time));

        Bundle bundle = getArguments();
        mBundleList = (ArrayList<ArticletabItemBean>) bundle.getSerializable("list");
        categoryCode = mBundleList.get(mPagePosition).getCategoryCode();
        getArticleListData(pageindex, pagesize, ordertype);
    }

    // 网络请求数据
    private List<ArticleListItemBean> getArticleListData(final int pageIndex,
                                                         final int pageSize, int orderType) {
        String path = GETLISTDATAURL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&categorycode=" + categoryCode + "&pagesize=" + pageSize + "&pageindex=" + pageIndex + "&itemcount=" + itemCount;
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mListView.onRefreshComplete();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                mListView.onRefreshComplete();
                try {
                    mArticleListData = new ArrayList<ArticleListItemBean>();
                    ArticleListItemBean bean = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            bean = new ArticleListItemBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            bean.setTitleID(jo.optString("ArticleID"));
                            bean.setMagazineName(jo.optString("SubTitle"));
                            bean.setTitle(jo.optString("Title"));
                            if (jo.optString("KeyWord").equals("null")) {
                                bean.setMagazineName("");
                            } else {
                                bean.setMagazineName(jo.optString("KeyWord"));
                            }
                            if (jo.optString("Author").equals("null")) {
                                bean.setAuthor("");
                            } else {
                                bean.setAuthor(jo.optString("Author"));
                            }
                            if (jo.optString("Summary").equals("null")) {
                                bean.setIntroduction("");
                            } else {
                                bean.setIntroduction(jo.optString("Summary"));
                            }
                            if (jo.optString("BigImgName").equals("null")) {
                                bean.setArticleImgList("");
                            } else {
                                bean.setArticleImgList(jo.optString("BigImgName"));
                            }
                            if (jsonObject.optString("ItemCount").equals("null")) {
                                itemCount = 0;
                            } else {
                                itemCount = Integer.parseInt(jsonObject.optString("ItemCount"));

                            }
                            bean.setYear(jo.optString("UpdateDate"));
                            mArticleListData.add(bean);
                        }

                        if (pageIndex == 1) {
                            Log.e("==", "" + pageIndex);
                            adapter = new MyArticleListAdapter(mArticleListData, mContext, mListView);
                            mListView.setAdapter(adapter);
                            allList = new ArrayList<ArticleListItemBean>();
                            allList.addAll(mArticleListData);
                        } else if (itemCount > pageIndex * pageSize) {
                            Log.e(">", "" + pageIndex);
                            allList.addAll(mArticleListData);
                            adapter.getMoreData(allList);
                            adapter.notifyDataSetChanged();
                            listView.scrollTo(0, pageIndex * pageSize);
                        } else {
                            Log.e("<", "" + pageIndex);
                            allList.addAll(mArticleListData);
                            adapter.getMoreData(allList);
                            adapter.notifyDataSetChanged();
                            listView.scrollTo(0, pageIndex * pageSize);
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
                                        Intent intent = new Intent(mContext, SplashActivity.class);
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
                    mListView.onRefreshComplete();
					mArticleListData = new ArrayList<ArticleListItemBean>();
					ArticleListItemBean bean = null;
					JSONObject jsonObject = new JSONObject(arg0.result);
					if (checkRequestCode(jsonObject).equals("1")) {
                        itemCount = Integer.parseInt(jsonObject.optString("ItemCount"));
                        if(itemCount/pageSize>pageIndex){
						JSONArray ja = jsonObject.getJSONArray("Articlelist");
						for (int i = 0; i < ja.length(); i++) {
							bean = new ArticleListItemBean();
							JSONObject jo = (JSONObject) ja.get(i);
							bean.setMagazineName(jo.optString("MagazineName"));
							bean.setMagazineGUID(jo.optString("MagazineGUID"));
							bean.setYear(jo.optString("Year"));
							bean.setIssue(jo.optString("Issue"));
							bean.setTitleID(jo.optString("TitleID"));
							bean.setTitle(jo.optString("Title"));
							bean.setAuthor(jo.optString("Author"));
							bean.setIntroduction(jo.optString("Introduction"));
							bean.setCategoryCode(jo.optString("CategoryCode"));
							bean.setPubStartDate(jo.optString("PubStartDate"));
                            String imgList = jo.optString("ArticleImgList");

                            //FIXME 文章列表接口会返回一个字符串，用逗号分割了多个图片地址，现取第一个图片，输出至表单；

                            String[] imgArg = imgList.split(",");
                            bean.setArticleImgList(imgArg[0]);
							bean.setArticleImgWidth(jo
									.optString("ArticleImgWidth"));
							bean.setArticleImgHeight(jo
									.optString("ArticleImgHeight"));
							bean.setMagazineLogo(jo.optString("MagazineLogo"));
							bean.setSource(jo.optString("Source"));
							mArticleListData.add(bean);
                        }
                            if(pageindex > 1){
                                List<ArticleListItemBean> AllList = new ArrayList<ArticleListItemBean>();
                                AllList.addAll(mArticleListData);
                                mListView.setAdapter(new MyArticleListAdapter(
                                        AllList, mContext, mListView));
                                mListView.scrollTo(0,pageIndex * pageSize);
                            }else{
                                mListView.setAdapter(new MyArticleListAdapter(
                                        mArticleListData, mContext, mListView));
                            }
						} else {
                            Toast.makeText(mContext, "没有了", Toast.LENGTH_LONG).show();
                        }
					} else {
						Toast.makeText(mContext, jsonMessageParser(jsonObject),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
            }
        });
        return mArticleListData;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Intent intent = new Intent(getActivity(), ArticleActivity.class);
        Log.e("pageindex", pageindex + "");
        if (pageindex == 1) {
            Log.e("mArticleListData", "" + mArticleListData.size());
            intent.putExtra("list", (Serializable) mArticleListData);
        } else {
            Log.e("allList", "" + allList.size());
            intent.putExtra("list", (Serializable) allList);
        }
        intent.putExtra("position", arg2 - 1);
        //区分是从咨询页面跳转还是从收藏页面跳转（1为资讯，2为收藏）;
        intent.putExtra("code", 1);
        startActivity(intent);
    }
}
