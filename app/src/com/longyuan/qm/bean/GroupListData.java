package com.longyuan.qm.bean;

import java.util.List;

public class GroupListData {
    private String Column;
    private List<ChildListData> list;

    public String getColumn() {
        return Column;
    }

    public void setColumn(String column) {
        Column = column;
    }

    public List<ChildListData> getList() {
        return list;
    }

    public void setList(List<ChildListData> list) {
        this.list = list;
    }
}
