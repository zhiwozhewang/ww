package com.longyuan.qm.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.utils.DeviceUtils;
import com.longyuan.qm.utils.FileUtil;
import com.longyuan.qm.utils.Utils;

import java.io.File;
import java.util.List;

public class MyBookShelfListAdapter extends BaseAdapter {
    private static final String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savePicPath = SDPATH + "/LYyouyue/";
    private Context mContext;
    private List<BookClassifyBean> bookInfo = null;
    private ViewHolder holder;
    private int[] itemState;

    public MyBookShelfListAdapter(Context context, List<BookClassifyBean> bookList) {
        this.bookInfo = bookList;
        this.mContext = context;
    }

    public List<BookClassifyBean> getBookInfo() {
        return bookInfo;
    }

    public void setBookInfo(List<BookClassifyBean> bookInfo) {
        this.bookInfo = bookInfo;
        itemState = new int[bookInfo.size()];
        init();
    }

    private void init() {
        for (int i = 0; i < bookInfo.size(); i++) {
            itemState[i] = 0;
        }
    }

    @Override
    public int getCount() {
        return bookInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return bookInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.bookshelf_tem_gridview, null);
            holder.mBookCover = (RelativeLayout) convertView
                    .findViewById(R.id.bookshelf_cover);
            holder.mBookName = (TextView) convertView
                    .findViewById(R.id.bookshelf_tvBookName);
            holder.mCover = (ImageView) convertView
                    .findViewById(R.id.bookshelf_imageView_cover);
            holder.mSelectIcon = (ImageView) convertView
                    .findViewById(R.id.bookshelf_FileSelectIcon);
            holder.mBookPro = (TextView) convertView
                    .findViewById(R.id.bookshelf_tvBookProgress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        if (bookInfo.get(position).getBookName() != null)
//            if (returnSuffix(bookInfo.get(position).getBookPath()).contains(
//                    ".txt")) {
//                holder.mBookCover
//                        .setBackgroundResource(R.drawable.listview_txtcover);
//            } else if (returnSuffix(bookInfo.get(position).getBookPath())
//                    .contains(".epub")) {
//
//                if (bookInfo.get(position).getBookCover() != null
//                        && bookInfo.get(position).getBookCover()
//                        .startsWith("http")) {
//
//                } else {
//
//                }
//            } else if (returnSuffix(bookInfo.get(position).getBookPath())
//                    .contains(".html")) {
//                holder.mBookCover
//                        .setBackgroundResource(R.drawable.listview_htmlcover);
//            } else if (returnSuffix(bookInfo.get(position).getBookPath())
//                    .contains(".oeb")) {
//                holder.mBookCover
//                        .setBackgroundResource(R.drawable.listview_oebicon);
//            } else if (returnSuffix(bookInfo.get(position).getBookPath())
//                    .contains(".mobi")) {
//                holder.mBookCover
//                        .setBackgroundResource(R.drawable.listview_mobiicon);
//            } else {
//                holder.mBookCover
//                        .setBackgroundResource(R.drawable.listview_othercover);
//            }
//        else
//            holder.mBookCover
//                    .setBackgroundResource(R.drawable.listview_othercover);

        LayoutParams para = (LayoutParams) holder.mBookCover.getLayoutParams();
        para.topMargin = (position / 3) == 0 ? DeviceUtils.dip2px(mContext,
                (float) 7) : DeviceUtils.dip2px(mContext, (float) 24);
        holder.mBookCover.setLayoutParams(para);

        holder.mBookName.setText(bookInfo.get(position).getBookName());
        holder.mBookPro.setText(bookInfo.get(position).getAuthor());

        if (FileUtil.checkFileIsExist(savePicPath
                + Utils.string2U8(bookInfo.get(position).getBookName()))) {
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
            bitmapUtils
                    .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
            bitmapUtils.display(holder.mCover, savePicPath
                    + Utils.string2U8(bookInfo.get(position).getBookName()));

        } else {
            HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);
            final String target2 = savePicPath + Utils.string2U8(bookInfo.get(position).getBookName()
                   );
            httpUtils.download(bookInfo.get(position).getBookCover(), target2,
                    true, true, new RequestCallBack<File>() {

                        @Override
                        public void onFailure(HttpException arg0, String arg1) {
                            Log.e("DownLoad Image Is Failure", arg0.toString());
                        }

                        @Override
                        public void onSuccess(ResponseInfo<File> arg0) {
                            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
                            bitmapUtils.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
                            bitmapUtils
                                    .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
                            bitmapUtils.display(holder.mCover, target2);
                        }
                    }
            );
        }

        if (!FileUtil.checkFileIsExist(savePicPath + bookInfo.get(position).getBookName() + "dec.epub")) {
            // Log.e("adapter item position", position+"");
            holder.mSelectIcon.setVisibility(View.VISIBLE);
            holder.mSelectIcon.setBackgroundResource(R.drawable.icon_down);
        } else if (bookInfo.get(position).getBookIsHasDumped().equals("1")) {
            // 判断状态是否为正在下载
            holder.mSelectIcon.setVisibility(View.VISIBLE);
            holder.mSelectIcon
                    .setBackgroundResource(R.drawable.icon_downloading);
        } else if (bookInfo.get(position).getBookOpenTime().equals("1")) {
            holder.mSelectIcon.setVisibility(View.VISIBLE);
            holder.mSelectIcon.setBackgroundResource(R.drawable.icon_read);
        } else if (!FileUtil.checkFileIsExist(savePicPath + bookInfo.get(position)
                .getBookName() + "dec.epub")) {
            holder.mSelectIcon.setVisibility(View.VISIBLE);
            holder.mSelectIcon.setBackgroundResource(R.drawable.icon_down);
            // holder.mSelectIcon.setBackgroundResource(R.drawable.checkbox_selected);
        } else {
            holder.mSelectIcon.setVisibility(View.INVISIBLE);
            // holder.mSelectIcon.setBackgroundResource(R.drawable.checkbox_selected);
        }
        return convertView;
    }

    public String returnSuffix(String fileName) {

        if (fileName.lastIndexOf(".") > 0) {
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            return fileSuffix;
        }
        return null;
    }

    public String returnName(String fileName) {

        if (fileName.indexOf(".") > 0) {
            String name = fileName.substring(fileName.indexOf("."));
            return name;
        }
        return null;
    }

    class ViewHolder {
        private TextView mBookName, mBookPro;
        private ImageView mCover, mSelectIcon;
        private RelativeLayout mBookCover;
    }
}