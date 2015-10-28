package com.longyuan.qm.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.LyApplication;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.MenuBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.fragment.BookShopFragment;
import com.longyuan.qm.utils.FileDES;
import com.longyuan.qm.utils.FileUtil;
import com.longyuan.qm.utils.LoadingDialog;
import com.longyuan.qm.utils.Utils;

import org.geometerplus.android.fbreader.FBReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 2015/1/15.
 */
public class BookDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savaPicPath = SDPATH + "/LYyouyue/";
    private static String GETBASEINFO_URL = null;
    private static String GETBOOKDOWNLOAD_URL = null;
    private BookClassifyBean bookClassifyBean = null;
    private ImageView imageView;
    private TextView bookName, author, bookType, bookPress, bookNote,
            headTitle;
    private Button leftBtn, addBtn, rightBtn;
    private int position = -1;
    private int i = 10;
    private boolean mFlag = false;
    private boolean mSDFlalg = false;
    private BookShopFragment bookShopFragment;
    private String userName = null, bookCode = "", downloadBookUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetail_activity_normal);

        Utils.getHttpRequestHeader();
        GETBASEINFO_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "book/GetBasicInfo?";
        GETBOOKDOWNLOAD_URL = ConstantsAmount.UNITBASEURL_REQUESTHEADER + "book/Download?";
        mContext = BookDetailActivity.this;
        userName = mSp.getString("username", null);

        bookName = (TextView) findViewById(R.id.bkname);
        author = (TextView) findViewById(R.id.textView_auth);
        bookType = (TextView) findViewById(R.id.textView_sub);
        bookPress = (TextView) findViewById(R.id.textView_pub);
        bookNote = (TextView) findViewById(R.id.TextView_detail);
        headTitle = (TextView) findViewById(R.id.head_layout_text);
        imageView = (ImageView) findViewById(R.id.book_cover);
        leftBtn = (Button) findViewById(R.id.head_layout_showLeft);
        addBtn = (Button) findViewById(R.id.addBookShelf);
        rightBtn = (Button) findViewById(R.id.head_layout_showRight);

        leftBtn.setBackgroundResource(R.drawable.button_back_selector);
        leftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.GONE);
        addBtn.setOnClickListener(this);
        init();
        // FIXME 接收广播通知 (0为成功，10为失败，1为下载中)
        LocalBroadcastManager broadcastManager = LocalBroadcastManager
                .getInstance(mContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                Log.e("BroadcastReceiver", extras.getInt("position") + "///");
                switch (extras.getInt("position")) {
                    case 100:
                        addBtn.setClickable(true);
                        addBtn.setText("下载完成，点击阅读");
                        mFlag = true;
                        mSDFlalg = true;
                        break;
                    case 1:
                        break;
                    case 10:
                        Toast.makeText(mContext, "下载失败，请检测网络或sd卡是否安装正确!",
                                Toast.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver,
                intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // 判断所选的图书是否已存在
        List<BookClassifyBean> bookInfo = DataBase.getInstance(mContext)
                .selectFromBookShelfList(userName);

        for (int i = 0; i < bookInfo.size(); i++) {
            BookClassifyBean info = null;
            info = bookInfo.get(i);
            if (bookClassifyBean.getBookName().equals(info.getBookName())) {
                mFlag = true;
            }
        }
        if (FileUtil.checkFileIsExist(savaPicPath + bookClassifyBean.getBookName() + "dec.epub")) {
            for (int i = 0; i < bookInfo.size(); i++) {
                if (bookClassifyBean.getBookPath().equals(
                        bookInfo.get(i).getBookPath())) {
                    if (bookInfo.get(i).getBookIsHasDumped().equals("0")) {
                        addBtn.setText("已添加过此书，点击阅读");
                        mFlag = true;
                        mSDFlalg = true;
                    } else {
                        addBtn.setText("下载中...");
                        addBtn.setClickable(false);
                    }
                }
            }
            if (!mSDFlalg) {
                FileUtil.deleteFile(bookClassifyBean.getBookPath());
                mFlag = false;
            }
            FileUtil.deleteFile(bookClassifyBean.getBookPath());
//            initEncrypt();

        }
    }


    /**
     * (非 Javadoc) Title: init Description:
     *
     * @see com.longyuan.qm.BaseFragment#init()
     */
    @Override
    protected void init() {
        super.init();
        LoadingDialog.showDialog(mContext, "正在加载...");
//        Bundle bundle = getArguments();
//        position = bundle.getInt("position");
//        bookClassifyBean = (BookClassifyBean) bundle.getParcelableArrayList(
//                "mBookClassifyList").get(position);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");
        position = bundle.getInt("position");
        ArrayList<BookClassifyBean> list = (ArrayList<BookClassifyBean>) bundle.getSerializable("bList");
        bookClassifyBean = list.get(position);
        boolean b = bookClassifyBean == null;
        Log.e("", "" + b + "\n" + userName);

        if (bookClassifyBean != null) {
            LoadingDialog.dissmissDialog();
            bookClassifyBean.setUserName(userName);
            String path = GETBASEINFO_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&bookguid=" + bookClassifyBean.getBookGuid();
            HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
            httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
                @Override
                public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                    LoadingDialog.dissmissDialog();
                    try {
                        JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                        JSONObject jo = jsonObject.getJSONObject("Data");
                        bookNote.setText(Utils.replaceHtmlTag(jo.optString("Note")));
                        bookName.setText(jo.optString("BookName"));
                        author.setText(jo.optString("Author"));
                        bookType.setText(bookClassifyBean.getCategory());
                        bookPress.setText(jo.optString("PublishName"));
                        BitmapUtils bitmapUtil = new BitmapUtils(mContext);
                        bitmapUtil
                                .configDefaultLoadingImage(R.drawable.empty_photo_vertical);
                        bitmapUtil
                                .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
                        bitmapUtil.display(imageView, jo.optString("CoverImage"));

                        JSONArray ja = jo.getJSONArray("BookTypes");
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject jo_bookTypes = (JSONObject) ja.get(i);
                            if (jo_bookTypes.optString("BookType").equals("5")) {
                                bookCode = jo_bookTypes.optString("GetCode");
                            }
                        }
                        headTitle.setText("简介");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    LoadingDialog.dissmissDialog();
                }
            });
        } else {
            LoadingDialog.dissmissDialog();
            Toast.makeText(mContext, "加载失败!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (!FileUtil.checkSDCard()) {
            Toast.makeText(mContext, ConstantsAmount.SDCARKERROR,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (FileUtil.checkFileIsExist(bookClassifyBean.getBookPath())) {
            mFlag = true;
        }
        if (!mFlag) {
            if (isInternet()) {

                getDownloadUrl();
                HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
                String target2 = savaPicPath + Utils.string2U8(bookClassifyBean.getBookName()
                );
                httpUtils.download(bookClassifyBean.getBookCover(), target2,
                        true, true, new RequestCallBack<File>() {

                            @Override
                            public void onFailure(HttpException arg0,
                                                  String arg1) {
                                Log.e("DownLoad Image Is Failure",
                                        arg0.toString());
                            }

                            @Override
                            public void onSuccess(ResponseInfo<File> arg0) {
                                bookClassifyBean.setDownloadUrl(downloadBookUrl);
                            }
                        }
                );
                DataBase.getInstance(mContext).addToBookShelfList(
                        bookClassifyBean);
            } else {
                Toast.makeText(mContext,
                        ConstantsAmount.BAD_NETWORK_CONNECTION,
                        Toast.LENGTH_LONG).show();
            }
        } else {
//            FileDES fileDES;
//            File successFile = new File(bookClassifyBean.getBookPath());
//            String bookName = successFile.getName();
//
//            String fileUrl = Environment
//                    .getExternalStorageDirectory()
//                    + "/LYyouyue/"
//                    + bookName;
//            try {
//                fileDES = new FileDES("DSEPUB86");
////                fileDES.DecryptFile(fileUrl, fileUrl + ".dec"); // 解密
////                Log.e("FileDES", "" + fileUrl + ".dec");
//
//                            fileDES.DecryptFile(fileUrl, fileUrl + ".dec"); // 解密
//                            FileUtil.deleteFile(fileUrl);
//                            FileUtil.RenameFile(
//                                    fileUrl + ".dec",
//                                    Environment.getExternalStorageDirectory()
//                                            + "/LYyouyue/"
//                                            + bookClassifyBean.getBookName()
//                                            + ".epub"
//                            );
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            initDecrypt();
            FBReader.startReader(mContext, bookClassifyBean.getBookPath());
        }
    }

    private void getDownloadUrl() {
        LoadingDialog.showDialog(mContext, "解析中...");
        String path = GETBOOKDOWNLOAD_URL + "apptoken=" + Utils.createAppToken() + "&ticket=" + ConstantsAmount.TICKET + "&getCode=" + bookCode;
        HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        httpUtils.send(HttpRequest.HttpMethod.GET, path, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> objectResponseInfo) {
                LoadingDialog.dissmissDialog();
                try {
                    JSONObject jsonObject = new JSONObject(objectResponseInfo.result);
                    if (checkRequestCode(jsonObject).equals("1")) {
                        downloadBookUrl = jsonObject.optString("Data");
                        downloadBook();
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

            @Override
            public void onFailure(HttpException e, String s) {
                LoadingDialog.dissmissDialog();
                Toast.makeText(mContext, ConstantsAmount.BAD_NETWORK_CONNECTION, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadBook() {
        String target = bookClassifyBean.getBookPath();
        HttpUtils http = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        http.download(downloadBookUrl, target, true, true,
                new RequestCallBack<File>() {

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        FileDES fileDES;
                        File successFile = arg0.result;
                        String bookName = successFile.getName();
                        String fileUrl = Environment
                                .getExternalStorageDirectory()
                                + "/LYyouyue/"
                                + bookName;

                        FileUtil.RenameFile(fileUrl, Environment.getExternalStorageDirectory()
                                            + "/LYyouyue/"
                                            + bookClassifyBean.getBookName()
                                            + "dec.epub");

//                        try {
//                            fileDES = new FileDES("DSEPUB86");
//                            fileDES.DecryptFile(fileUrl, fileUrl + ".dec"); // 解密
////                            FileUtil.deleteFile(fileUrl);
//
//                            FileUtil.RenameFile(
//                                    fileUrl + ".dec",
//                                    Environment.getExternalStorageDirectory()
//                                            + "/LYyouyue/"
//                                            + bookClassifyBean.getBookName()
//                                            + ".epub"
//                            );
//
//                            FileUtil.RenameFile(fileUrl, Environment.getExternalStorageDirectory()
//                                    + "/LYyouyue/"
//                                    + bookClassifyBean.getBookName()
//                                    + "dec.epub");
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }

                        DataBase.getInstance(mContext).updateBookState(
                                bookClassifyBean.getBookName(), 0);
                        Intent intent = new Intent(
                                "android.intent.action.CART_BROADCAST");
                        intent.putExtra("position", 100);
                        LocalBroadcastManager.getInstance(mContext)
                                .sendBroadcast(intent);
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                        int p = (int) ((current * 100) / total);
                        addBtn.setText("下载中：" + p + "%");
                        addBtn.setClickable(false);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        addBtn.setClickable(false);
                        DataBase.getInstance(mContext).updateBookState(
                                bookClassifyBean.getBookName(), 1);
                        Intent intent = new Intent(
                                "android.intent.action.CART_BROADCAST");
                        intent.putExtra("position", 1);
                        LocalBroadcastManager.getInstance(mContext)
                                .sendBroadcast(intent);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                        addBtn.setText("下载失败,请检测网络或sd卡是否安装");
                        addBtn.setClickable(false);
                        if (i == 10) {
                            Intent intent = new Intent(
                                    "android.intent.action.CART_BROADCAST");
                            intent.putExtra("position", i);
                            LocalBroadcastManager.getInstance(mContext)
                                    .sendBroadcast(intent);
                        }
                        i++;
                        downloadBook();
                    }
                }
        );
    }
    //FIXME 文件DES加密
    private void initEncrypt() {
        try {
            FileDES fileDES;
            fileDES = new FileDES("DSEPUB86");
            fileDES.doEncryptFile(bookClassifyBean.getBookPath(), bookClassifyBean.getBookPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //FIXME 文件DES解密
    private void initDecrypt() {
        try {
            FileDES fileDES;
            fileDES = new FileDES("DSEPUB86");
            fileDES.DecryptFile(Environment.getExternalStorageDirectory()
                    + "/LYyouyue/"
                    + bookClassifyBean.getBookName()
                    + "dec.epub", bookClassifyBean.getBookPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
