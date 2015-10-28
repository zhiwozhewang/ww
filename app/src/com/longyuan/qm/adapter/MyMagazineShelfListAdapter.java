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
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.longyuan.qm.ConstantsAmount;
import com.longyuan.qm.R;
import com.longyuan.qm.bean.MagazineDetailListBean;
import com.longyuan.qm.utils.FileUtil;
import com.longyuan.qm.utils.Utils;

import java.io.File;
import java.util.List;

public class MyMagazineShelfListAdapter extends BaseAdapter {
    private static String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savaPicPath = SDPATH + "/LYyouyue/";
    private Context mContext;
    private List<MagazineDetailListBean> magInfo = null;
    private ViewHolder holder;
    private int[] itemState;

    public MyMagazineShelfListAdapter(Context context,
                                      List<MagazineDetailListBean> magList) {
        this.magInfo = magList;
        this.mContext = context;
    }

    public List<MagazineDetailListBean> getmagInfo() {
        return magInfo;
    }

    public void setmagInfo(List<MagazineDetailListBean> magInfo) {
        this.magInfo = magInfo;
        itemState = new int[magInfo.size()];
        init();
    }

    private void init() {
        for (int i = 0; i < magInfo.size(); i++) {
            itemState[i] = 0;
        }
    }

    @Override
    public int getCount() {
        return magInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return magInfo.get(position);
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
                    R.layout.magshelf_tem_gridview, null);
            holder.mMagCover = (RelativeLayout) convertView
                    .findViewById(R.id.magshelf_cover);
            holder.mMagName = (TextView) convertView
                    .findViewById(R.id.magshelf_tvMagName);
            holder.mCover = (ImageView) convertView
                    .findViewById(R.id.magshelf_imageView_cover);
            holder.mSelectIcon = (ImageView) convertView
                    .findViewById(R.id.magshelf_FileSelectIcon);
            holder.mMagPro = (TextView) convertView
                    .findViewById(R.id.magshelf_tvMagProgress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.mMagName.setText(magInfo.get(position).getMagazineName());
        holder.mMagPro.setText(magInfo.get(position).getYear() + "年第"
                + magInfo.get(position).getIssue() + "期");

        if (FileUtil.checkFileIsExist(savaPicPath
                + Utils.string2U8(magInfo.get(position).getMagazineName()
                + magInfo.get(position).getYear()
                + magInfo.get(position).getIssue()))) {
//             + ".png"
            BitmapUtils bitmapUtils = new BitmapUtils(mContext);
            bitmapUtils.configDefaultLoadingImage(R.drawable.empty_photo_vertical);
            bitmapUtils
                    .configDefaultLoadFailedImage(R.drawable.empty_photo_vertical);
            bitmapUtils.display(holder.mCover, savaPicPath
                    + Utils.string2U8(magInfo.get(position).getMagazineName()
                    + magInfo.get(position).getYear()
                    + magInfo.get(position).getIssue()));
//             + ".png"
        } else {
            holder.mCover.setImageResource(R.drawable.empty_photo_vertical);
            final String target2 = savaPicPath
                    + Utils.string2U8(magInfo.get(position).getMagazineName()
                    + magInfo.get(position).getYear()
                    + magInfo.get(position).getIssue());
//             + ".png"

            HttpUtils httpUtils = new HttpUtils(ConstantsAmount.DEFAULT_CONN_TIMEOUT);

            httpUtils.download(magInfo.get(position).getCover(), target2, true,
                    true, new RequestCallBack<File>() {

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

        return convertView;
    }

    static class ViewHolder {
        private TextView mMagName, mMagPro;
        private ImageView mCover, mSelectIcon;
        private RelativeLayout mMagCover;
    }
}