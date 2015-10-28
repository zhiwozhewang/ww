/**
 * @Title: ArticleTabFragment.java
 * @Package com.longyuan.qm.fragment
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-26 上午10:42:47
 * @version V1.0
 */
package com.longyuan.qm.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.longyuan.qm.activity.HomeActivity;
import com.longyuan.qm.activity.SplashActivity;
import com.longyuan.qm.bean.ArticletabItemBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dragonsource
 * @ClassName: ArticleTabFragment
 * @Description: 咨询页面(这里用一句话描述这个类的作用)
 * @date 2014-9-26 上午10:42:47
 */
public class ArticleTabFragment extends BaseFragment implements
        ViewPager.OnPageChangeListener {
    //	private static final String TABINDICATOR_URL = ConstantsAmount.BASEURL
//			+ "EssentialCategoryList.ashx?";
//    private static final String TABINDICATOR_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "category/GetAll?";
    private static String TABINDICATOR_URL = null;
    private TextView title_tv;
    private TabPageIndicator indicator;
    private ViewPager mPager;
    private Button mPullDown_Btn;
    private ArrayList<ArticletabItemBean> mList = new ArrayList<ArticletabItemBean>();
    private ArticlePagerAdapter mAdapter;
    private boolean hasData = false;
    private PopWindowGVAdapter.ViewHolder holder = null;
    private RelativeLayout headLayout;

    /**
     * (非 Javadoc) Title: onCreateView Description:
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     * @see com.longyuan.qm.BaseFragment#onCreateView(LayoutInflater,
     * ViewGroup, Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Utils.getHttpRequestHeader();
        TABINDICATOR_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "category/GetAllByKind?";

        View mView = inflater.inflate(R.layout.article_page_layout, null);
        title_tv = (TextView) mView.findViewById(R.id.head_layout_text);
        title_tv.setText(ConstantsAmount.MENUBEANLIST.get(ConstantsAmount.MENUPOSITION).getName());
        mPullDown_Btn = (Button) mView.findViewById(R.id.pulldown_btn);
        mContext = getActivity();
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

        mPullDown_Btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                initPopupWindow(mPager.getCurrentItem());
            }
        });
        return mView;
    }

    private void initView(View mView) {
        mPager = (ViewPager) mView.findViewById(R.id.vp_list);
        indicator = (TabPageIndicator) mView.findViewById(R.id.indicator);
        indicator.setOnPageChangeListener(this);
        mAdapter = new ArticlePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(1);
        indicator.setViewPager(mPager);
        headLayout = (RelativeLayout) mView.findViewById(R.id.head_layout);
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

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View vPopWindow = inflater.inflate(R.layout.mypopupwindow_layout, null,
                false);
        final PopupWindow mPw = new PopupWindow(vPopWindow, width, height
                - headLayoutHeight - statusBarHeight, true);
        mPw.showAtLocation(indicator, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
        GridView gridView = (GridView) vPopWindow
                .findViewById(R.id.column_edit_gv_gvcolumn);
        PopWindowGVAdapter adapter = new PopWindowGVAdapter(mList, position);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mPw.dismiss();
                indicator.setCurrentItem(arg2);
            }
        });

        View block_View = (View) vPopWindow.findViewById(R.id.block_view);
        block_View.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mPw.dismiss();
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        ((HomeActivity) mContext).initSlidingMenuListener(arg0);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getTabPageIndicatorData
     * @Description: 从网络获取分类数据(这里用一句话描述这个方法的作用)
     */
    private void getTabPageIndicatorData() {
        LoadingDialog.showDialog(mContext, "正在加载...");
//		String path = TABINDICATOR_URL + "authToken=" + LyApplication.authToken;
        String path = TABINDICATOR_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&kind=6";
//        + "&kind=1";
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
                    ArticletabItemBean bean = null;

                    List<ArticletabItemBean> list = new ArrayList<ArticletabItemBean>();
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        JSONArray ja = (JSONArray) jsonObject.get("Data");
                        for (int i = 0; i < ja.length(); i++) {
                            bean = new ArticletabItemBean();
                            JSONObject jo = (JSONObject) ja.get(i);
                            String categoryCode = jo.optString("CategoryCode");
                            String categoryName = jo.optString("CategoryName");
                            int categoryLevel = jo.optInt("CategoryLevel");
                            int kind = jo.optInt("Kind");
                            int resourceTotal = jo.optInt("ResourceTotal");
                            int orderNumber = jo.optInt("OrderNumber");
                            bean.setCategoryCode(categoryCode);
                            bean.setCategoryName(categoryName);
                            list.add(bean);
                        }
                        String[] classifyArray = Utils.menuValueArray();
                        //FIXME 按照截取的数组排序录入List集合；
                        for (int i = 0; i < classifyArray.length; i++) {
                            for(int j = 0; j < list.size(); j++) {
                                if (list.get(j).getCategoryCode().equals(classifyArray[i])) {
                                    mList.add(list.get(j));
                                }
                            }
                        }
                        if (mList.size() > 0) {
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
                        Toast.makeText(mContext, jsonMessageParser(jsonObject),
                                Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


				/*try {
                    ArticletabItemBean bean = null;
					JSONObject jsonObject = new JSONObject(arg0.result);
					if (checkRequestCode(jsonObject).equals("1")) {.GaaRa
						JSONArray jsonArray = jsonObject
								.getJSONArray("Catagorys");
						for (int i = 0; i < jsonArray.length(); i++) {
							bean = new ArticletabItemBean();
							JSONObject jo = (JSONObject) jsonArray.get(i);
							bean.setCategoryCode(jo.optString("CategoryCode"));
							bean.setCategoryName(jo.optString("CategoryName"));
							bean.setParentCategoryCode(jo
									.optString("ParentCategoryCode"));
							mList.add(bean);
						}
						if (mList.size() > 0) {
//							mPullDown_Btn.setVisibility(View.VISIBLE);
							hasData = true;
							mAdapter.notifyDataSetChanged();
							indicator.notifyDataSetChanged();
						}
					} else {
//						mPullDown_Btn.setVisibility(View.GONE);
						Toast.makeText(mContext, jsonMessageParser(jsonObject),
								Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}*/
            }
        });
    }

    class ArticlePagerAdapter extends FragmentPagerAdapter {
        private FragmentTransaction mTransaction = null;
        private FragmentManager mFragmentManager;
        private ArticleListFragment articleListFragment = null;

        public ArticlePagerAdapter(FragmentManager fm) {
            super(fm);
            this.mFragmentManager = fm;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position).getCategoryName();
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
            ArticleListFragment fragment = (ArticleListFragment) mFragmentManager
                    .findFragmentByTag(name);

            if (fragment != null) {
                mTransaction.attach(fragment);

            } else {
                fragment = (ArticleListFragment) getItem(position);

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
            articleListFragment = new ArticleListFragment(arg0);

            Bundle bundle = new Bundle();
            bundle.putSerializable("list", mList);
            articleListFragment.setArguments(bundle);
            return articleListFragment;
        }
    }

    class PopWindowGVAdapter extends BaseAdapter {
        private List<ArticletabItemBean> mList;
        private int mPosition;

        public PopWindowGVAdapter(List<ArticletabItemBean> list, int position) {
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
