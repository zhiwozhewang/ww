package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
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
import com.longyuan.qm.activity.MagazineDetailActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyMagazineAttentionAdapter;
import com.longyuan.qm.bean.MagazineAttentionBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MagazineAttentionFragment extends BaseFragment {

    // 查看杂志关注列表
//    private static final String SELECTMAGFAVORITEURL = ConstantsAmount.BASEURL
//            + "GetMagazineFavoriteList.ashx?";
    // 删除杂志关注的URL
//    private static final String DELETEMAGFAVORITEURL = ConstantsAmount.BASEURL
//            + "UserMagazineFavoriteDel.ashx?";

    private static String SELECTMAGFAVORITEURL = null;
    private static String DELETEMAGFAVORITEURL = null;
    private GridView mGridView;
    private List<MagazineAttentionBean> list = new ArrayList<MagazineAttentionBean>();
    private MyMagazineAttentionAdapter adapter = null;
    private String userName = null;
    private MagazineDetailFragment detailFragment;
    private SharedPreferences preferences = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Utils.getHttpRequestHeader();
        SELECTMAGFAVORITEURL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/concern/GetList?";
        DELETEMAGFAVORITEURL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "user/concern/remove?";

        View mView = inflater.inflate(R.layout.magazine_attention_normal, null);
        preferences = getActivity().getSharedPreferences("net",
                Context.MODE_PRIVATE);
        userName = preferences.getString("NAME", "");
        mGridView = (GridView) mView.findViewById(R.id.Mag_Att_Shelf);

        if (isInternet()) {
            LoadingDialog.showDialog(mContext, "正在加载...");
            selectMagAttList();
        } else {
            Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION,
                    Toast.LENGTH_LONG).show();
        }

        mGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (isInternet()) {
                    String mag_guid = list.get(arg2).getMag_guid();
                    String mag_categoryName = list.get(arg2)
                            .getMag_categoryName();
                    FragmentTransaction t = getActivity()
                            .getSupportFragmentManager().beginTransaction();
                    if (detailFragment == null) {
//                        detailFragment = new MagazineDetailFragment();
//                        Bundle bundle = new Bundle();
//                        bundle.putString("mag_guid", mag_guid);
//                        bundle.putString("mag_categoryName", mag_categoryName);
//                        detailFragment.setArguments(bundle);
//                        t.replace(R.id.fl_main, detailFragment);
//                        t.commit();
                        Intent intent = new Intent(mContext, MagazineDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("mag_guid", mag_guid);
                        bundle.putString("mag_categoryName", mag_categoryName);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(getActivity(), "当前网络不可用,请检测网络!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mGridView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
                Builder builder = new Builder(getActivity());
                builder.setTitle("提示：")
                        .setMessage("确定删除吗？")
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                    }
                                }
                        )
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (isInternet()) {
                                            if (list.get(arg2) != null) {
                                                LoadingDialog.showDialog(
                                                        mContext, "正在删除...");
                                                deleteMagAtt(list.get(arg2)
                                                        .getId(), arg2);
                                            } else {
                                                Toast.makeText(getActivity(),
                                                        "数据加载失败",
                                                        Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        } else {
                                            Toast.makeText(getActivity(),
                                                    "当前网络不可用，请检测网络!",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                        ).show();
                return true;
            }
        });
        return mView;
    }

    private void selectMagAttList() {
        list.clear();
//        String path = SELECTMAGFAVORITEURL + "authToken="
//                + LyApplication.authToken + "&pagesize=1000";
        String path = SELECTMAGFAVORITEURL + "apptoken=" + Utils.createAppToken() +
                "&ticket=" + ConstantsAmount.TICKET + "&resourcekind=2" + "&pagesize=9000&pageindex=1&itemcount=0";

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(getActivity(), "同步关注列表失败!", Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    LoadingDialog.dissmissDialog();
                    MagazineAttentionBean info = null;
                    String imageListUrlString = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (jsonObject.optString("Code").equals("0")) {
                        Toast.makeText(getActivity(), "同步关注列表失败!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        list.clear();
                        if (jsonObject.getString("ItemCount").equals("0")) {
                            Toast.makeText(mContext, "当前用户没有关注任何资源", Toast.LENGTH_SHORT).show();
                            adapter = new MyMagazineAttentionAdapter(mContext, list);
                            mGridView.setAdapter(adapter);
                            return;
                        }
                        JSONArray jsonArray = jsonObject
                                .optJSONArray("Data");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            info = new MagazineAttentionBean();
                            JSONObject jo = (JSONObject) jsonArray.get(i);
                            String ID = jo.optString("ID");
                            String MagazineName = jo.optString("MagazineName");
                            String MagazineGUID = jo.optString("MagazineGuid");
                            String Year = jo.optString("Year");
                            String Issue = jo.optString("Issue");
                            JSONArray ja_CoverImages = jo.getJSONArray("CoverImages");
                            // FIXME MagazineType（JSONArray型）杂志类型；
                            for (int j = 0; j < ja_CoverImages.length(); j++) {
                                if (ja_CoverImages.getString(j).endsWith("-l.jpg")) {
                                    imageListUrlString = ja_CoverImages.getString(j);
                                    break;
                                }
                            }
                            info.setMag_name(MagazineName);
                            info.setMag_guid(MagazineGUID);
                            info.setMag_cover(imageListUrlString);
                            info.setUsername(userName);
                            info.setId(ID);
                            list.add(info);
                        }
                        Log.e("", "" + list.size());
                    }
                    adapter = new MyMagazineAttentionAdapter(mContext, list);
                    mGridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                /*try {
                    LoadingDialog.dissmissDialog();
                    MagazineAttentionBean info = null;
                    String imageListUrlString = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (jsonObject.optString("Code").equals("0")) {
                        Toast.makeText(getActivity(), "同步关注列表失败!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        list.clear();
                        JSONArray jsonArray = jsonObject
                                .optJSONArray("MagazineList");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            info = new MagazineAttentionBean();
                            JSONObject jo = (JSONObject) jsonArray.get(i);
                            String MagazineName = jo.optString("MagazineName");
                            String MagazineGUID = jo.optString("MagazineGUID");
                            String Year = jo.optString("Year");
                            String Issue = jo.optString("Issue");
                            String CoverPicList = jo.optString("CoverPicList");

                            if (CoverPicList != null
                                    && !CoverPicList.equals("")) {
                                imageListUrlString = Utils
                                        .getImageListUrlString(CoverPicList);
                            } else {
                                info.setMag_cover(CoverPicList);
                            }
                            info.setMag_name(MagazineName);
                            info.setMag_guid(MagazineGUID);
                            info.setMag_cover(imageListUrlString);
                            info.setUsername(userName);
                            list.add(info);
                        }
                    }
                    adapter = new MyMagazineAttentionAdapter(mContext, list);
                    mGridView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
            }
        });
    }

    private void deleteMagAtt(String id, final int position) {
//        String path = DELETEMAGFAVORITEURL + "authToken="
//                + LyApplication.authToken + "&magazineguid=" + Mag_guid;
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
        httpUtils.send(HttpMethod.POST, DELETEMAGFAVORITEURL, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(getActivity(), "删除失败!", Toast.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                Log.e("deleteMagAtt : onSuccess", arg0.result);
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        String Message = jsonObject.optString("Message");
                        String Code = jsonObject.optString("Code");
                        Toast.makeText(getActivity(), "删除成功!",
                                Toast.LENGTH_LONG).show();
                        // selectMagAttList();
                        // 刷新列表
                        list.remove(position);
                        adapter = new MyMagazineAttentionAdapter(mContext, list);
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
                                        ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                                        ConstantsAmount.BASEURL_UNIT = null;
                                        LyApplication.authToken = null;
                                        startActivity(intent);
                                        HomeActivity.instance_home.finish();
                                    }
                                }).setCancelable(false).create();
                        alertDialog.show();
                    } else {
                        Toast.makeText(getActivity(), jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
               /* try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    String Message = jsonObject.optString("Message");
                    String Code = jsonObject.optString("Code");
                    if (Code.equals("1") && Message.equals("删除成功")) {
                        Toast.makeText(getActivity(), "删除成功!",
                                Toast.LENGTH_LONG).show();
                        // selectMagAttList();
                        // 刷新列表
                        list.remove(position);
                        adapter = new MyMagazineAttentionAdapter(mContext, list);
                        mGridView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), "删除失败!",
                                Toast.LENGTH_LONG).show();
                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
