package com.longyuan.qm.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.longyuan.qm.BaseActivity;
import com.longyuan.qm.R;
import com.longyuan.qm.adapter.MyOfflineMagazineDirectoryAdapter;
import com.longyuan.qm.bean.ChildListData;
import com.longyuan.qm.bean.GroupListData;
import com.longyuan.qm.db.DataBase;

import java.util.ArrayList;
import java.util.List;

public class OfflineMagazineDirectoryActivity extends BaseActivity {
    private TextView headtitle, headnumber;
    private ImageView mCloseImg;
    private ExpandableListView expandableListView;
    private String year, issue, magazineName;
    private MyOfflineMagazineDirectoryAdapter adapter = null;
    private List<GroupListData> groupListFinal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.magazinedetail_directory_fragment);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 强制竖屏
        groupListFinal = getData();

        headtitle = (TextView) findViewById(R.id.mag_directory_headtitle);
        headnumber = (TextView) findViewById(R.id.mag_directory_headnumber);
        expandableListView = (ExpandableListView) findViewById(R.id.mag_directory_listview);
        mCloseImg = (ImageView) findViewById(R.id.mag_directory_headcloseimg);

        adapter = new MyOfflineMagazineDirectoryAdapter(
                OfflineMagazineDirectoryActivity.this, groupListFinal);
        expandableListView.setAdapter(adapter);
        headtitle.setText(magazineName);
        headnumber.setText(year + "年第" + issue + "期");
        mCloseImg.setBackgroundResource(R.drawable.mag_directory_closebtn);
        // groupList默认展开的方法：
        int groupCount = expandableListView.getCount();
        for (int i = 0; i < groupCount; i++) {
            expandableListView.expandGroup(i);
        }

        mCloseImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        expandableListView.setGroupIndicator(null);// 去掉向下的箭头;

        // 覆盖groupList的点击事件;
        expandableListView.setOnGroupClickListener(new OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true;
            }
        });

        expandableListView.setOnChildClickListener(new OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TextView textView = (TextView) v.findViewById(R.id.child_tv);
                // textView.setTextColor(Color.RED);

                Intent it = new Intent(OfflineMagazineDirectoryActivity.this,
                        OfflineMagazineReaderActivity.class);
                it.putExtra("RESTYPE", 1);
                it.putExtra("MAGAZINEID", "");
                it.putExtra("ISMAGAZINEREAD", false);
                it.putExtra("READFROM", 0);
                it.putExtra("title_id", groupListFinal.get(groupPosition)
                        .getList().get(childPosition).getTitleID());

                DataBase.getInstance(OfflineMagazineDirectoryActivity.this)
                        .updateToOfflineMagDirectoryArticleList(
                                "1",
                                groupListFinal.get(groupPosition).getList()
                                        .get(childPosition).getTitleID()
                        );

                List<String> selectFromMagDirectoryByType = DataBase
                        .getInstance(OfflineMagazineDirectoryActivity.this)
                        .selectFromOfflineMagDirectoryByType();

                adapter.setgList(groupListFinal);
                adapter.setChildIsReadList(selectFromMagDirectoryByType);
                adapter.notifyDataSetChanged();
                startActivity(it);
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (groupListFinal != null) {
            List<String> selectFromMagDirectoryByType = DataBase.getInstance(
                    OfflineMagazineDirectoryActivity.this)
                    .selectFromOfflineMagDirectoryByType();
            adapter.setgList(groupListFinal);
            adapter.setChildIsReadList(selectFromMagDirectoryByType);
            adapter.notifyDataSetChanged();
        }
    }

    private List<GroupListData> getData() {
        Intent intent = this.getIntent();
        magazineName = intent.getStringExtra("magazineName");
        issue = intent.getStringExtra("issue");
        year = intent.getStringExtra("year");

        List<ChildListData> childListData = DataBase.getInstance(
                OfflineMagazineDirectoryActivity.this)
                .selectAllFromOfflineMagDirectory(magazineName, issue, year);

        Log.e("childListData", "//" + childListData.size());

        List<GroupListData> groupList = new ArrayList<GroupListData>();
        GroupListData groupListData = null;

        for (int i = 0; i < childListData.size(); i++) {
            groupListData = new GroupListData();
            groupListData.setColumn(childListData.get(i).getColumn());
            Log.e("", childListData.get(i).getColumn());
            groupList.add(groupListData);
        }

        // 去重复，将子集合添加到父集合中！
        for (int j = 0; j < groupList.size(); j++) {
            for (int k = groupList.size() - 1; k > j; k--) {
                if (groupList.get(j).getColumn()
                        .equals(groupList.get(k).getColumn())) {
                    groupList.remove(j);
                }
            }
        }

        for (int m = 0; m < groupList.size(); m++) {
            List<ChildListData> addlist = new ArrayList<ChildListData>();
            for (int n = 0; n < childListData.size(); n++) {
                if (childListData.get(n).getColumn()
                        .equals(groupList.get(m).getColumn())) {
                    addlist.add(childListData.get(n));
                }
            }
            groupList.get(m).setList(addlist);
        }
        return groupList;
    }
}
