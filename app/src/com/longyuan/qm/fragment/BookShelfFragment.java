package com.longyuan.qm.fragment;

import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
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
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyBookShelfListAdapter;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.utils.FileDES;
import com.longyuan.qm.utils.FileUtil;
import com.longyuan.qm.utils.Utils;

import org.geometerplus.android.fbreader.FBReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BookShelfFragment extends BaseFragment {
    private static final String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savePicPath = SDPATH + "/LYyouyue/";
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 10:
                    break;
                case 0:
                    List<BookClassifyBean> bookInfo2 = DataBase.getInstance(
                            getActivity()).selectFromBookShelfList(userName);
                    adapter.setBookInfo(bookInfo2);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private GridView bookShelf;
    private MyBookShelfListAdapter adapter;
    private List<BookClassifyBean> bookInfo = new ArrayList<BookClassifyBean>();
    private String bookId = null, bookName = null, deleteBookUrl = null, deleteDECBookUrl = null,
            deleteBookImgUrl = null, userName = null;
    private int i = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = LayoutInflater.from(getActivity()).inflate(
                R.layout.bookshelf_activity_normal, null);
        userName = mSp.getString("username", null);
        // 书架默认选中
        bookInfo = DataBase.getInstance(getActivity()).selectFromBookShelfList(
                userName);

        bookShelf = (GridView) mView.findViewById(R.id.bookShelf);

        adapter = new MyBookShelfListAdapter(getActivity(), bookInfo);
        bookShelf.setAdapter(adapter);

        bookShelf.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    final int arg2, long arg3) {
                if (!Environment.MEDIA_MOUNTED.equals(Environment
                        .getExternalStorageState())) {
                    Toast.makeText(getActivity(), "请检测sd卡安装是否正确!",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (FileUtil.checkFileIsExist(savePicPath + bookInfo.get(arg2).getBookName() + "dec.epub")) {
                    for (int i = 0; i < bookInfo.size(); i++) {
                        DataBase.getInstance(getActivity())
                                .updateBookOpenTimeList(
                                        bookInfo.get(i).getBookName(), 0);
                    }
                    DataBase.getInstance(getActivity()).updateBookOpenTimeList(
                            bookInfo.get(arg2).getBookName(), 1);

                    initDecrypt(savePicPath + bookInfo.get(arg2).getBookName());
                    FBReader.startReader(mContext, bookInfo.get(arg2).getBookPath());
//                    startActivity(new Intent(getActivity(), FBReader.class));
                } else {
                    downloadBook(bookInfo.get(arg2));
                }
            }
        });

        bookShelf.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                bookId = bookInfo.get(arg2).getBookid();
                bookName = bookInfo.get(arg2).getBookName();

                deleteBookUrl = Environment.getExternalStorageDirectory()
                        + "/LYyouyue/" + bookInfo.get(arg2).getBookName()
                        + ".epub";

                deleteDECBookUrl = Environment.getExternalStorageDirectory()
                        + "/LYyouyue/" + bookInfo.get(arg2).getBookName()
                        + "dec.epub";

                deleteBookImgUrl = Environment.getExternalStorageDirectory()
                        + "/LYyouyue/" + Utils.string2U8(bookInfo.get(arg2).getBookName()
                );

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
                                        try {
                                            DataBase.getInstance(getActivity())
                                                    .deleteItemFromBookShelfList(
                                                            bookId);
                                            bookInfo = DataBase.getInstance(
                                                    getActivity())
                                                    .selectFromBookShelfList(
                                                            userName);

                                            boolean selectBook = DataBase
                                                    .getInstance(getActivity())
                                                    .selectBookNameFromDdifferentUser(
                                                            bookName);
                                            if (!selectBook) {
                                                FileUtil.deleteFile(deleteBookUrl);
                                                FileUtil.deleteFile(deleteBookImgUrl);
                                                FileUtil.deleteFile(deleteDECBookUrl);
                                            } else {
                                                adapter.setBookInfo(bookInfo);
                                                adapter.notifyDataSetChanged();
                                                return;
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        adapter.setBookInfo(bookInfo);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                        ).show();
                return true;
            }
        });

        // FIXME 接收广播通知 (0为成功，10为失败，1为下载中)
        LocalBroadcastManager broadcastManager = LocalBroadcastManager
                .getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CART_BROADCAST");
        BroadcastReceiver mItemViewListClickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                Log.e("BroadcastReceiver", extras.getInt("position") + "///");
                switch (extras.getInt("position")) {
                    case 100:
                        bookInfo = DataBase.getInstance(getActivity())
                                .selectFromBookShelfList(userName);
                        adapter.setBookInfo(bookInfo);
                        adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        break;
                    case 10:
                        Toast.makeText(getActivity(), "下载失败，请检测网络或sd卡是否安装正确!",
                                Toast.LENGTH_LONG).show();
                        bookInfo = DataBase.getInstance(getActivity())
                                .selectFromBookShelfList(userName);
                        adapter.setBookInfo(bookInfo);
                        adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;
                }
            }
        };
        broadcastManager.registerReceiver(mItemViewListClickReceiver,
                intentFilter);
        return mView;
    }

    //FIXME 文件DES解密
    private void initDecrypt(String bookPath) {
        try {
            FileDES fileDES;
            fileDES = new FileDES("DSEPUB86");
            fileDES.DecryptFile(bookPath + "dec.epub", bookPath + ".epub");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        bookInfo = DataBase.getInstance(getActivity()).selectFromBookShelfList(
                userName);
        adapter.setBookInfo(bookInfo);
        adapter.notifyDataSetChanged();

        for (int i = 0; i < bookInfo.size(); i++) {
            FileUtil.deleteFile(bookInfo.get(i).getBookPath());
        }
    }

    private void downloadBook(final BookClassifyBean bookClassifyInfo) {
        String target = bookClassifyInfo.getBookPath();
        HttpUtils http = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
        http.download(bookClassifyInfo.getDownloadUrl(), target, true, true,
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
                        try {
                            fileDES = new FileDES("DSEPUB86");
                            fileDES.DecryptFile(fileUrl, fileUrl + ".dec"); // 解密
//                            FileUtil.deleteFile(fileUrl);

                            FileUtil.RenameFile(
                                    fileUrl + ".dec",
                                    Environment.getExternalStorageDirectory()
                                            + "/LYyouyue/"
                                            + bookClassifyInfo.getBookName()
                                            + ".epub"
                            );

                            FileUtil.RenameFile(fileUrl, Environment.getExternalStorageDirectory()
                                    + "/LYyouyue/"
                                    + bookClassifyInfo.getBookName()
                                    + "dec.epub");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        DataBase.getInstance(getActivity()).updateBookState(
                                bookClassifyInfo.getBookName(), 0);
                        Intent intent = new Intent(
                                "android.intent.action.CART_BROADCAST");
                        intent.putExtra("position", 100);
                        LocalBroadcastManager.getInstance(getActivity())
                                .sendBroadcast(intent);
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        super.onLoading(total, current, isUploading);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        DataBase.getInstance(getActivity()).updateBookState(
                                bookClassifyInfo.getBookName(), 1);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        Toast.makeText(getActivity(), "下载失败,请检测网络或sd卡是否安装",
                                Toast.LENGTH_LONG).show();
                        downloadBook(bookClassifyInfo);
                        handler.sendEmptyMessage(i++);
                    }
                }
        );
    }
}
