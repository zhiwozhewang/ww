package com.longyuan.qm.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.RelativeLayout;
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
import com.longyuan.qm.activity.BookDetailActivity;
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.adapter.MyBookSearchListAdapter;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.BookListBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.fragment.BookShopFragment.PopWindowGVAdapter.ViewHolder;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshGridView;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class BookShopFragment extends BaseFragment implements
        ViewPager.OnPageChangeListener, OnItemClickListener {
    private static String LISTDATA_URL = ConstantsAmount.BASEURL
            + "GetbookCategorys.ashx?";
    private static String TABINDICATOR_URL = null;
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
                book_search_counts.setVisibility(View.INVISIBLE);
            }
        }
    };
    private Button head_LeftBtn, searchButton;
    private TextView title, failure, book_search_counts;
    private BookClassifyFragment bookClassifyFragment;
    private TabPageIndicator indicator;
    private Button showColumn;
    private ViewPager mPager;
    private PullToRefreshGridView mGridView;
    private EditText searchEditText;
    private BookDetailFragment bookDetailFragment;
    private RelativeLayout headLayout;
    private TabPageIndicatorAdapter mAdapter;
    private ArrayList<BookListBean> bookList = new ArrayList<BookListBean>();
    private ArrayList<BookClassifyBean> bookClassifyBean = new ArrayList<BookClassifyBean>();
    private boolean hasData = false;
    private String sdPath = Environment.getExternalStorageDirectory()
            .toString();
    private String baseBookPath = sdPath + "/LYyouyue/";
    private ViewHolder holder;
    private InputMethodManager manager;
    private int pageSize = 100, pageIndex = 1;
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
        manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        ((HomeActivity) this.getActivity()).registerMyTouchListener(mTouchListener);
        Utils.getHttpRequestHeader();
        TABINDICATOR_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "category/GetAllByKind?";
        SEARCHLISTURL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "book/search?";

        View mView = LayoutInflater.from(getActivity()).inflate(
                R.layout.bookshop_activity_normal, null);
        // FIXME 设置TabPageIndicator的样式
        mContext.setTheme(com.viewpagerindicator.R.style.Theme_PageIndicatorDefaults);

        // 判断是否有数据，若没有数据则不加载控件；
        if (hasData) {
            initView(mView);
        } else {
            if (isInternet()) {
                getBookShopInfo();
            } else {
                Toast.makeText(getActivity(), "当前网络不可用,请检测网络!",
                        Toast.LENGTH_LONG).show();
            }
            initView(mView);
        }
        //
        head_LeftBtn = (Button) mView.findViewById(R.id.head_layout_showLeft);
        head_LeftBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).showMenu();
            }
        });
        title = (TextView) mView.findViewById(R.id.head_layout_text);
        book_search_counts = (TextView) mView.findViewById(R.id.book_search_counts);
        title.setText(ConstantsAmount.MENUBEANLIST.get(ConstantsAmount.MENUPOSITION).getName());
        headLayout = (RelativeLayout) mView.findViewById(R.id.relativeLayout1);
        mGridView = (PullToRefreshGridView) mView
                .findViewById(R.id.book_search_gridview);
        mGridView.setVisibility(View.INVISIBLE);
        searchEditText = (EditText) mView.findViewById(R.id.book_search_edit);
        searchButton = (Button) mView.findViewById(R.id.book_search_button);
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
                    book_search_counts.setVisibility(View.VISIBLE);
                    LoadingDialog.showDialog(mContext, "正在加载...");
                   /* String path = SEARCHLISTURL + "authToken=" + LyApplication.authToken
                            + "&pageindex=1&pagesize=5000&keyword="
                            + searchContent;*/
                    //FIXME
                    String path = SEARCHLISTURL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET
                            + "&booktype=5" + "&keyword=" + Utils.string2U8(searchContent) + "&pagesize=" + pageSize +"&pageindex=" + pageIndex + "&itemcount=0";
