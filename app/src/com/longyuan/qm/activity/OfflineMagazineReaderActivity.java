package com.longyuan.qm.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.Constance;
import com.longyuan.qm.bean.MagazineReaderBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.utils.DeviceUtils;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase.Mode;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.longyuan.qm.widget.pulltorefresh.PullToRefreshScrollView;

import java.util.List;

public class OfflineMagazineReaderActivity extends BaseActivity {
    private TextView magreader_issue, magreader_name, magreader_title,
            magreader_author;
    private Button fontSize, fav_button;
    private PullToRefreshScrollView mPullRefreshScrollView;
    private WebView webView;
    private WebSettings wb;
    private boolean isBig = false;
    private MagazineReaderBean magazineReaderInfo = new MagazineReaderBean();
    private List<MagazineReaderBean> listData;
    private ScrollView mScrollView;
    private String titleid = "";
    private int resType = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magazinereader);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 强制竖屏

        magreader_issue = (TextView) findViewById(R.id.magreader_issue);
        magreader_name = (TextView) findViewById(R.id.magreader_name);
        magreader_title = (TextView) findViewById(R.id.magreader_title);
        magreader_author = (TextView) findViewById(R.id.magreader_author);
        webView = (WebView) findViewById(R.id.webView1);
        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);

        fontSize = (Button) this.findViewById(R.id.magreader_button_fontsize);
        fav_button = (Button) this.findViewById(R.id.magreader_fav_button);
        fav_button.setVisibility(View.GONE);

        mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setMode(Mode.DISABLED);
        // FIXME 获取ScrollView布局;
        mScrollView = mPullRefreshScrollView.getRefreshableView();
        /*mPullRefreshScrollView
                .setOnRefreshListener(new OnRefreshListener2<ScrollView>() {
                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        if (listData.get(0).getPreviousTitleid().equals("")) {
                            Toast.makeText(OfflineMagazineReaderActivity.this,
                                    "已是第一篇", Toast.LENGTH_LONG).show();
                        } else {
                            getData(listData.get(0).getPreviousTitleid());
                            mScrollView.scrollTo(0, 0);
                            DataBase.getInstance(
                                    OfflineMagazineReaderActivity.this)
                                    .updateToOfflineMagDirectoryArticleList(
                                            "1",
                                            listData.get(0)
                                                    .getPreviousTitleid()
                                    );
                        }
                        mPullRefreshScrollView.onRefreshComplete();
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ScrollView> refreshView) {
                        if (listData.get(0).getNextTitleid().equals("")) {
                            Toast.makeText(OfflineMagazineReaderActivity.this,
                                    "已是最后一篇", Toast.LENGTH_SHORT).show();
                            LoadingDialog.dissmissDialog();
                        } else {
                            getData(listData.get(0).getNextTitleid());
                            mScrollView.scrollTo(0, 0);
                            DataBase.getInstance(
                                    OfflineMagazineReaderActivity.this)
                                    .updateToOfflineMagDirectoryArticleList(
                                            "1",
                                            listData.get(0).getNextTitleid());
                        }
                        mPullRefreshScrollView.onRefreshComplete();
                    }
                });*/

        wb = webView.getSettings();
        wb.setJavaScriptEnabled(true);
        // 图片过大时显示适应屏幕的压缩版
        wb.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        Intent intent = getIntent();
        resType = intent.getIntExtra("RESTYPE", 1);
        titleid = intent.getStringExtra("title_id");
        getData(titleid);
    }

    private void initView(List<MagazineReaderBean> list) {
        if (list.size() > 0) {
            magreader_issue.setText("NO." + list.get(0).getIssue() + "."
                    + list.get(0).getYear());
            magreader_name.setText(list.get(0).getMagazineName());
            magreader_title.setText(list.get(0).getTitle());
            if (list.get(0).getTitle().equals("")) {
                magreader_author.setVisibility(View.INVISIBLE);
            } else {
                magreader_author.setText(list.get(0).getAuthor());
                magreader_author.setVisibility(View.VISIBLE);
            }
            String htmlContent = Utils.getHtmlData(list.get(0).getContent(), "");
            webView.loadDataWithBaseURL("", htmlContent, "text/html", "utf-8",
                    "");
        }
    }

    private void getData(String titleid) {
        listData = DataBase.getInstance(OfflineMagazineReaderActivity.this)
                .selectFromOfflineMagazineReaderList(titleid);
        initView(listData);
    }

    public void onfontSizeClick(View v) {
        this.changeTextSize();
    }

    public void onBackClick(View v) {
        setRightOut(true);
        finish();
    }

    public void onFavoriteClick(View v) {
        toFav();
    }

    private void changeTextSize() {
        if (!isBig) {
            fontSize.setBackgroundResource(R.drawable.button_font_selector_press);
            isBig = true;
        } else {
            fontSize.setBackgroundResource(R.drawable.button_font_selector);
            isBig = false;
        }

        if (isBig) {
            wb.setDefaultFontSize(Constance.MAX_TEXT_SIZE);
        } else {
            wb.setDefaultFontSize(Constance.MIN_TEXT_SIZE);
        }

        webView.postInvalidate();

        DeviceUtils.setSettingBooleanValue("FONTSIZE", isBig,
                OfflineMagazineReaderActivity.this);
    }

    private void toFav() {
        LoadingDialog.showDialog(mContext, "正在加载...");
        magazineReaderInfo.setRestype(resType);
        if (resType == 9)
            magazineReaderInfo.setRestype(3);
        Log.e("tofav:titleid", listData.get(0).getTitle() + "\n"
                + listData.get(0).getTitleid());
    }
}