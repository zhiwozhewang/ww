package com.longyuan.qm.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.MagazineDetailActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyMagazineListAdapter;
import com.longyuan.qm.bean.MagazineClassifyBean;
import com.longyuan.qm.bean.MagazineListBean;
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
public class MagazineClassifyFragment extends BaseFragment implements
        OnScrollListener {
    private static String LISTDATA_URL = ConstantsAmount.BASEURL
            + "GetMagazineList.ashx?";
    private static String GETMAGAZINEBYCATEGORY_URL = null;
    private PullToRefreshGridView mGridView;
    private GridView gridView;
    private List<MagazineListBean> magazineLists = null;
    private ArrayList<MagazineClassifyBean> classifyList = null;
    private ArrayList<MagazineClassifyBean> getMoreList = new ArrayList<MagazineClassifyBean>();
    private MagazineDetailFragment magazineDetailFragment = null;
    private int position = 0;
    private int itemCount = 0;
    private ClassifyCallBack mCallBack;
    private MyMagazineListAdapter adapter;

    /**
     * Fragment中，注册 接收MainActivity的Touch回调的对象
     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
     */
//    private HomeActivity.MyTouchListener mTouchListener = new HomeActivity.MyTouchListener() {
//
//        @Override
//        public boolean onTouchEvent(MotionEvent event) {
//            try {
//                if (mCallBack != null) {
//                    mCallBack.onCallBack(event);
//                } else {
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
    // 分页+默认值
    private int pageindex = 1;
    private int pagesize = 1000;
    private String magazinetype = "2", categoryCode = null, categoryName = null;

    public MagazineClassifyFragment(int p) {
        this.position = p;
        // 在该Fragment的构造函数中注册mTouchListener的回调
//        ((HomeActivity) getActivity()).registerMyTouchListener(mTouchListener);
    }

    public MagazineClassifyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Utils.getHttpRequestHeader();
        GETMAGAZINEBYCATEGORY_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/GetMagazineByCategory?";

        View mView = LayoutInflater.from(getActivity()).inflate(
                R.layout.magazineshop_classify_activity_normal, null);
        mContext = getActivity();
        mGridView = (PullToRefreshGridView) mView
                .findViewById(R.id.magazine_Classify_GridView);

        gridView = mGridView.getRefreshableView();
        mGridView.setOnRefreshListener(new OnRefreshListener<GridView>() {
            @Override
            public void onRefresh(PullToRefreshBase<GridView> refreshView) {
                pageindex = 1;
                getMagazineListData(pageindex, pagesize);
                mGridView.setRefreshing();
            }
        });

        mGridView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if(itemCount > pagesize * pageindex) {
                    pageindex++;
                    getMagazineListData(pageindex, pagesize);
                }
            }
        });

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @SuppressWarnings("unchecked")
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                Intent intent = new Intent(mContext, MagazineDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mMagazineClassifyList", classifyList);
                bundle.putInt("position", arg2);
                bundle.putString("categoryName", categoryName);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
            }
        });
        init();
        return mView;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void init() {
        super.init();
        Bundle bundle = getArguments();
        magazineLists = (List<MagazineListBean>) bundle.getParcelableArrayList(
                "list").get(0);
        categoryCode = magazineLists.get(position).getCategoryCode();
        categoryName = magazineLists.get(position).getCategoryName();
        getMagazineListData(pageindex, pagesize);
    }

    private List<MagazineClassifyBean> getMagazineListData(final int pageIndex,
                                                           final int pageSize) {

		/*String path = LISTDATA_URL + "authToken=" + LyApplication.authToken
                + "&categoryid="
				+ magazineLists.get(position).getCategoryCode() + "&Pageindex="
				+ pageindex + "&pagesize=" + pagesize + "&orderby=ordernumber";*/

        String path = GETMAGAZINEBYCATEGORY_URL + "apptoken=" + Utils.createAppToken() + "&ticket="
                + ConstantsAmount.TICKET + "&categorycode=" + categoryCode + "&magazinetype="
                + magazinetype + "&pagesize=" + pageSize + "&pageindex=" + pageIndex + "&itemcount="
                + itemCount;

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
                try {
                    mGridView.onRefreshComplete();
                    classifyList = new ArrayList<MagazineClassifyBean>();
                    MagazineClassifyBean classifyBean = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = jsonObject.getJSONArray("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            classifyBean = new MagazineClassifyBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            classifyBean.setMagazineGUID(jo.optString("MagazineGuid"));
                            classifyBean.setMagazineName(jo.optString("MagazineName"));
                            classifyBean.setYear(jo.optString("Year"));
                            classifyBean.setIssue(jo.optString("Issue"));
                            JSONArray ja_Magtype = jo.getJSONArray("MagazineTypes");
                            for (int j = 0; j < ja_Magtype.length(); j++) {
                                JSONObject jo_magtype = (JSONObject) ja_Magtype.get(j);
                                classifyBean.setMagzineType(jo_magtype.optString("MagzineType"));
                            }
                            JSONArray ja_Covers = jo.getJSONArray("CoverImages");
                            for (int k = 0; k < ja_Covers.length(); k++) {
                                if (ja_Covers.getString(k).endsWith("-l.jpg")) {
                                    classifyBean.setCoverPicList(ja_Covers.getString(k));
                                    break;
                                }
                            }
                            itemCount = Integer.parseInt(jsonObject.getString("ItemCount"));

                            classifyList.add(classifyBean);
                        }
//                        adapter = new MyMagazineListAdaper(mContext,
//                                classifyList, mGridView);
//                        mGridView.setAdapter(adapter);

                        if(pageIndex == 1) {
                            getMoreList = classifyList;
                            adapter = new MyMagazineListAdapter(mContext,
                                    getMoreList, mGridView);
                            mGridView.setAdapter(adapter);
                        } else {
                            getMoreList.addAll(classifyList);
//                            adapter = new MyBookListAdaper(mContext,
//                                    getMoreList, mGridView);
//                            mGridView.setAdapter(adapter);
                            adapter.setListData(getMoreList);
                            adapter.notifyDataSetChanged();
//                            mGridView.scrollTo(0, pageSize * pageIndex / 3);
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
        return classifyList;
    }

    public void setCallBack(ClassifyCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

    }

    public interface ClassifyCallBack {
        public void onCallBack(MotionEvent ev);
    }
}
