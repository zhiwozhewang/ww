package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyMagazineDirectoryAdapter;
import com.longyuan.qm.bean.ChildListData;
import com.longyuan.qm.bean.GroupListData;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MagazineDirectoryActivity extends BaseActivity {
//    private static  String LISTURL = ConstantsAmount.BASEURL
//            + "GetMagazineCatalog.ashx?";
    private static  String MAGDIRECTORY_URL = null;
    private TextView headtitle, headnumber;
    private ImageView mCloseImg;
    private ExpandableListView expandableListView;
    private String name, year, issue, mag_guid, Column;
    private List<GroupListData> groupList;
    private List<GroupListData> groupListFinal = null;
    private List<ChildListData> childList;
    private MyMagazineDirectoryAdapter adapter = null;
    public static MagazineDirectoryActivity instance_mda = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance_mda = this;
        setContentView(R.layout.magazinedetail_directory_fragment);

        Utils.getHttpRequestHeader();

        MAGDIRECTORY_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/article/catalog?";

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 强制竖屏
        getData();
        headtitle = (TextView) findViewById(R.id.mag_directory_headtitle);
        headnumber = (TextView) findViewById(R.id.mag_directory_headnumber);
        expandableListView = (ExpandableListView) findViewById(R.id.mag_directory_listview);
        mCloseImg = (ImageView) findViewById(R.id.mag_directory_headcloseimg);
//        String path = LISTURL + "authtoken=" + LyApplication.authToken
//                + "&magazineguid=" + mag_guid + "&year=" + year + "&issue="
//                + issue;

        String path = MAGDIRECTORY_URL + "apptoken=" + Utils.createAppToken() + "&ticket="
                + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid + "&year=" + year + "&issue=" + issue;
        LoadingDialog.showDialog(mContext, "正在加载...");

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                LoadingDialog.dissmissDialog();
                try {
                    LoadingDialog.dissmissDialog();
                    groupListFinal = getListData(objectResponseInfo.result);

                    adapter = new MyMagazineDirectoryAdapter(
                            MagazineDirectoryActivity.this, groupListFinal);
                    expandableListView.setAdapter(adapter);
                    headtitle.setText(name);
                    headnumber.setText(year + "年第" + issue + "期");
                    mCloseImg
                            .setBackgroundResource(R.drawable.mag_directory_closebtn);
                    // groupList默认展开的方法：
                    int groupCount = expandableListView.getCount();
                    for (int i = 0; i < groupCount; i++) {
                        expandableListView.expandGroup(i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }
        });


        mCloseImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListView.setGroupIndicator(null);// 去掉向下的箭头;

        // 覆盖groupList的点击事件;
        expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TextView textView = (TextView) v.findViewById(R.id.child_tv);
                // textView.setTextColor(Color.RED);
                if (!isInternet()) {
                    Toast.makeText(MagazineDirectoryActivity.this,
                            "当前网络不可用，情检测网络!", Toast.LENGTH_LONG).show();
                    return false;
                }
                Intent it = new Intent(MagazineDirectoryActivity.this,
                        MagazineReaderActivity.class);

                it.putExtra("title_id", groupListFinal.get(groupPosition)
                        .getList().get(childPosition).getTitleID());

                it.putExtra("RESTYPE", 1);

                DataBase.getInstance(MagazineDirectoryActivity.this)
                        .updateToMagDirectoryArticleList(
                                "1",
                                groupListFinal.get(groupPosition).getList()
                                        .get(childPosition).getTitleID()
                        );

                List<String> selectFromMagDirectoryByType = DataBase
                        .getInstance(MagazineDirectoryActivity.this)
                        .selectFromMagDirectoryByType();

                adapter.setgList(groupListFinal);
                adapter.setChildIsReadList(selectFromMagDirectoryByType);
                adapter.notifyDataSetChanged();
                startActivity(it);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (groupListFinal != null) {
            List<String> selectFromMagDirectoryByType = DataBase.getInstance(
                    MagazineDirectoryActivity.this)
                    .selectFromMagDirectoryByType();
            adapter.setgList(groupListFinal);
            adapter.setChildIsReadList(selectFromMagDirectoryByType);
            adapter.notifyDataSetChanged();
        }
    }

    private List<GroupListData> getListData(String json) throws JSONException {
        groupList = new ArrayList<GroupListData>();
        childList = new ArrayList<ChildListData>();

        GroupListData groupListData = null;
        ChildListData childListData = null;

        JSONObject jsonObject = new JSONObject(json);
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONArray ja = (JSONArray) jsonObject.get("Data");
            for (int i = 0; i < ja.length(); i++) {
                groupListData = new GroupListData();
                JSONObject jo = (JSONObject) ja.get(i);
                String column = jo.optString("Column");
                groupListData.setColumn(column);
                JSONArray ja_Articles = (JSONArray) jo.get("Articles");
                for (int j = 0; j < ja_Articles.length(); j++) {
                    childListData = new ChildListData();
                    JSONObject jo_Articles = (JSONObject) ja_Articles.get(j);
                    childListData.setColumn(column);
                    childListData.setTitleID(jo_Articles.optString("ArticleID"));
                    childListData.setTitle(jo_Articles.optString("Title"));
                    childListData.setAuthor(jo_Articles.optString("Author"));
                    childListData.setArticleImgList(jo_Articles.optString("FirstImg"));
                    childListData.setYear(year);
                    childListData.setIssue(issue);
                    childListData.setIntroduction("");
                    childListData.setCategoryCode("50");
                    childListData.setPubStartDate("");
                    childListData.setArticleImgWidth("");
                    childListData.setArticleImgHeight("");
                    childListData.setMagazineLogo("");
                    childListData.setMagazineName(name);
                    childList.add(childListData);
                }
                groupList.add(groupListData);
//            Log.e("getListData:groupList", "//" + groupList.size());
//            Log.e("getListData:childList", "//" + childList.size());
            }

            // 去重复，将子集合添加到父集合中！
        /*for (int j = 0; j < groupList.size(); j++) {
            for (int k = groupList.size() - 1; k > j; k--) {
                if (groupList.get(j).getColumn()
                        .equals(groupList.get(k).getColumn())) {
                    groupList.remove(j);
                }
            }
        }*/

            for (int m = 0; m < groupList.size(); m++) {
                List<ChildListData> addlist = new ArrayList<ChildListData>();
                for (int n = 0; n < childList.size(); n++) {
                    if (childList.get(n).getColumn()
                            .equals(groupList.get(m).getColumn())) {
                        addlist.add(childList.get(n));
                    }
                }
                groupList.get(m).setList(addlist);
            }

            DataBase.getInstance(MagazineDirectoryActivity.this)
                    .addMagDirectoryMagazineList(groupList);

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
                            finish();

                            HomeActivity.instance_home.finish();

                        }
                    }).setCancelable(false).create();
            alertDialog.show();
        } else {
            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                    Toast.LENGTH_LONG).show();
        }
            // 目录接口数据存入数据库（区分已读和未读）

            return groupList;



    /*private List<GroupListData> getListData(String json) throws JSONException {
        groupList = new ArrayList<GroupListData>();
        childList = new ArrayList<ChildListData>();

        GroupListData groupListData = null;
        ChildListData childListData = null;

        JSONObject jsonObject = new JSONObject(json);
        name = jsonObject.optString("MagazineName");
        String Year = jsonObject.optString("Year");
        String Issue = jsonObject.optString("Issue");
        JSONArray jsonArray = jsonObject.getJSONArray("Articles");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jo = jsonArray.getJSONObject(i);
            String TitleID = jo.optString("TitleID");
            Log.e("directory:titleid", TitleID);
            String Author = jo.optString("Author");
            String Title = jo.optString("Title");
            String Introduction = jo.optString("Introduction");
            Column = jo.optString("Column");
            if (Column.equals("") || Column == null) {
                Log.e("column", "=null");
                Column = "其他";
            }

            String CategoryCode = jsonObject.optString("CategoryCode");
            String ArticleImgList = jsonObject.optString("ArticleImgList");
            String PubStartDate = jsonObject.optString("PubStartDate");
            String ArticleImgWidth = jsonObject.optString("ArticleImgWidth");
            String ArticleImgHeight = jsonObject.optString("ArticleImgHeight");
            String MagazineLogo = jsonObject.optString("MagazineLogo");

            groupListData = new GroupListData();
            childListData = new ChildListData();

            childListData.setIntroduction(Introduction);
            childListData.setTitle(Title);
            childListData.setTitleID(TitleID);
            childListData.setColumn(Column);
            childListData.setAuthor(Author);
            childListData.setMagazineName(name);
            childListData.setYear(Year);
            childListData.setIssue(Issue);
            childListData.setCategoryCode(CategoryCode);
            childListData.setArticleImgList(ArticleImgList);
            childListData.setPubStartDate(PubStartDate);
            childListData.setArticleImgWidth(ArticleImgWidth);
            childListData.setArticleImgHeight(ArticleImgHeight);
            childListData.setMagazineLogo(MagazineLogo);

            childList.add(childListData);

            groupListData.setColumn(Column);
            groupList.add(groupListData);

        }

        // 去重复，将子集合添加到父集合中！
        for (int j = 0; j < groupList.size(); j++) {
            for (int k = groupList.size() - 1; k > j; k--) {
                if (groupList.get(j).getColumn()
                        .equals(groupList.get(k).getColumn())) {
                    groupList.remove(j);
                }
            }
        }

        for (int m = 0; m < groupList.size(); m++) {
            List<ChildListData> addlist = new ArrayList<ChildListData>();
            for (int n = 0; n < childList.size(); n++) {
                if (childList.get(n).getColumn()
                        .equals(groupList.get(m).getColumn())) {
                    addlist.add(childList.get(n));
                }
            }
            groupList.get(m).setList(addlist);
        }

        // 目录接口数据存入数据库（区分已读和未读）
        DataBase.getInstance(MagazineDirectoryActivity.this)
                .addMagDirectoryMagazineList(groupList);
        return groupList;
    }*/
    }

    private void getData() {
        Intent intent = this.getIntent();
        issue = intent.getStringExtra("issus");
        year = intent.getStringExtra("year");
        mag_guid = intent.getStringExtra("mag_guid");
        name = intent.getStringExtra("mag_name");
    }
}