//
                    HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                    httpUtils.send(HttpMethod.GET, path,
                            new RequestCallBack<String>() {
                                @Override
                                public void onFailure(HttpException arg0,
                                                      String arg1) {
                                    LoadingDialog.dissmissDialog();
                                    Toast.makeText(mContext, "搜索失败！", Toast.LENGTH_LONG).show();
                                    searchEditText.setText("");
                                    book_search_counts.setVisibility(View.INVISIBLE);
                                }

                                @Override
                                public void onSuccess(ResponseInfo<String> arg0) {
                                    LoadingDialog.dissmissDialog();
                                    try {
                                        bookClassifyBean = getBookDataFromJson(arg0.result);
                                        // FIXME
                                        MyBookSearchListAdapter adapter = new MyBookSearchListAdapter(getActivity(), bookClassifyBean);
                                        mGridView.setAdapter(adapter);
                                        book_search_counts.setText("共为您找出 " + bookClassifyBean.size() + " 本图书!");
//                                        Toast.makeText(mContext, "共为您找出 " + bookClassifyBean.size() + " 本图书!", Toast.LENGTH_LONG).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                }
            }
        });

        mGridView.setOnItemClickListener(this);
        searchEditText.addTextChangedListener(textWatcher);
        return mView;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                            long arg3) {
        FragmentTransaction beginTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        if (bookDetailFragment == null) {
//            bookDetailFragment = new BookDetailFragment();
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("mBookClassifyList",
//                    bookClassifyBean);
//            bundle.putInt("position", arg2);
//            bookDetailFragment.setArguments(bundle);
//            beginTransaction.add(R.id.fl_main,
//                    bookDetailFragment);
////            beginTransaction.addToBackStack("BC");
////            beginTransaction.hide(this);
//            beginTransaction.commit();

            Intent intent = new Intent(mContext, BookDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("bList", bookClassifyBean);
            bundle.putInt("position", arg2);
            intent.putExtra("bundle", bundle);
            startActivity(intent);
        }
    }


    private void initView(View mView) {
        mPager = (ViewPager) mView.findViewById(R.id.bookshop_vp_list);
        failure = (TextView) mView.findViewById(R.id.bookshop_failure);
        indicator = (TabPageIndicator) mView
                .findViewById(R.id.bookshop_indicator);
        showColumn = (Button) mView.findViewById(R.id.button1);
//		showColumn.setVisibility(View.GONE);

        showColumn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initPopupWindow(mPager.getCurrentItem());
            }
        });

        mAdapter = new TabPageIndicatorAdapter(getChildFragmentManager(),
                bookList);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        // FIXME TabPageIndicator 绑定 ViewPager
        indicator.setViewPager(mPager);
        indicator.setOnPageChangeListener(this);
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
        int headLayoutHeight = headLayout.getLayoutParams().height;

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
        PopWindowGVAdapter adapter = new PopWindowGVAdapter(bookList, position);
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
//			}
//		});
    }

    private boolean getBookShopInfo() {
        LoadingDialog.showDialog(mContext, "正在加载...");

        String path = TABINDICATOR_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&kind=3";
        //String path = LISTDATA_URL + "authtoken=" + LyApplication.authToken + "&booktype=24";
        HttpUtils utils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        utils.send(HttpMethod.GET, path,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        LoadingDialog.dissmissDialog();
                        failure.setVisibility(View.VISIBLE);
                        failure.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                getBookShopInfo();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> arg0) {
                        LoadingDialog.dissmissDialog();
                        try {
                            bookList = getBookList(arg0.result);
                            failure.setVisibility(View.INVISIBLE);
//							showColumn.setVisibility(View.VISIBLE);
                            if (bookList.size() > 0) {
                                hasData = true;
                            }
                            // 4
                            mAdapter.notifyDataSetChanged();
                            indicator.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        return false;
    }

    private ArrayList<BookListBean> getBookList(String json) throws JSONException {
        ArrayList<BookListBean> list = new ArrayList<BookListBean>();
        ArrayList<BookListBean> allList = new ArrayList<BookListBean>();
        BookListBean book = null;
        JSONObject jsonObject = new JSONObject(json);
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
            for (int i = 0; i < jsonArray.length(); i++) {
                book = new BookListBean();
                JSONObject jo = (JSONObject) jsonArray.get(i);
                book.setCategoryID(jo.optString("CategoryCode"));
                book.setCategoryName(jo.optString("CategoryName"));
                allList.add(book);
            }
            String[] classifyArray = Utils.menuValueArray();
            //FIXME 按照截取的数组排序录入List集合；
            for (int i = 0; i < classifyArray.length; i++) {
                for (int j = 0; j < allList.size(); j++) {
                    if (allList.get(j).getCategoryID().equals(classifyArray[i])) {
                        list.add(allList.get(j));
                    }
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
        return list;
    }
    /*LoadingDialog.dissmissDialog();
    failure.setVisibility(View.INVISIBLE);
    MagazineListBean bean = null;
    try {
        JSONObject jsonObject = new JSONObject(arg0.result);
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONArray ja = (JSONArray) jsonObject.get("Data");
            for (int i = 0; i < ja.length(); i++) {
                bean = new MagazineListBean();
                JSONObject jo = (JSONObject) ja.get(i);
                bean.setCategoryCode(jo.optString("CategoryCode"));
                bean.setCategoryName(jo.optString("CategoryName"));
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


    private ArrayList<BookClassifyBean> getBookDataFromJson(String json)
            throws JSONException {
        ArrayList<BookClassifyBean> bookPraseList = new ArrayList<BookClassifyBean>();
        BookClassifyBean searchInfo;
        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.optString("Code").equals("0")) {
            Toast.makeText(mContext, "发送错误！", Toast.LENGTH_LONG).show();
            return new ArrayList<BookClassifyBean>();
        }
        if (jsonObject.optString("ItemCount").equals("0")) {
            Toast.makeText(mContext, "没有该图书！", Toast.LENGTH_LONG).show();
            searchEditText.setText("");
            return new ArrayList<BookClassifyBean>();
        }
        if (checkRequestCode(jsonObject).equals("1")) {
            JSONArray jsonArray = (JSONArray) jsonObject.get("Data");
            if (jsonArray.length() == 0) {
                Toast.makeText(mContext, "没有该图书！", Toast.LENGTH_LONG).show();
                return new ArrayList<BookClassifyBean>();
            } else {
                JSONArray ja = jsonObject.getJSONArray("Data");
                for (int i = 0; i < ja.length(); i++) {
                    searchInfo = new BookClassifyBean();
                    JSONObject jo = (JSONObject) ja.get(i);
                    searchInfo.setBookGuid(jo.optString("BookGuid"));
                    searchInfo.setBookName(jo.optString("BookName"));
                    searchInfo.setBookCover(jo.optString("CoverImages"));

//                searchInfo.setCategory();
                    searchInfo.setAuthor(jo.optString("Author"));
                    searchInfo.setBookPath(baseBookPath
                            + jo.optString("BookName") + ".epub");
                    JSONArray ja_BookTypes = jo.getJSONArray("BookTypes");
                    for (int j = 0; j < ja_BookTypes.length(); j++) {
                        JSONObject jo_BookType = (JSONObject) ja_BookTypes.get(j);
                        jo_BookType.optString("BookType");
                    }
                    bookPraseList.add(searchInfo);
                }

            /*for (int i = 0; i < jsonArray.length(); i++) {
                searchInfo = new BookListBean();
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
            }*/
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
        return bookPraseList;
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

    public boolean isFirst() {
        if (mPager.getCurrentItem() == 0)
            return true;
        else
            return false;
    }

    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        private FragmentTransaction mTransaction = null;
        private FragmentManager mFragmentManager;
        private List<BookListBean> mList = null;

        public TabPageIndicatorAdapter(FragmentManager fm,
                                       List<BookListBean> list) {
            super(fm);
            this.mFragmentManager = fm;
            this.mList = list;
        }

        @Override
        public int getCount() {
            if (mList.size() == 0) {
            }
            return bookList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return bookList.get(position).getCategoryName();
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
            BookClassifyFragment fragment = (BookClassifyFragment) mFragmentManager
                    .findFragmentByTag(name);

            if (fragment != null) {
                mTransaction.attach(fragment);

            } else {
                fragment = (BookClassifyFragment) getItem(position);
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
            bookClassifyFragment = new BookClassifyFragment(arg0);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("list", bookList);
            bookClassifyFragment.setArguments(bundle);
            return bookClassifyFragment;
        }
    }

    class PopWindowGVAdapter extends BaseAdapter {
        private List<BookListBean> mList;
        private int mPosition;

        public PopWindowGVAdapter(List<BookListBean> list, int position) {
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
