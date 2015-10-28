package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.PopupWindow;
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
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.MagazineDetailActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyMagazineSearchListAdapter;
import com.longyuan.qm.bean.MagazineClassifyBean;
import com.longyuan.qm.bean.MagazineListBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.fragment.MagazineShopFragment.PopWindowGVAdapter.ViewHolder;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MagazineShopFragment extends BaseFragment implements
        ViewPager.OnPageChangeListener {
    private static String TABINDICATOR_URL = null;
    //	private static String TABINDICATOR_URL = ConstantsAmount.BASEURL
//			+ "MagazineCategoryList.ashx?";
//    private static String SEARCHLISTURL = ConstantsAmount.BASEURL
//            + "magazineSeach.ashx?";
    private static String SEARCHLISTURL = null;
    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            int length = searchEditText.getText().toString().trim().length();
            if (length == 0) {
                mPager.setVisibility(View.VISIBLE);
                mGridView.setVisibility(View.INVISIBLE);
            }
        }
    };
    private TextView failure;
    private TabPageIndicator indicator;
    private ViewPager mPager;
    private Button mPullDown_Btn, searchButton;
    private EditText searchEditText;
    private PullToRefreshGridView mGridView;
    private MagazineClassifyFragment magazineClassifyFragment;
    private MagazineDetailFragment magazineDetailFragment;
    private TabPageIndicatorAdapter mAdapter;
    private List<MagazineListBean> parseMagazineList = new ArrayList<MagazineListBean>();
    private ArrayList<MagazineClassifyBean> searchDataFromJson = null;
    private ViewHolder holder;
    private boolean hasData = false;
    private InputMethodManager manager;
    /**
     * Fragment中，注册
     * 接收MainActivity的Touch回调的对象
     * 重写其中的onTouchEvent函数，并进行该Fragment的逻辑处理
     */

    private HomeActivity.MyTouchListener mTouchListener = new HomeActivity.MyTouchListener() {
        @Override
        public void onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub
            try {
                if (getActivity().getCurrentFocus() != null
                        && getActivity().getCurrentFocus().getWindowToken() != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        Utils.getHttpRequestHeader();
        TABINDICATOR_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "category/GetAllByKind?";
        SEARCHLISTURL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "magazine/search?";

        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((HomeActivity) this.getActivity()).registerMyTouchListener(mTouchListener);

        View mView = inflater.inflate(R.layout.magazineshop_fragment_normal,
                null);
        // FIXME 设置TabPageIndicator的样式
        mContext.setTheme(com.viewpagerindicator.R.style.Theme_PageIndicatorDefaults);


        if (hasData) {
            initView(mView);
        } else {
            if (isInternet()) {
                getTabPageIndicatorData();
            } else {
                Toast.makeText(mContext,
                        ConstantsAmount.BAD_NETWORK_CONNECTION,
                        Toast.LENGTH_LONG).show();
            }
            initView(mView);
        }
        return mView;
    }

    private void initView(View mView) {
        mGridView = (PullToRefreshGridView) mView
                .findViewById(R.id.magazine_search_gridview);
        mGridView.setVisibility(View.INVISIBLE);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
//                FragmentTransaction beginTransaction = getActivity()
//                        .getSupportFragmentManager().beginTransaction();
                if (magazineDetailFragment == null) {
//                    magazineDetailFragment = new MagazineDetailFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("mMagazineClassifyList",
//                            searchDataFromJson);
//                    bundle.putInt("position", arg2);
//                    magazineDetailFragment.setArguments(bundle);
//                    beginTransaction.replace(R.id.fl_main,
//                            magazineDetailFragment);
//                    searchButton.setClickable(false);
//                    beginTransaction.commit();

                    Intent intent = new Intent(mContext, MagazineDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("mMagazineClassifyList", searchDataFromJson);
                    bundle.putInt("position", arg2);
//                    bundle.putString("categoryName", categoryName);
                    intent.putExtra("bundle", bundle);
                    startActivity(intent);
                }
            }
        });
        indicator = (TabPageIndicator) mView.findViewById(R.id.mag_indicator);
        mPager = (ViewPager) mView.findViewById(R.id.mag_vp_list);
        failure = (TextView) mView.findViewById(R.id.mag_failure);
        mPullDown_Btn = (Button) mView.findViewById(R.id.button1);
        failure = (TextView) mView.findViewById(R.id.mag_failure);
        mAdapter = new TabPageIndicatorAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        indicator.setViewPager(mPager);
        indicator.setOnPageChangeListener(this);
//		mPullDown_Btn.setVisibility(View.GONE);
        mPullDown_Btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initPopupWindow(mPager.getCurrentItem());
            }
        });
