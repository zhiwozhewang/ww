package com.longyuan.qm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.longyuan.qm.R;
import com.longyuan.qm.bean.GroupListData;

import java.util.List;

public class MyOfflineMagazineDirectoryAdapter extends BaseExpandableListAdapter {
    private List<GroupListData> gList;
    private Context mContext;
    private List<String> childIsReadList;

    public MyOfflineMagazineDirectoryAdapter(Context context,
                                             List<GroupListData> groupList) {
        this.mContext = context;
        this.gList = groupList;
    }

    public MyOfflineMagazineDirectoryAdapter(Context context,
                                             List<GroupListData> groupList, List<String> IsReadList) {
        this.mContext = context;
        this.gList = groupList;
        this.childIsReadList = IsReadList;

    }

    public List<String> getChildIsReadList() {
        return childIsReadList;
    }

    public void setChildIsReadList(List<String> childIsReadList) {
        this.childIsReadList = childIsReadList;
    }

    public List<GroupListData> getgList() {
        return gList;
    }

    public void setgList(List<GroupListData> gList) {
        this.gList = gList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return gList.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ViewChildHolder cHolder = null;
        if (convertView == null) {
            cHolder = new ViewChildHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.magazinedirectory_exitem_child, null);
            cHolder.ctv = (TextView) convertView.findViewById(R.id.child_tv);
            convertView.setTag(cHolder);
        } else {
            cHolder = (ViewChildHolder) convertView.getTag();
        }

        cHolder.ctv.setText(gList.get(groupPosition).getList()
                .get(childPosition).getTitle());
        cHolder.ctv.setTextColor(Color.parseColor("#666666"));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return gList.get(groupPosition).getList().size();
    }

    // group method stub
    @Override
    public Object getGroup(int groupPosition) {
        return gList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return gList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ViewGroupHolder gHolder = null;
        if (convertView == null) {
            gHolder = new ViewGroupHolder();
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.magazinedirectory_exitem_group, null);
            gHolder.gtv = (TextView) convertView.findViewById(R.id.group_tv);
            convertView.setTag(gHolder);
        } else {
            gHolder = (ViewGroupHolder) convertView.getTag();
        }
        gHolder.gtv.setText(gList.get(groupPosition).getColumn());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class ViewGroupHolder {
        private TextView gtv;
    }

    static class ViewChildHolder {
        private TextView ctv;
    }
}
