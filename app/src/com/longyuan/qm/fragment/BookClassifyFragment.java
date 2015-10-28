package com.longyuan.qm.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
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
import com.longyuan.qm.activity.BookDetailActivity;
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyBookListAdapter;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.BookListBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class BookClassifyFragment extends BaseFragment implements OnItemClickListener {
    private static String LISTDATA_URL = ConstantsAmount.BASEURL
            + "Getbooklist.ashx?";
    private static String TABINDICATOR_URL = null;
    private PullToRefreshGridView mGridView;
    private GridView gridView;
    private ArrayList<BookListBean> bookLists = null;
    private ArrayList<BookClassifyBean> mBookClassifyList = null;
    private ArrayList<BookClassifyBean> getMoreList = new ArrayList<BookClassifyBean>();
    private MyBookListAdapter adapter;
    private int position = 0;
    // 分页+默认值
    private int pageindex = 1;
    private int pagesize = 30;
    private int itemCount = 0;
    private BookDetailFragment bookDetailFragment = null;
    private String sdPath = Environment.getExternalStorageDirectory()
            .toString();
    private String baseBookPath = sdPath + "/LYyouyue/";
    private boolean canGetMore = true;
    private String user_name;

    public BookClassifyFragment() {
    }

    public BookClassifyFragment(int position) {
        this.position = position;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(
                R.layout.bookshop_classify_activity_normal, null);

        Utils.getHttpRequestHeader();
        TABINDICATOR_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "book/GetBookByCategory?";

        user_name = mSp.getString("username", null);
        mContext = getActivity();
        mGridView = (PullToRefreshGridView) mView
                .findViewById(R.id.book_Classify_GridView);

        Bundle bundle = getArguments();
        bookLists = (ArrayList<BookListBean>) bundle.getSerializable("list");

        getBookListData(pageindex, pagesize);

        gridView = mGridView.getRefreshableView();
        mGridView.setOnItemClickListener(this);
        mGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {

            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                pageindex = 1;
                getBookListData(1, pagesize);
            }
        });

        mGridView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (itemCount > pageindex * pagesize) {
                    pageindex++;
                    getBookListData(pageindex, pagesize);
                    adapter.setListData(getMoreList);
                    adapter.notifyDataSetChanged();
                    gridView.scrollTo(0, pagesize * pageindex / 3);
                }
//                else {
//                    Toast.makeText(mContext, "已是最后一页!", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {
//        FragmentTransaction t = getActivity()
//                .getSupportFragmentManager().beginTransaction();
//        bookDetailFragment = new BookDetailFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelableArrayList("mBookClassifyList", mBookClassifyList);
//        bundle.putInt("position", arg2);
//        bookDetailFragment.setArguments(bundle);
//        t.hide(this);
//        t.add(R.id.fl_main, bookDetailFragment, "BC");
//        t.addToBackStack("BC");
//        t.commit();

        Intent intent = new Intent(mContext, BookDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("bList", mBookClassifyList);
        bundle.putInt("position", arg2);
        intent.putExtra("bundle", bundle);
        startActivity(intent);
    }

    private List<BookClassifyBean> getBookListData(final int pageIndex, final int pageSize) {
//        String path = LISTDATA_URL + "authtoken=" + LyApplication.authToken
//                + "&categoryid=" + bookLists.get(position).getCategoryID()
//                + "&Pageindex=" + pageIndex + "&pagesize=" + pageSize + "&orderby=ordernumber";
        String path = TABINDICATOR_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&categorycode=" + bookLists.get(position).getCategoryID() + "&booktype=5" + "&pagesize=" + pageSize + "&pageindex=" + pageIndex + "&itemcount=" + itemCount;
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                mGridView.onRefreshComplete();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                mGridView.onRefreshComplete();
                try {
                    mBookClassifyList = new ArrayList<BookClassifyBean>();
                    BookClassifyBean mBookClassifyInfo = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            mBookClassifyInfo = new BookClassifyBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            mBookClassifyInfo.setBookGuid(jo.optString("BookGuid"));
                            mBookClassifyInfo.setBookName(jo.optString("BookName"));
                            mBookClassifyInfo.setAuthor(jo.optString("Author"));
                            mBookClassifyInfo.setPublishName(jo.optString("PublishName"));
                            mBookClassifyInfo.setPubDate(jo.optString("PubDate"));
                            mBookClassifyInfo.setBookCover(jo.optString("CoverImages"));
                            mBookClassifyInfo.setBookPath(baseBookPath
                                    + jo.optString("BookName") + ".epub");
//                            itemCount = Integer.parseInt(jsonObject.optString("ItemCount"));

                            mBookClassifyInfo.setCategory(bookLists.get(position).getCategoryName());
                            //FIXME 接口返回多种类型，需要加判断；
                            mBookClassifyList.add(mBookClassifyInfo);
                        }

//                        adapter = new MyBookListAdaper(mContext,
//                                mBookClassifyList, mGridView);
//                        mGridView.setAdapter(adapter);

                        if (pageIndex == 1) {
                            getMoreList = mBookClassifyList;
                            adapter = new MyBookListAdapter(mContext,
                                    mBookClassifyList, mGridView);
                            mGridView.setAdapter(adapter);
                        } else {
                            getMoreList.addAll(mBookClassifyList);
                            adapter.setListData(getMoreList);
                            adapter.notifyDataSetChanged();
                            gridView.scrollTo(0, pageSize * pageIndex / 3);
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

//                try {
//                    mGridView.onRefreshComplete();
//                    mBookClassifyList = new ArrayList<BookClassifyBean>();
//                    BookClassifyBean mBookClassifyInfo = null;
//                    JSONObject jsonObject = new JSONObject(arg0.result);
//                    if (checkRequestCode(jsonObject).equals("1")) {
//                        JSONArray ja = jsonObject.getJSONArray("BookList");
//                        for (int i = 0; i < ja.length(); i++) {
//                            mBookClassifyInfo = new BookClassifyBean();
//                            JSONObject jo = (JSONObject) ja.get(i);
//                            mBookClassifyInfo.setBookName(jo
//                                    .optString("BookName"));
//                            mBookClassifyInfo.setBookGuid(jo
//                                    .optString("BookGuid"));
//                            mBookClassifyInfo.setOrderNumber(jo
//                                    .optString("OrderNumber"));
//                            mBookClassifyInfo.setPubDate(jo
//                                    .optString("PubDate"));
//                            mBookClassifyInfo.setAuthor(jo.optString("Author"));
//                            mBookClassifyInfo.setCategory(jo
//                                    .optString("Category"));
//                            mBookClassifyInfo.setPublishName(jo
//                                    .optString("PublishName"));
//                            mBookClassifyInfo.setISBN(jo.optString("ISBN"));
//                            mBookClassifyInfo.setNote(jo.optString("Note"));
//                            mBookClassifyInfo.setBookCover(jo
//                                    .optString("BookCover"));
//                            mBookClassifyInfo.setDownloadUrl(jo
//                                    .optString("DownloadUrl"));
//
//                            mBookClassifyInfo.setBookPath(baseBookPath
//                                    + jo.optString("BookName") + ".epub");
//                            mBookClassifyInfo.setBookAddTime(System
//                                    .currentTimeMillis() + "");
//
//                            mBookClassifyInfo.setUserName(user_name);
//                            mBookClassifyList.add(mBookClassifyInfo);
//                        }
//                        adapter = new MyBookListAdaper(mContext,
//                                mBookClassifyList, mGridView);
//                        mGridView.setAdapter(adapter);
//                    } else {
//                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
//                                Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        });
        return mBookClassifyList;
    }
}
