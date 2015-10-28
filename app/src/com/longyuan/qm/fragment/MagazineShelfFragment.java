package com.longyuan.qm.fragment;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.longyuan.qm.BaseFragment;
import com.longyuan.qm.R;
import com.longyuan.qm.activity.OfflineMagazineDirectoryActivity;
import com.longyuan.qm.adapter.MyMagazineShelfListAdapter;
import com.longyuan.qm.bean.MagazineDetailListBean;
import com.longyuan.qm.db.DataBase;
import com.longyuan.qm.utils.FileUtil;
import com.longyuan.qm.utils.Utils;

import java.util.List;

public class MagazineShelfFragment extends BaseFragment {
    private static final String SDPATH = Environment
            .getExternalStorageDirectory().toString();
    private String savaPicPath = SDPATH + "/LYyouyue/";
    private GridView magShelf;
    private List<MagazineDetailListBean> selectFromofflineList = null;
    private String deleteMagImgUrl = "";
    private SharedPreferences preferences = null;
    private String userName = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(
                R.layout.magshelf_activity_normal, null);
        getActivity();
        preferences = getActivity().getSharedPreferences("net",
                Context.MODE_PRIVATE);
        userName = preferences.getString("NAME", "");

        magShelf = (GridView) mView.findViewById(R.id.magShelf);
        selectFromofflineList = DataBase.getInstance(getActivity())
                .selectFromofflineList(userName);
        final MyMagazineShelfListAdapter adapter = new MyMagazineShelfListAdapter(
                getActivity(), selectFromofflineList);
        magShelf.setAdapter(adapter);

        magShelf.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           final int arg2, long arg3) {
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
                                        String magazineName = selectFromofflineList
                                                .get(arg2).getMagazineName();
                                        String year = selectFromofflineList
                                                .get(arg2).getYear();
                                        String issue = selectFromofflineList
                                                .get(arg2).getIssue();
                                        try {
                                            DataBase.getInstance(getActivity())
                                                    .deleteFromMagDirectoryList(
                                                            magazineName,
                                                            issue, year,
                                                            userName);
                                            deleteMagImgUrl = savaPicPath
                                                    + Utils.string2U8(selectFromofflineList
                                                    .get(arg2)
                                                    .getMagazineName()
                                                    + selectFromofflineList
                                                    .get(arg2)
                                                    .getYear()
                                                    + selectFromofflineList
                                                    .get(arg2)
                                                    .getIssue()
                                                    );
//                                            + ".png"
                                            FileUtil.deleteFile(deleteMagImgUrl);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        DataBase.getInstance(getActivity())
                                                .deleteItemFromOfflineList(
                                                        selectFromofflineList
                                                                .get(arg2)
                                                );
                                        selectFromofflineList = DataBase
                                                .getInstance(getActivity())
                                                .selectFromofflineList(userName);
                                        adapter.setmagInfo(selectFromofflineList);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                        ).show();
                return true;
            }
        });

        magShelf.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String magazineName = selectFromofflineList.get(arg2)
                        .getMagazineName();
                String issue = selectFromofflineList.get(arg2).getIssue();
                String year = selectFromofflineList.get(arg2).getYear();

                Log.e("",  "magShelf//" + magazineName + "\n" + issue + "\n" + year);

                Intent intent = new Intent(getActivity(),
                        OfflineMagazineDirectoryActivity.class);
                intent.putExtra("magazineName", magazineName);
                intent.putExtra("issue", issue);
                intent.putExtra("year", year);
                startActivity(intent);
            }
        });
        return mView;
    }
}
