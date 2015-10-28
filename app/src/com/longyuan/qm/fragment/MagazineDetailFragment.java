package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.MagazineDirectoryActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyMagazineDetailAdapter;
import com.longyuan.qm.bean.MagazineAttentionBean;
import com.longyuan.qm.bean.MagazineClassifyBean;
import com.longyuan.qm.bean.MagazineDetailListBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MagazineDetailFragment extends BaseFragment {
    // 获取杂志详情的URL
//    private static final String LISTDATA_URL = ConstantsAmount.BASEURL
//            + "getMagzineInfo.ashx?";

    // 获取期列表的URL
//    private static final String DETAIL_LISTDATA_URL = ConstantsAmount.BASEURL
//            + "GetMagazineInfoList.ashx?";
    // 获取期刊目录的URL
//    private static final String DIRECTORY_LISTDATA_URL = ConstantsAmount.BASEURL
//            + "GetMagazineCatalog.ashx?";
    // 添加关注的URL
//    private static final String ADDMAGFAVORITE_URL = ConstantsAmount.BASEURL
//            + "UserMagazineFavoriteAdd.ashx?";

//    private static String GETMAGAZINEISSUE_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/GetMagazineIssue?";
    private static String GETBASICINFO_URL = null;
    private static String GETMAGAZINEISSUES_URL = null;
    private static String GETMAGAZINEATT_URL = null;
    private static String ADDMAGFAVORITE_URL = null;
    boolean successFlag = false;
    private MagazineClassifyBean bean = null;
    private TextView mag_name, title_classify, title_qk, mag_detail, headTitle;
    private Button leftBtn, attention_btn;
    private PullToRefreshGridView mGridView;
    private List<MagazineDetailListBean> detailLists = null;
    private MagazineAttentionBean magazineAttentionBean = null;
    private MagazineFragment magazineFragment = null;
    private int position = -1;
    private int pageSize = 1000;
    private int pageIndex = 1;
    private String mag_guid, cycle, mag_Name, category, categoryName, magazinetype = "2", ConcernID = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Utils.getHttpRequestHeader();
        GETBASICINFO_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/GetBasicInfo?";
        GETMAGAZINEISSUES_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/GetMagazineIssues?";
        GETMAGAZINEATT_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/Concern/IsConcern?";
        ADDMAGFAVORITE_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/concern/add?";

        View mView = inflater.inflate(R.layout.magazinedetail_fragment_normal,
                null);
        mContext = getActivity();
        headTitle = (TextView) mView.findViewById(R.id.head_layout_text);
        mag_name = (TextView) mView.findViewById(R.id.mag_name);
        title_classify = (TextView) mView.findViewById(R.id.title_classify);
        title_qk = (TextView) mView.findViewById(R.id.title_qk);
        mag_detail = (TextView) mView.findViewById(R.id.mag_detail);
        attention_btn = (Button) mView.findViewById(R.id.attention_btn);
        leftBtn = (Button) mView.findViewById(R.id.head_layout_showLeft);
        mGridView = (PullToRefreshGridView) mView
                .findViewById(R.id.mag_detail_gridview);
        headTitle.setText("杂志详情");
        mGridView.setMode(PullToRefreshBase.Mode.DISABLED);

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(getActivity(),
                        MagazineDirectoryActivity.class);
                intent.putExtra("issus", detailLists.get(arg2).getIssue());
                intent.putExtra("year", detailLists.get(arg2).getYear());
                intent.putExtra("mag_guid", mag_guid);
                intent.putExtra("mag_name", detailLists.get(arg2).getMagazineName());
                startActivity(intent);
            }
        });

        leftBtn.setBackgroundResource(R.drawable.button_back_selector);

        leftBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction beginTransaction = getFragmentManager()
                        .beginTransaction();
                magazineFragment = new MagazineFragment();
                if (bean == null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("initPager", "att");
                    magazineFragment.setArguments(bundle);
                }
                beginTransaction.replace(R.id.fl_main, magazineFragment);