//        mGridView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
////                FragmentTransaction beginTransaction = getActivity()
////                        .getSupportFragmentManager().beginTransaction();
//                if (magazineDetailFragment == null) {
////                    magazineDetailFragment = new MagazineDetailFragment();
////                    Bundle bundle = new Bundle();
////                    bundle.putSerializable("bundleListMagInfo",
////                            searchDataFromJson);
////                    bundle.putInt("position", arg2);
////                    magazineDetailFragment.setArguments(bundle);
////                    beginTransaction.replace(R.id.fl_main,
////                            magazineDetailFragment);
////                    searchButton.setClickable(false);
////                    beginTransaction.commit();
//                    Intent intent = new Intent(mContext, MagazineDetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("mMagazineClassifyList", searchDataFromJson);
//                    bundle.putInt("position", arg2);
////                    bundle.putString("categoryName", categoryName);
//                    intent.putExtra("bundle", bundle);
//                    startActivity(intent);
//                }
//            }
//        });

        searchEditText = (EditText) mView.findViewById(R.id.mag_search_edit);
        searchButton = (Button) mView.findViewById(R.id.mag_search_button);
        searchButton.setClickable(true);
        searchButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                searchButton.requestFocus();
                String searchContent = searchEditText.getText().toString()
                        .trim();
                if (!isInternet()) {
                    Toast.makeText(mContext, "当前网络不可用,请检测网络！",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (searchContent.equals("")) {
                    Toast.makeText(mContext, "请输入内容！", Toast.LENGTH_LONG)
                            .show();
                } else {
                    mPager.setVisibility(View.INVISIBLE);
                    mGridView.setVisibility(View.VISIBLE);
                    LoadingDialog.showDialog(mContext, "正在加载...");
                   /* String path = SEARCHLISTURL + "authToken=" + LyApplication.authToken
                            + "&pageindex=1&pagesize=5000&keyword="
                            + searchContent;*/
                    //FIXME  目前没分页操作，默认展示一页，90个；
                    String path = SEARCHLISTURL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET
                            + "&magazinetype=2" + "&keyword=" + Utils.string2U8(searchContent) + "&pagesize=1000&pageindex=1&itemcount=0";
                    HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                    httpUtils.send(HttpMethod.GET, path,
                            new RequestCallBack<String>() {
                                @Override
                                public void onFailure(HttpException arg0,
                                                      String arg1) {
                                    LoadingDialog.dissmissDialog();
                                    Toast.makeText(mContext, "搜索失败！",
                                            Toast.LENGTH_LONG).show();
                                    searchEditText.setText("");
                                }

                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    LoadingDialog.dissmissDialog();
                                    try {
                                        searchDataFromJson = getSearchDataFromJson(arg0.result);
                                        searchDataFromJson.size();
                                        MyMagazineSearchListAdapter adapter = new MyMagazineSearchListAdapter(
                                                getActivity(),
                                                searchDataFromJson);
                                        mGridView.setAdapter(adapter);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                }
            }
        });

//        mGridView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//                                    long arg3) {
//                FragmentTransaction beginTransaction = getActivity()
//                        .getSupportFragmentManager().beginTransaction();
//                if (magazineDetailFragment == null) {
//                    magazineDetailFragment = new MagazineDetailFragment();
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("mMagazineClassifyList",
//                            searchDataFromJson);
//                    bundle.putInt("position", arg2);
//                    magazineDetailFragment.setArguments(bundle);
//                    beginTransaction.replace(R.id.fl_main,
//                            magazineDetailFragment);
//                    beginTransaction.commit();
//                }
//            }
//        });

        searchEditText.addTextChangedListener(textWatcher);

        // 首先获得软键盘Manager
        failure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getTabPageIndicatorData();
            }
        });
    }

    private List<MagazineListBean> getTabPageIndicatorData() {
        LoadingDialog.showDialog(mContext, "正在加载...");
//		final String path = TABINDICATOR_URL + "authToken="
//				+ LyApplication.authToken;
        final String path = TABINDICATOR_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&kind=2";

        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpMethod.GET, path, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                LoadingDialog.dissmissDialog();
                failure.setVisibility(View.VISIBLE);
                Toast.makeText(mContext, ConstantsAmount.REQUEST_ONFAILURE,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                LoadingDialog.dissmissDialog();
                failure.setVisibility(View.INVISIBLE);
                MagazineListBean bean = null;
                ArrayList<MagazineListBean> list = new ArrayList<MagazineListBean>();
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = (JSONArray) jsonObject.get("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            bean = new MagazineListBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            bean.setCategoryCode(jo.optString("CategoryCode"));
                            bean.setCategoryName(jo.optString("CategoryName"));
                            list.add(bean);
                        }

                        String[] classifyArray = Utils.menuValueArray();
                        //FIXME 按照截取的数组排序录入List集合；
                        for (int i = 0; i < classifyArray.length; i++) {
                            for (int j = 0; j < list.size(); j++) {
                                if (list.get(j).getCategoryCode().equals(classifyArray[i])) {
                                    Log.e("foreach", classifyArray[i]);
                                    parseMagazineList.add(list.get(j));
                                }
                            }
                        }

                        if (parseMagazineList.size() > 0) {
//							mPullDown_Btn.setVisibility(View.VISIBLE);
                            hasData = true;
                            mAdapter.notifyDataSetChanged();
                            indicator.notifyDataSetChanged();
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
                        mPullDown_Btn.setVisibility(View.GONE);
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

				/*try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
					if (checkRequestCode(jsonObject).equals("1")) {
						JSONArray jsonArray = jsonObject
								.getJSONArray("Catagorys");
						for (int i = 0; i < jsonArray.length(); i++) {
							bean = new MagazineListBean();
							JSONObject jo = (JSONObject) jsonArray.get(i);
							bean.setCategoryCode(jo.optString("CategoryCode"));
							bean.setCategoryName(jo.optString("CategoryName"));
							bean.setParentCategoryCode(jo
									.optString("ParentCategoryCode"));
							bean.setIcon(jo.optString("Icon"));
							bean.setBackGround(jo.optString("BackGround"));
							parseMagazineList.add(bean);
						}
						if (parseMagazineList.size() > 0) {
//							mPullDown_Btn.setVisibility(View.VISIBLE);
							hasData = true;
							mAdapter.notifyDataSetChanged();
							indicator.notifyDataSetChanged();
						}
					} else {
						mPullDown_Btn.setVisibility(View.GONE);
						Toast.makeText(mContext, jsonMessageParser(jsonObject),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
            }
        });
        return parseMagazineList;
    }

    private void initPopupWindow(int position) {
        // 获得状态栏高度，title栏高度，屏幕的宽高算出PopupWindow的宽高;
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getActivity().getWindowManager().getDefaultDisplay()
                .getWidth();
        int height = getActivity().getWindowManager().getDefaultDisplay()
                .getHeight();

        // 从MagazineFragment中传过来的bundle中获取；
        Bundle bundle = getArguments();
        int headLayoutHeight = bundle.getInt("headheight");

        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vPopWindow = inflater.inflate(R.layout.mypopupwindow_layout, null,
                false);
        final PopupWindow mPw = new PopupWindow(vPopWindow, width, height
                - headLayoutHeight - statusBarHeight, true);
        mPw.showAtLocation(indicator, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
        GridView gridView = (GridView) vPopWindow
                .findViewById(R.id.column_edit_gv_gvcolumn);
        PopWindowGVAdapter adapter = new PopWindowGVAdapter(parseMagazineList, position);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mPw.dismiss();
                indicator.setCurrentItem(arg2);
            }
        });

        View block_View = vPopWindow.findViewById(R.id.block_view);
        block_View.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPw.dismiss();
            }
        });

//		Button button = (Button) vPopWindow.findViewById(R.id.button1);
//		button.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				mPw.dismiss();
//
//			}
//		});
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        ((HomeActivity) getActivity()).initSlidingMenuListener(arg0);
    }

    private ArrayList<MagazineClassifyBean> getSearchDataFromJson(String json)
            throws JSONException {
        ArrayList<MagazineClassifyBean> searchPraseList = new ArrayList<MagazineClassifyBean>();
        MagazineClassifyBean searchInfo;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.optString("Code").equals("0")) {
            Toast.makeText(mContext, "发送错误！", Toast.LENGTH_LONG).show();
            return new ArrayList<MagazineClassifyBean>();
        }
        if (jsonObject.optString("ItemCount").equals("0")) {
            Toast.makeText(mContext, "没有此杂志！", Toast.LENGTH_LONG).show();
            searchEditText.setText("");
            return new ArrayList<MagazineClassifyBean>();
        }

        if (checkRequestCode(jsonObject).equals("1")) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("Data");

            if (jsonArray.length() == 0) {
                Toast.makeText(mContext, "没有此杂志！", Toast.LENGTH_LONG).show();
                return new ArrayList<MagazineClassifyBean>();
            } else {
                for (int i = 0; i < jsonArray.length(); i++) {
                    searchInfo = new MagazineClassifyBean();
                    JSONObject jo = (JSONObject) jsonArray.get(i);
                    searchInfo.setMagazineName(jo.optString("MagazineName"));
                    searchInfo.setMagazineGUID(jo.optString("MagazineGuid"));
                    JSONArray ja_MagazineType = jo.getJSONArray("MagazineType");
                    for (int j = 0; j < ja_MagazineType.length(); j++) {
                        JSONObject jo_MagazineType = (JSONObject) ja_MagazineType.get(j);
                        jo_MagazineType.optString("MagzineType");
                    }
                    JSONArray ja_CoverImages = jo.getJSONArray("CoverImages");
                    for (int k = 0; k < ja_CoverImages.length(); k++) {
                        if (ja_CoverImages.getString(k).endsWith("-l.jpg")) {
                            searchInfo.setCoverPicList(ja_CoverImages.getString(k));
                            break;
                        }
                    }
                    searchPraseList.add(searchInfo);
                }
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
                            LyApplication.authToken = null;
                            ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
                            startActivity(intent);
                            HomeActivity.instance_home.finish();
                        }
                    }).setCancelable(false).create();
            alertDialog.show();
        } else {
            Toast.makeText(mContext, jsonMessageParser(jsonObject),
                    Toast.LENGTH_LONG).show();
        }

        return searchPraseList;
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        private FragmentTransaction mTransaction = null;
        private FragmentManager mFragmentManager;

        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentManager = fm;
        }

        @Override
        public int getCount() {
            return parseMagazineList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return parseMagazineList.get(position).getCategoryName();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment) object).getView() == view;
        }

        protected String getTag(int position) {
            return (String) getPageTitle(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mTransaction == null) {
                mTransaction = mFragmentManager.beginTransaction();
            }

            String name = getTag(position);
            MagazineClassifyFragment fragment = (MagazineClassifyFragment) mFragmentManager
                    .findFragmentByTag(name);

            if (fragment != null) {
                mTransaction.attach(fragment);

            } else {
                fragment = (MagazineClassifyFragment) getItem(position);

                mTransaction.add(container.getId(), fragment, getTag(position));
            }

            return fragment;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mTransaction != null) {
                mTransaction.commitAllowingStateLoss();// 可以丢失
                // mTransaction.commit();// 不可以的
                mTransaction = null;
                mFragmentManager.executePendingTransactions();// 马上提交
            }
        }

        @Override
        public Fragment getItem(int arg0) {
            magazineClassifyFragment = new MagazineClassifyFragment(arg0);

            Bundle bundle = new Bundle();
            ArrayList bundleList = new ArrayList();
            bundleList.add(parseMagazineList);
            bundle.putParcelableArrayList("list", bundleList);
            bundle.putString("mag_categoryName", parseMagazineList.get(arg0)
                    .getCategoryName());
            magazineClassifyFragment.setArguments(bundle);
            return magazineClassifyFragment;
        }
    }

    class PopWindowGVAdapter extends BaseAdapter {
        private List<MagazineListBean> mList;
        private int mPosition;

        public PopWindowGVAdapter(List<MagazineListBean> list, int position) {
            this.mList = list;
            this.mPosition = position;
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
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.column_edit_items, null);
                holder.tv_item = (TextView) convertView
                        .findViewById(R.id.column_tv_newstitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position == mPosition) {
                holder.tv_item.setText(mList.get(position).getCategoryName());
                holder.tv_item.setTextColor(getResources().getColor(R.color.column_tv_bg1));
            } else {
                holder.tv_item.setText(mList.get(position).getCategoryName());
                holder.tv_item.setTextColor(getResources().getColor(R.color.column_tv_bg2));
            }
            return convertView;
        }

        class ViewHolder {
            private TextView tv_item;
        }
    }
}
