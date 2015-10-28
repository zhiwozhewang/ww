/**
 * @Title: HomeActivity.java
 * @Package com.longyuan.qm.activity
 * @Description: TODO(用一句话描述该文件做什么)
 * @author dragonsource
 * @date 2014-9-24 下午4:17:23
 * @version V1.0
 */
package com.longyuan.qm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyLeftSlidingListViewAdapter;
import com.longyuan.qm.adapter.MyRightSlidingListViewAdapter;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.bean.MenuBeanRight;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.fragment.LeftMenuFragment;
import com.longyuan.qm.fragment.RightMenuFragment;
import com.longyuan.qm.fragment.SearchFragment;
import com.longyuan.qm.listener.OnLeftMenuItemClickListener;
import com.longyuan.qm.listener.OnRightMenuItemClickListener;
import com.longyuan.qm.utils.ActivityUtil;
import com.longyuan.qm.utils.FileDES;
import com.longyuan.qm.utils.ToastUtils;
import com.longyuan.qm.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * @author dragonsource
 * @ClassName: HomeActivity
 * @Description: Fragment和slidingmenu的载体(这里用一句话描述这个类的作用)
 * @date 2014-9-24 下午4:17:23
 */
public class HomeActivity extends SlidingFragmentActivity {
    private LeftMenuFragment leftMenuFragment;
    private RightMenuFragment rightMenuFragment;
    private SlidingMenu mSlidingMenu;
    private OnLeftMenuItemClickListener onLeftMenuItemClickListener;
    private OnRightMenuItemClickListener onRightMenuItemClickListener;
    private MyLeftSlidingListViewAdapter leftMenuAdapter;
    private MyRightSlidingListViewAdapter rightMenuAdapter;
    private ArrayList<MenuBeanRight> menu_r = new ArrayList<MenuBeanRight>();
    private String name, phoneNumber, unitName, logoUrl, backgroundUrl;
    private boolean isInternet;
    protected SharedPreferences mSp;
    public static HomeActivity instance_home = null;
    private String userName = "";
    private List<BookClassifyBean> bookInfo = new ArrayList<BookClassifyBean>();
    /*
     * 保存MyTouchListener接口的列表
     */
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<HomeActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * (非 Javadoc) Title: onCreate Description:
     *
     * @param savedInstanceState
     * @see com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity#onCreate(Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance_home = this;
        mSp = ActivityUtil.getSharedPreferences(this);
        init();
    }

    private void init() {
        getIntentData();
        initSlidingMenu();
        initMenuFragment();
        initContent();
        overridePendingTransition(R.anim.slide_in_from_right_home,
                R.anim.slide_out_form_right);
    }

    /**
     * 初始化各个显示内容的fragment，并且把第一个fragment作为默认显示界面
     */
    private void initContent() {
        // 侧滑menu点击触发监听接口
        onLeftMenuItemClickListener = new OnLeftMenuItemClickListener(this, ConstantsAmount.MENUBEANLIST);
        onLeftMenuItemClickListener.showDefaultFragment();
        onRightMenuItemClickListener = new OnRightMenuItemClickListener(this, menu_r, HomeActivity.this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");
        unitName = intent.getStringExtra("unitName");
        logoUrl = intent.getStringExtra("logoUrl");
        backgroundUrl = intent.getStringExtra("backgroundUrl");
        isInternet = intent.getBooleanExtra("isInternet", false);
        ConstantsAmount.LOGININTERNETSTATE = isInternet;

        if(!isInternet) {
            Intent mInt = new Intent(HomeActivity.this, OffLineActivity.class);
            startActivity(mInt);
        }
    }

    /**
     * 初始化侧滑组件
     */
    private void initSlidingMenu() {
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.shadow);
        mSlidingMenu
                .setBehindOffset(ActivityUtil.getScreenPixel(this).widthPixels / 3);
        mSlidingMenu.setFadeEnabled(true);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
        mSlidingMenu.setAnimationCacheEnabled(true);
        mSlidingMenu.setAnimation(AnimationUtils.loadAnimation(this,
                R.anim.anim_scale_in));
        // actionBar不为null 所在Activity必须加上
        // android:theme="@style/Theme.Sherlock.Light"
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void initSlidingMenuListener(int position) {
        if (position == 0) {
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        } else {
            mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    /**
     * 初始化侧滑菜单fragment
     */
    private void initMenuFragment() {
        setBehindContentView(R.layout.layout_menu);
        mSlidingMenu.setSecondaryMenu(R.layout.layout_secondary_menu);
        FragmentTransaction fragmentTransaction = HomeActivity.this
                .getSupportFragmentManager().beginTransaction();

        leftMenuFragment = new LeftMenuFragment();
        rightMenuFragment = new RightMenuFragment();
//        menus = new ArrayList<MenuBean>();
        menu_r = new ArrayList<MenuBeanRight>();

//        menus.add(new MenuBean("资讯", R.drawable.icon_art,
//                new ArticleTabFragment(), R.drawable.icon_art_press,
//                R.drawable.icon_art_press));
//        menus.add(new MenuBean("杂志", R.drawable.icon_mgnz,
//                new MagazineFragment(), R.drawable.icon_mgnz_press,
//                R.drawable.icon_mgnz));
//        menus.add(new MenuBean("图书", R.drawable.icon_book, new BookShopFragment(),
//                R.drawable.icon_book_press, R.drawable.icon_book));
//        menus.add(new MenuBean("文库", R.drawable.icon_search,
//                new SearchFragment(), R.drawable.icon_search_press,
//                R.drawable.icon_search));

        ConstantsAmount.MENUBEANLIST.add((new MenuBean("文库", R.drawable.icon_search,
                new SearchFragment(), R.drawable.icon_search_press,
                R.drawable.icon_search)));

        /*menu_r.add(new MenuBeanRight("收藏", R.drawable.icon_fav, new FavActivity(),
                R.drawable.icon_fav_press, R.drawable.icon_fav));
        menu_r.add(new MenuBeanRight("书架", R.drawable.icon_shelf,
                new OfflineActivity(), R.drawable.icon_shelf_press,
                R.drawable.icon_shelf));
        menu_r.add(new MenuBeanRight("设置", R.drawable.icon_setting,
                new SettingActivity(), R.drawable.icon_setting_press,
                R.drawable.icon_setting));*/

        menu_r.add(new MenuBeanRight("收藏", R.drawable.icon_fav, new FavActivity(),
                R.drawable.icon_fav_press, R.drawable.icon_fav));
        menu_r.add(new MenuBeanRight("书架", R.drawable.icon_shelf,
                new OffLineActivity(), R.drawable.icon_shelf_press,
                R.drawable.icon_shelf));
        menu_r.add(new MenuBeanRight("设置", R.drawable.icon_setting,
                new SettingActivity(), R.drawable.icon_setting_press,
                R.drawable.icon_setting));

        leftMenuAdapter = new MyLeftSlidingListViewAdapter(this, ConstantsAmount.MENUBEANLIST);
        fragmentTransaction.replace(R.id.fl_menu, leftMenuFragment, "LMF");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        FragmentTransaction fragmentTransaction_r = HomeActivity.this
                .getSupportFragmentManager().beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
//        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("unitName", unitName);
        bundle.putString("logoUrl", logoUrl);
        bundle.putString("backgroundUrl", backgroundUrl);

        rightMenuAdapter = new MyRightSlidingListViewAdapter(this, menu_r);
        rightMenuFragment.setArguments(bundle);
        fragmentTransaction_r.replace(R.id.fl_secondary_menu, rightMenuFragment);
        fragmentTransaction_r.commit();
    }

    public OnLeftMenuItemClickListener getOnLeftMenuItemClickListener() {
        return onLeftMenuItemClickListener;
    }

    public OnRightMenuItemClickListener getOnRightMenuItemClickListener() {
        return onRightMenuItemClickListener;
    }

    public MyLeftSlidingListViewAdapter getLeftMenuAdapter() {
        return leftMenuAdapter;
    }

    public MyRightSlidingListViewAdapter getRightMenuAdapter() {
        return rightMenuAdapter;
    }

    public void showMenu(View view) {
        if (mSlidingMenu != null) {
            mSlidingMenu.showMenu();
        }
    }

    public void showRightMenu(View view) {
        if (mSlidingMenu != null) {
            mSlidingMenu.showSecondaryMenu(true);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mSlidingMenu.isMenuShowing()) {
            return super.dispatchKeyEvent(event);
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (!(Utils.isFastDoubleClick())) {
                    ToastUtils.showToastShort(this, "再点一次退出");
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }



    /**
     * 回调接口
     *
     * @author fun陈
     */
    public interface MyTouchListener {
        public void onTouchEvent(MotionEvent event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConstantsAmount.MENUBEANLIST = new ArrayList<MenuBean>();
        ConstantsAmount.GETLATEST_URL = null;
        ConstantsAmount.GETUNITSERVICES_URL = null;
        ConstantsAmount.GETSERVICETICKET_URL = null;
        ConstantsAmount.GETMENUITEM_URL = null;
        ConstantsAmount.MENUPOSITION = 0;
        LyApplication.authToken = null;
//        ConstantsAmount.BASEURL_UNIT = null;
//        ConstantsAmount.UNITBASEURL_REQUESTHEADER = null;
        instance_home = null;
//        try {
//            initEncrypt();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
    }
}
