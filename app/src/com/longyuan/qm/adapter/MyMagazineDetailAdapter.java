package com.longyuan.qm.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.ChildListData;
import com.longyuan.qm.bean.GroupListData;
import com.longyuan.qm.bean.MagazineDetailListBean;
import com.longyuan.qm.bean.MagazineReaderBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MyMagazineDetailAdapter extends BaseAdapter {
    private static final String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savaPicPath = SDPATH + "/LYyouyue/";
    //    private static final String LISTURL = ConstantsAmount.BASEURL
//            + "GetMagazineCatalog.ashx?";
    private static String LISTREADERURL = ConstantsAmount.BASEURL
            + "getMagazineArticle.ashx?";

    private static String MAGDIRECTORY_URL = null;
    private static String GETDETAIL_URL = null;

    private List<MagazineDetailListBean> mList;
    private Context mContext;
    private ViewHolder holder;
    private MagazineDetailListBean detailList = null;
    private ProgressDialog proDialog;
    private List<GroupListData> groupList;
    private List<ChildListData> childList;
    private List<Integer> isAddedPosition = new ArrayList<Integer>();
    private boolean isShow = true;
    private int position_add = -1;
    private SharedPreferences preferences = null;
    private String userName = null, year, issue, mName;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    String titleid = "";
                    List<GroupListData> listData;
                    try {
                        listData = getDriectoryListData((String) msg.obj, position_add);
                        if(listData == null) {
                            LoadingDialog.dissmissDialog();
                            return;
                        }
                        for (int i = 0; i < listData.size(); i++) {
                            for (int j = 0; j < listData.get(i).getList().size(); j++) {
                                titleid = listData.get(i).getList().get(j)
                                        .getTitleID();
//                                Log.e("//", titleid);
                                getOfflineReaderList(titleid);

                                /*if (i == listData.size() - 1
                                        && j == listData.get(listData.size() - 1)
                                        .getList().size() - 1) {
                                    Log.e("最后一条", "的后面一条");
                                    String titleID2 = listData.get(i).getList()
                                            .get(j).getTitleID();

                                    int length = titleID2.length();
                                    String subTitleidBefore = (String) titleID2
                                            .subSequence(0, length - 2);

                                    String subTitleidBy2 = (String) titleID2
                                            .subSequence(length - 2, length);

                                    Integer subLast2 = Integer
                                            .parseInt(subTitleidBy2);
                                    subLast2 = subLast2 + 1;
                                    String finalTitleid = subTitleidBefore
                                            + subLast2;
                                    getOfflineReaderList(finalTitleid);
                                }*/
                            }
                        }
                        DataBase.getInstance(mContext).addOfflineMagazineList(
                                detailList, userName);
                        notifyDataSetChanged();
                        LoadingDialog.dissmissDialog();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    public MyMagazineDetailAdapter(List<MagazineDetailListBean> list,
                                   Context context) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            preferences = mContext.getSharedPreferences("net",
                    Context.MODE_PRIVATE);
            userName = preferences.getString("NAME", "");
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.magazinedetail_gridview_item, null);
            holder.magdetail_item_img = (ImageView) convertView
                    .findViewById(R.id.magdetail_gridview_item_img);
            holder.magdetail_item_magissue = (TextView) convertView
                    .findViewById(R.id.magdetail_gridview_item_magissue);
            holder.addbtn = (Button) convertView
                    .findViewById(R.id.magdetail_gridview_item_addbtn);
            convertView.setTag(holder);
            Utils.getHttpRequestHeader();
            MAGDIRECTORY_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/article/catalog?";
            GETDETAIL_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/article/GetDetail?";
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        year = mList.get(position).getYear();
        issue = mList.get(position).getIssue();
        mName = mList.get(position).getMagazineName();
        holder.magdetail_item_magissue.setText(mList.get(position).getYear()
                + "年" + mList.get(position).getIssue() + "期");
//        Log.e("adapter:addbtn", "" + mList.get(position).getIssue());

        BitmapUtils bitmapUtil = new BitmapUtils(mContext);
        bitmapUtil.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
        bitmapUtil
                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
        bitmapUtil.display(holder.magdetail_item_img, mList.get(position)
                .getCover());

        List<MagazineDetailListBean> selectFromofflineList = DataBase
                .getInstance(mContext).selectFromofflineList(userName);
        holder.addbtn.setText("添加杂志架");
        for (int i = 0; i < selectFromofflineList.size(); i++) {
            String uniquenessIsreadMagListItem = selectFromofflineList.get(i)
                    .getMagazineName()
                    + selectFromofflineList.get(i).getYear()
                    + selectFromofflineList.get(i).getIssue();
            String MagListItem = mList.get(position).getMagazineName()
                    + mList.get(position).getYear()
                    + mList.get(position).getIssue();
            if (uniquenessIsreadMagListItem.equals(MagListItem)) {
                holder.addbtn.setText("已添加");
                isAddedPosition.add(position);
            }
        }

        holder.addbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                issue = mList.get(position).getIssue();
                year = mList.get(position).getYear();
                mName = mList.get(position).getMagazineName();
                if (isAddedPosition.size() == 0) {
                    LoadingDialog.showDialog(mContext, "下载中...");
                    saveOfflineDirectoryData(mList.get(position)
                                    .getMagazineName(), mList.get(position)
                                    .getMagazineId(), mList.get(position).getYear(),
                            mList.get(position).getIssue()
                    );
                    detailList = new MagazineDetailListBean();
                    detailList.setMagazineId(mList.get(position)
                            .getMagazineId());
                    detailList.setMagazineName(mList.get(position)
                            .getMagazineName());
                    detailList.setCover(mList.get(position).getCover());
                    detailList.setIssue(mList.get(position).getIssue());
                    detailList.setYear(mList.get(position).getYear());

                    //FIXME encodeBase64转码文件名

                    String imgLocalName = Utils.string2U8(mList.get(position).getMagazineName() + mList.get(position).getYear() + mList.get(position).getIssue());
//                    + ".png"

                    Log.e("encodeBase64转码文件名", imgLocalName);
                    // 下载封面
                    String target = savaPicPath + imgLocalName;

                    HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                    httpUtils.download(mList.get(position).getCover(), target,
                            new RequestCallBack<File>() {

                                @Override
                                public void onSuccess(ResponseInfo<File> arg0) {

                                }

                                @Override
                                public void onFailure(HttpException arg0,
                                                      String arg1) {
                                    Log.e("onFailure", arg1);
                                }
                            }
                    );
                } else {
                    for (int i = 0; i < isAddedPosition.size(); i++) {
                        if (isAddedPosition.get(i) == position) {
                            Toast.makeText(mContext, "您已添加过该期杂志", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            position_add = position;
                        }
                    }
                    if (position_add != -1) {
                        LoadingDialog.showDialog(mContext, "下载中...");
                        saveOfflineDirectoryData(mList.get(position_add)
                                .getMagazineName(), mList.get(position_add)
                                .getMagazineId(), mList.get(position_add)
                                .getYear(), mList.get(position_add).getIssue());


                        detailList = new MagazineDetailListBean();
                        detailList.setMagazineId(mList.get(position_add)
                                .getMagazineId());
                        detailList.setMagazineName(mList.get(position_add)
                                .getMagazineName());
                        detailList.setCover(mList.get(position_add).getCover());
                        detailList.setIssue(mList.get(position_add).getIssue());
                        detailList.setYear(mList.get(position_add).getYear());

                        // 下载封面
                        String target = savaPicPath
                                + Utils.string2U8(mList.get(position_add).getMagazineName()
                                + mList.get(position_add).getYear()
                                + mList.get(position_add).getIssue());
//                         + ".png"

                        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                        httpUtils.download(mList.get(position_add).getCover(),
                                target, new RequestCallBack<File>() {

                                    @Override
                                    public void onSuccess(
                                            ResponseInfo<File> arg0) {
                                    }

                                    @Override
                                    public void onFailure(HttpException arg0,
                                                          String arg1) {
                                    }
                                }
                        );
                        position_add = -1;
                    }
                }
            }
        });
        return convertView;
    }

    private void saveOfflineDirectoryData(final String mag_name,
                                          String mag_guid, final String year, final String issue) {

//        String path = LISTURL + "authtoken=" + LyApplication.authToken
//                + "&magazineguid=" + mag_guid + "&year=" + year + "&issue="
//                + issue;

        String path = MAGDIRECTORY_URL + "apptoken=" + Utils.createAppToken() + "&ticket="
                + ConstantsAmount.TICKET + "&magazineguid=" + mag_guid + "&year=" + year + "&issue=" + issue;

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                Log.e("onFailure", arg0.toString());
                LoadingDialog.dissmissDialog();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                handler.sendMessage(handler.obtainMessage(1,
                        arg0.result));
            }
        });
    }

    private void getOfflineReaderList(final String titleid) {

//        String magReaderUrl = LISTREADERURL + "titleid=" + titleid
//                + "&authToken=" + LyApplication.authToken;

        String path = GETDETAIL_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&articleid=" + titleid;

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        getOfflineReaderList(titleid);
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        MagazineReaderBean magReaderListData = null;
                        try {
                            magReaderListData = getMagReaderListData(arg0.result);
                            DataBase.getInstance(mContext)
                                    .addOfflineMagazineReaderList(
                                            magReaderListData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }

    private List<GroupListData> getDriectoryListData(String json, int position) throws JSONException {
        groupList = new ArrayList<GroupListData>();
        childList = new ArrayList<ChildListData>();

        GroupListData groupListData = null;
        ChildListData childListData = null;

        /*JSONObject jsonObject = new JSONObject(json);
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
                childListData.setYear("2012");
                childListData.setIssue("1");
                childListData.setIntroduction("");
                childListData.setCategoryCode("");
                childListData.setArticleImgList("");
                childListData.setPubStartDate("");
                childListData.setArticleImgWidth("");
                childListData.setArticleImgHeight("");
                childListData.setMagazineLogo("");
                childList.add(childListData);
            }
            groupList.add(groupListData);*/
//            Log.e("getListData:groupList", "//" + groupList.size());
//            Log.e("getListData:childList", "//" + childList.size());
        JSONObject jsonObject = new JSONObject(json);
        if (Utils.checkRequestCode(jsonObject).equals("1")) {
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
                    childListData.setMagazineName(mName);
                    childList.add(childListData);
                }
                groupList.add(groupListData);
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
            // 目录接口数据存入数据库（区分已读和未读）
            DataBase.getInstance(mContext).addOfflineMagDirectoryMagazineList(
                    groupList);
        } else {
            Toast.makeText(mContext, Utils.jsonMessageParser(jsonObject),
                    Toast.LENGTH_LONG).show();
            return null;
        }
        return groupList;
    }

    /*private List<GroupListData> getDriectoryListData(String json)
            throws JSONException {
        groupList = new ArrayList<GroupListData>();
        childList = new ArrayList<ChildListData>();

        GroupListData groupListData = null;
        ChildListData childListData = null;

        JSONObject jsonObject = new JSONObject(json);
        String name = jsonObject.optString("MagazineName");
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
            String Column = jo.optString("Column");
            if (Column.equals("") || Column.equals("null")) {
                Log.e("column", "=null");
                Column = "其他";
            }

            String CategoryCode = jsonObject.optString("CategoryCode");
            String ArticleImgList = jsonObject.optString("ArticleImgList");
            String PubStartDate = jsonObject.optString("PubStartDate");
            String ArticleImgWidth = jsonObject.optString("ArticleImgWidth");
            String ArticleImgHeight = jsonObject.optString("ArticleImgHeight");
            String MagazineLogo = jsonObject.optString("MagazineLogo");
            jsonObject.optString("MagazineLogo");

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
            // Log.e("groupList", groupList.get(m).getColumn() + "///"
            // + groupList.get(m).getList().size());
        }

        // 目录接口数据存入数据库（区分已读和未读）
        DataBase.getInstance(mContext).addOfflineMagDirectoryMagazineList(
                groupList);
        return groupList;
    }*/

    private MagazineReaderBean getMagReaderListData(String json) throws JSONException {
        List<MagazineReaderBean> list = new ArrayList<MagazineReaderBean>();
        MagazineReaderBean info = new MagazineReaderBean();
        JSONObject jsonObject = new JSONObject(json);
//        if (checkRequestCode(jsonObject).equals("1")) {
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
        info.setPreviousTitleid(jo.getJSONObject("PreviousArticle").optString("dfwa20120101"));
        info.setNextTitleid(jo.getJSONObject("NextArticle").optString("NextArticle"));
//            list.add(info);
        return info;
//        } else {
////            Toast.makeText(mContext, jsonMessageParser(jsonObject), Toast.LENGTH_LONG).show();
//            return null;
//        }
    }

    /*private MagazineReaderBean getMagReaderListData(String json)
            throws JSONException {
        MagazineReaderBean info = new MagazineReaderBean();

        JSONObject jsonObject = new JSONObject(json);

        info.setMagazineName(jsonObject.optString("MagazineName"));
        info.setMagazineGUID(jsonObject.optString("MagazineGUID"));
        info.setIssue(jsonObject.optString("Issue"));
        info.setYear(jsonObject.optString("Year"));
        info.setTitle(jsonObject.optString("Title"));
        info.setTitleid(jsonObject.optString("Titleid"));
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

        return info;
    }*/

    class ViewHolder {
        private ImageView magdetail_item_img;
        private TextView magdetail_item_magissue;
        private Button addbtn;
    }
}