//                beginTransaction.show(magazineFragment);
                beginTransaction.commit();
            }
        });

        attention_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isInternet()) {
                    if (magazineAttentionBean != null) {
                        addMagAtt(magazineAttentionBean);
                    } else {
                        Toast.makeText(getActivity(), "数据加载失败!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "当前网络不可用，请检测网络!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        init();
        return mView;
    }

    /**
     * (非 Javadoc) Title: init Description:
     *
     * @see com.longyuan.qm.BaseFragment#init()
     */
    @Override
    protected void init() {
        super.init();
        Bundle bundle = getArguments();
        position = bundle.getInt("position");
        categoryName = bundle.getString("categoryName");

        // 杂志库中进入
        if (bundle.getParcelableArrayList("mMagazineClassifyList") != null) {
            bean = (MagazineClassifyBean) bundle.getParcelableArrayList(
                    "mMagazineClassifyList").get(position);
            mag_guid = bean.getMagazineGUID();
            category = bean.getMagazineName();
            if (isInternet()) {
                getDetailDataFromMagShop();
            } else {
                Toast.makeText(mContext,
                        ConstantsAmount.BAD_NETWORK_CONNECTION,
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // FIXME 从关注进入;
            mag_guid = bundle.getString("mag_guid");
            getDetailDataFromMagAtt();
        }
    }

    /**
     * @param @param magazineAttentionBean2
     * @return void
     * @throws
     * @Title: addMagAtt
     * @Description: 杂质添加关注(这里用一句话描述这个方法的作用)
     */
    private void addMagAtt(MagazineAttentionBean attBean) {
        LoadingDialog.showDialog(mContext, "请稍候...");
//        String path = ADDMAGFAVORITE_URL + "authToken="
//                + LyApplication.authToken + "&magazineguid="
//                + mag_guid;

//        String path = ADDMAGFAVORITE_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + mag_guid + "&resourcekind=2";

        RequestParams params = new RequestParams();
        try {
            params.setHeader("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("apptoken", Utils.createAppToken());
            obj.put("ticket", ConstantsAmount.TICKET);
            obj.put("resourceid", mag_guid);
            obj.put("resourcekind", "2");
            params.setBodyEntity(new StringEntity(obj.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.POST, ADDMAGFAVORITE_URL, params, new RequestCallBack<String>() {


            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    LoadingDialog.dissmissDialog();
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    jsonObject.optString("Message");
                    String Code = jsonObject.optString("Code");
                    if (Code.equals("1")) {
                        Toast.makeText(mContext, "关注成功!", Toast.LENGTH_LONG)
                                .show();
                        attention_btn.setText("已关注");
                        attention_btn.setClickable(false);
                    } else {
                        Toast.makeText(mContext, "关注失败!", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getDetailDataFromMagShop
     * @Description: 获取杂志详情页面信息（从杂志库入口进入）(这里用一句话描述这个方法的作用)
     */

    private void getDetailDataFromMagShop() {
        // 从获取杂志目录的借口中获取刊期字段；
//        String directoryPath = DIRECTORY_LISTDATA_URL + "authtoken="
//                + LyApplication.authToken + "&magazineguid=" + mag_guid;
//        String path = GETMAGAZINEISSUE_URL + "apptoken=" + ConstantsAmount.APPTOKEN +
//                "&ticket=" + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid +
//                "&year=" + bean.getYear() + "&issue=" + bean.getIssue();


        String path = GETBASICINFO_URL + "apptoken=" + Utils.createAppToken() +
                "&ticket=" + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid;
        LoadingDialog.showDialog(mContext, "正在加载...");

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        LoadingDialog.dissmissDialog();
                        Toast.makeText(mContext,
                                ConstantsAmount.REQUEST_ONFAILURE,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        LoadingDialog.dissmissDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                JSONObject jo = jsonObject.getJSONObject("Data");
                                mag_name.setText(jo.optString("MagazineName"));
                                jo.optString("ISSN");
                                title_qk.setText(Utils.MagazineCycleParser(jo.optString("Cycle")));
                                mag_detail.setText(Utils.replaceHtmlTag(jo.optString("Note")));
                                title_classify.setText(categoryName);
                                getAttentionData();
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
                                Toast.makeText(mContext,
                                        jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
//                        try {
//                            JSONObject jsonObject = new JSONObject(arg0.result);
//                            if (checkRequestCode(jsonObject).equals("1")) {
//                                JSONObject jo = (JSONObject) jsonObject.get("Data");
//                                jo.optString("MagazineGuid");
//                                mag_name.setText(jo.optString("MagazineName"));
//                                jo.optString("Year");
//                                jo.optString("Issue");
//                                jo.optString("ISSN");
//                                title_qk.setText(Utils.MagazineCycleParser(jo.optString("Cycle")));
//                                mag_detail.setText(jo.optString("Note"));
//                                title_classify.setText(categoryName);
//
//                                JSONArray ja_MagazineTypes = jo.getJSONArray("MagazineTypes");
//                                for (int j = 0; j < ja_MagazineTypes.length(); j++) {
//                                    JSONObject jo_MagazineTypes = (JSONObject) ja_MagazineTypes.get(j);
//                                    jo_MagazineTypes.optString("MagzineType");
//                                    jo_MagazineTypes.optString("PageCount");
//                                    jo_MagazineTypes.optString("FileSize");
//                                }
//                                JSONArray ja_CoverImages = jo.getJSONArray("CoverImages");
//                                for (int k = 0; k < ja_CoverImages.length(); k++) {
//                                    if (ja_CoverImages.getString(k).endsWith("-l.jpg")) {
//                                        ja_CoverImages.getString(k);
//                                        break;
//                                    }
//                                }
////                                    JSONArray ja_Extensions = jo.getJSONArray("Extensions");
//                            } else {
//                                Toast.makeText(mContext,
//                                        jsonMessageParser(jsonObject),
//                                        Toast.LENGTH_LONG).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                        /*try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                cycle = jsonObject.optString("Cycle");
                                mag_Name = jsonObject.optString("MagazineName");
                                category = jsonObject.optString("Category");
                                IsFavorite = jsonObject.optString("IsFavorite");
                                title_qk.setText(cycle);
                                mag_name.setText(mag_Name);
                                title_classify.setText(category);
                                mag_detail.setText(bean.getIntroduction());
                                getAttentionData();
                            } else {
                                Toast.makeText(mContext,
                                        jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
        );
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getDetailDataFromMagAtt
     * @Description: 获取杂志详情页面信息(从关注入口进入)(这里用一句话描述这个方法的作用)
     */
    /*private void getDetailDataFromMagAtt() {
        // 从获取杂志目录的借口中获取刊期字段；
        String directoryPath = DIRECTORY_LISTDATA_URL + "authtoken="
                + LyApplication.authToken + "&magazineguid=" + mag_guid;

        LoadingDialog.showDialog(mContext, "正在加载...");
        magazineAttentionBean = new MagazineAttentionBean();
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpMethod.GET, directoryPath,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        LoadingDialog.dissmissDialog();
                        Toast.makeText(mContext,
                                ConstantsAmount.REQUEST_ONFAILURE,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                cycle = jsonObject.optString("Cycle");
                                mag_Name = jsonObject.optString("MagazineName");
                                category = jsonObject.optString("Category");
                                IsFavorite = jsonObject.optString("IsFavorite");

                                title_qk.setText(cycle);
                                mag_name.setText(mag_Name);
                                title_classify.setText(category);
                                getAttentionData();
                            } else {
                                Toast.makeText(mContext,
                                        jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }*/

    /**
     * @param
     * @return void
     * @throws
     * @Title: getDetailDataFromMagAtt
     * @Description: 获取杂志详情页面信息(从关注入口进入)(这里用一句话描述这个方法的作用)
     */
    private void getDetailDataFromMagAtt() {
        // 从获取杂志目录的借口中获取刊期字段；
//        String directoryPath = DIRECTORY_LISTDATA_URL + "authtoken="
//                + LyApplication.authToken + "&magazineguid=" + mag_guid;

        String path = GETBASICINFO_URL + "apptoken=" + Utils.createAppToken() +
                "&ticket=" + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid;

        LoadingDialog.showDialog(mContext, "正在加载...");
        magazineAttentionBean = new MagazineAttentionBean();
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        LoadingDialog.dissmissDialog();
                        Toast.makeText(mContext,
                                ConstantsAmount.REQUEST_ONFAILURE,
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        LoadingDialog.dissmissDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(arg0.result);
                            if (checkRequestCode(jsonObject).equals("1")) {
                                JSONObject jo = jsonObject.getJSONObject("Data");
                                mag_name.setText(jo.optString("MagazineName"));
                                jo.optString("ISSN");
                                title_qk.setText(Utils.MagazineCycleParser(jo.optString("Cycle")));
                                mag_detail.setText(Utils.replaceHtmlTag(jo.optString("Note")));
                                title_classify.setText(categoryName);
                                getAttentionData();
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
                                Toast.makeText(mContext,
                                        jsonMessageParser(jsonObject),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

//    /**
//     * @param
//     * @return void
//     * @throws
//     * @Title: getGridViewDataFromJson
//     * @Description: 获取杂志期刊列表(这里用一句话描述这个方法的作用)
//     */
//    private void getGridViewDataList(String mag_guid, int pagesize,
//                                     int pageindex) {
//        LoadingDialog.showDialog(mContext, "加载中...");
////        String detailListURL = DETAIL_LISTDATA_URL + "authtoken="
////                + LyApplication.authToken + "&magazineguid=" + mag_guid
////                + "&pagesize=" + pagesize + "&pageindex=" + pageindex;
//        String path = GETMAGAZINEISSUES_URL + "apptoken=" + ConstantsAmount.APPTOKEN + "&ticket="
//                + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid + "&magazinetype=" + magazinetype;
//
//        detailLists = new ArrayList<MagazineDetailListBean>();
//        HttpUtils httpUtils = new HttpUtils();
//        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
//
//                    @Override
//                    public void onFailure(HttpException arg0, String arg1) {
//                        Toast.makeText(getActivity(), "加载失败",
//                                Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<String> arg0) {
//                        try {
//                            LoadingDialog.dissmissDialog();
////                            detailLists = getGridViewDataFromJson(arg0.result);
//                            List<MagazineDetailListBean> list = new ArrayList<MagazineDetailListBean>();
//                            String imageListUrlString = null;
//                            JSONObject jsonObject = new JSONObject(arg0.result);
//                            if(checkRequestCode(jsonObject).equals("1")) {
//
//                            } else {
//                                Toast.makeText(mContext, jsonMessageParser(jsonObject),
//                                        Toast.LENGTH_LONG).show();
//                            }
//                            MyMagazineDetailAdapter adapter = new MyMagazineDetailAdapter(
//                                    detailLists, getActivity());
//                            mGridView.setAdapter(adapter);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//        );
//    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getGridViewDataFromJson
     * @Description: 获取杂志期刊列表(这里用一句话描述这个方法的作用)
     */
    private void getGridViewDataList(String mag_guid) {
        LoadingDialog.showDialog(mContext, "加载中...");
//        String detailListURL = DETAIL_LISTDATA_URL + "authtoken="
//                + LyApplication.authToken + "&magazineguid=" + mag_guid
//                + "&pagesize=" + pagesize + "&pageindex=" + pageindex;
        String path = GETMAGAZINEISSUES_URL + "apptoken=" + Utils.createAppToken() + "&ticket="
                + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid + "&magazinetype=" + magazinetype;

        detailLists = new ArrayList<MagazineDetailListBean>();
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(getActivity(), "加载失败",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        LoadingDialog.dissmissDialog();

                        try {
                            detailLists = new ArrayList<MagazineDetailListBean>();
                            MagazineDetailListBean detailList = null;
                            Log.e("MagazineDetailFragment : result", "" + arg0.result);
                            JSONObject jsonObject = new JSONObject(arg0.result);

                            if (checkRequestCode(jsonObject).equals("1")) {
                                JSONArray ja = jsonObject.getJSONArray("Data");
                                for (int i = 0; i < ja.length(); i++) {
                                    detailList = new MagazineDetailListBean();
                                    JSONObject jo = (JSONObject) ja.get(i);
                                    detailList.setMagazineId(jo.optString("MagazineGuid"));
                                    detailList.setMagazineName(jo.optString("MagazineName"));
                                    detailList.setYear(jo.optString("Year"));
                                    detailList.setIssue(jo.optString("Issue"));

                                    if(!jo.optString("MagazineType").equals("null")) {
                                    JSONArray ja_MagazineType = jo.getJSONArray("MagazineType");
                                        for (int j = 0; j < ja_MagazineType.length(); j++) {
                                            JSONObject jo_MagzineType = (JSONObject) ja_MagazineType.get(j);
                                            detailList.setMagzineType(jo_MagzineType.optString("MagazineType"));
                                        }
                                    }

                                    JSONArray ja_CoverImages = jo.getJSONArray("CoverImages");
                                    for (int k = 0; k < ja_CoverImages.length(); k++) {
                                        if (ja_CoverImages.getString(k).endsWith("-l.jpg")) {
                                            detailList.setCover(ja_CoverImages.getString(k));
//                                            break;
                                        }
                                    }
                                    detailLists.add(detailList);
                                }
                                MyMagazineDetailAdapter adapter = new MyMagazineDetailAdapter(
                                        detailLists, getActivity());
                                mGridView.setAdapter(adapter);
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
                    }
                }
        );
    }

    private List<MagazineDetailListBean> getGridViewDataFromJson(String json)
            throws JSONException {
        List<MagazineDetailListBean> list = new ArrayList<MagazineDetailListBean>();
        JSONObject jsonObject = new JSONObject(json);
        MagazineDetailListBean detailList = null;
        String imageListUrlString = null;
        JSONArray jsonArray = jsonObject.getJSONArray("MagazineList");
        for (int i = 0; i < jsonArray.length(); i++) {
            detailList = new MagazineDetailListBean();
            JSONObject jo = jsonArray.getJSONObject(i);
            String MagazineName = jo.optString("MagazineName");
            String Year = jo.optString("Year");
            String Issue = jo.optString("Issue");
            String CoverPicList = jo.optString("CoverPicList");

            if (CoverPicList != null && !CoverPicList.equals("")) {
                imageListUrlString = Utils.getImageListUrlString(CoverPicList);
            } else {
                detailList.setCover(CoverPicList);
            }

            detailList.setMagazineName(MagazineName);
            detailList.setYear(Year);
            detailList.setIssue(Issue);
            detailList.setCover(imageListUrlString);
            detailList.setMagazineId(mag_guid);
            list.add(detailList);
        }
        return list;
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getAttentionData
     * @Description: 获取关注杂志数据(这里用一句话描述这个方法的作用)
     */
    private void getAttentionData() {
//        String path = LISTDATA_URL + "authtoken=" + LyApplication.authToken
//                + "&magazineguid=" + mag_guid;
        String path = GETMAGAZINEATT_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&resourceid=" + mag_guid;
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
                try {
//                    getAttentionDataFromJson(arg0.result);
                    magazineAttentionBean = new MagazineAttentionBean();
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONObject jo = jsonObject.getJSONObject("Data");
                        ConcernID = jo.optString("ConcernID");
                        if (jo.optString("IsConcern").equals("true")) {
                            attention_btn.setText("已关注");
                            attention_btn.setClickable(false);
                        } else {
                            attention_btn.setText("关注");
                            attention_btn.setClickable(true);
                        }
                        getGridViewDataList(mag_guid);
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
                                        ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                        ConstantsAmount.MENUPOSITION = 0;
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

    /*private MagazineAttentionBean getAttentionDataFromJson(String json)
            throws JSONException {
        LoadingDialog.dissmissDialog();
        magazineAttentionBean = new MagazineAttentionBean();
        JSONObject jsonObject = new JSONObject(json);
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONObject jo = (JSONObject) jsonObject.opt("Magazines");
            mag_detail.setText(Utils.replaceHtmlTag(jo.optString("Summary")));
            if (jo.optString("IsFavorite").equals("true")) {
                attention_btn.setText("已关注");
                attention_btn.setClickable(false);
            } else {
                attention_btn.setText("关注");
                attention_btn.setClickable(true);
            }
            // 获取杂志期刊列表;
            getGridViewDataList(mag_guid, pageSize, pageIndex);
        } else {
            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                    Toast.LENGTH_LONG).show();
        }
        return magazineAttentionBean;
    }*/
}
