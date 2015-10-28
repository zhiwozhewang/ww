/**
 * @Title: SearchFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: 文库页面(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-26 上午10:42:47
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import com.longyuan.qm.adapter.MySearchListAdapter;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.SearchListItemBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dragonsource
 * @ClassName: SearchFragment
 * @Description: 搜索文章(这里用一句话描述这个类的作用)
 * @date 2014-9-26 上午10:42:47
 */
public class SearchFragment extends BaseFragment {

    //    private static final String SEARCHLIST_URL = ConstantsAmount.BASEURL
//            + "ArticleSearchList.ashx?";
    private static String SEARCHLIST_URL = null;
    private EditText search_EditText;
    private Button search_Button, showLeft_Button;
    private ListView search_ListView;
    private TextView title;
    private int pageSize = 100, pageIndex = 1;
    private List<SearchListItemBean> mSearchList = null;
    private ClassifyCallBack mCallBack;
    private InputMethodManager manager;

//    public SearchFragment(){
//        // 在该Fragment的构造函数中注册mTouchListener的回调
//        ((HomeActivity)getActivity()).registerMyTouchListener(mTouchListener);
//    }
//    /**
//     * Fragment中，注册 接收MainActivity的Touch回调的对象
//     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
//     */
//    private HomeActivity.MyTouchListener mTouchListener = new HomeActivity.MyTouchListener() {
//
//        @Override
//        public void onTouchEvent(MotionEvent event) {
//            try {
//                if (mCallBack != null) {
//                    mCallBack.onCallBack(event);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    /**
     * Fragment中，注册
     * 接收MainActivity的Touch回调的对象
     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
     */
    private HomeActivity.MyTouchListener mTouchListener = new HomeActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            try {
                if (getActivity().getCurrentFocus() != null
                        && getActivity().getCurrentFocus().getWindowToken() != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        manager.hideSoftInputFromWindow(getActivity()
                                        .getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS
                        );
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected View setContentView(LayoutInflater inflater) {
        Utils.getHttpRequestHeader();
        SEARCHLIST_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "compilation/article/search?";

        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((HomeActivity) this.getActivity()).registerMyTouchListener(mTouchListener);

        View mView = inflater.inflate(R.layout.activity_search, null);
        title = (TextView) mView.findViewById(R.id.head_layout_text);
        search_Button = (Button) mView.findViewById(R.id.search_button);
        search_EditText = (EditText) mView.findViewById(R.id.search_editText);
        search_ListView = (ListView) mView.findViewById(R.id.search_listView);
        showLeft_Button = (Button) mView.findViewById(R.id.head_layout_showLeft);

        if (!ConstantsAmount.LOGININTERNETSTATE) {
            showLeft_Button.setBackgroundResource(R.drawable.uy_refresh);
            showLeft_Button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isInternet()) {
                        Toast.makeText(mContext, "正在验证登录信息...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, SplashActivity.class);
                        startActivity(intent);
                        HomeActivity.instance_home.finish();
                    } else {
                        Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        title.setText("文库");
        search_Button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    String getText = search_EditText.getText().toString();
                    String search_Text = URLEncoder.encode(getText, "utf-8");

                    if (getText.length() > 0) {
                        getSearchListData(search_Text, pageIndex, pageSize);

                    } else {
                        Toast.makeText(mContext, "请输入内容!", Toast.LENGTH_LONG)
                                .show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // 设置本页面可以侧滑
        ((HomeActivity) getActivity()).initSlidingMenuListener(0);
        search_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, ArticleActivity.class);
                intent.putExtra("title_id", mSearchList.get(i).getTitleID());
                intent.putExtra("formType", "3");
                startActivity(intent);
            }
        });

        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((HomeActivity) this.getActivity()).registerMyTouchListener(mTouchListener);

        return mView;
    }

    private void getSearchListData(String keyword, int pageIndex, int pageSize) {
       /* String path = SEARCHLIST_URL + "authToken=" + LyApplication.authToken
                + "&pageindex=" + pageIndex + "&pagesize=" + pageSize
                + "&keyword=" + keyword;*/

        LoadingDialog.showDialog(mContext, "正在加载...");
        String path = SEARCHLIST_URL + "apptoken=" + Utils.createAppToken() +
                "&ticket=" + ConstantsAmount.TICKET + "&categorycode=" + "&keyword="
                + keyword + "&pagesize=" + pageSize + "&pageindex=" + pageIndex + "&itemcount=0";

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                try {
                    mSearchList = new ArrayList<SearchListItemBean>();
                    SearchListItemBean bean = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
//                        String itemCount = jsonObject.optString("ItemCount");
                        String data = jsonObject.optString("Data");
                        if(data.equals("null")) {
                            Toast.makeText(mContext, "未找到资源!", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONArray ja = jsonObject.getJSONArray("Data");
                            for (int i = 0; i < ja.length(); i++) {
                                bean = new SearchListItemBean();
                                JSONObject jo = (JSONObject) ja.get(i);
                                bean.setTitleID(jo.optString("ArticleID"));
                                bean.setTitle(jo.optString("Title"));
                                bean.setAuthor(jo.optString("Author"));
                                bean.setKeyWord(jo.optString("KeyWord"));
                                bean.setYear(jo.optString("UpdateDate"));
                                mSearchList.add(bean);
                            }
                            search_ListView.setAdapter(new MySearchListAdapter(
                                    mContext, mSearchList));
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
                    mSearchList = new ArrayList<SearchListItemBean>();
                    SearchListItemBean bean = null;
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        String pageCount = jsonObject.optString("PageCount");
                        JSONArray ja = jsonObject.getJSONArray("Articlelist");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo = (JSONObject) ja.get(i);
                            bean = new SearchListItemBean();
                            bean.setPageCount(pageCount);
                            bean.setMagazineName(jo.optString("MagazineName"));
                            bean.setMagazineGUID(jo.optString("MagazineGUID"));
                            bean.setYear(jo.optString("Year"));
                            bean.setIssue(jo.optString("Issue"));
                            bean.setTitleID(jo.optString("TitleID"));
                            bean.setTitle(jo.optString("Title"));
                            bean.setAuthor(jo.optString("Author"));
                            bean.setAbstract(jo.optString("Abstract"));
                            mSearchList.add(bean);
                        }
                        search_ListView.setAdapter(new MySearchListAdapter(
                                mContext, mSearchList));
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

    public void setCallBack(ClassifyCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    public interface ClassifyCallBack {
        public void onCallBack(MotionEvent ev);
    }
}
