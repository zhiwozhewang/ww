package com.longyuan.qm.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.longyuan.qm.bean.BookClassifyBean;
import com.longyuan.qm.bean.ChildListData;
import com.longyuan.qm.bean.GroupListData;
import com.longyuan.qm.bean.MagazineDetailListBean;
import com.longyuan.qm.bean.MagazineReaderBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//懒汉式单例类.在第一次调用的时候实例化 

public class DataBase {

    private static DataBase single = null;
    private static DatabaseHelper dbHelper = null;
    private Context context;
    private String dba = "longyuan_qm.db";

    private DataBase(Context c) {
        context = c;
        if (dbHelper == null)
            dbHelper = new DatabaseHelper(this.context, dba, null, 1);
    }

    public synchronized static DataBase getInstance(Context c) {
        if (single == null || dbHelper == null) {
            single = new DataBase(c);
        }
        return single;
    }

    public synchronized void delAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.execSQL("delete from CategoryList;");
            db.execSQL("delete from articlelist;");
            db.execSQL("delete from article;");
            db.execSQL("delete from collection;");
            db.execSQL("delete from submagazinelist;");
            db.execSQL("delete from defaultlist;");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public synchronized void delWhenVip() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        try {
            db.execSQL("delete from article;");
            db.execSQL("delete from collection;");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * 杂志部分
     */
    public synchronized void addMagazineList(List<Map<String, Object>> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                String mid = (String) list.get(i).get("MagazineGUID");
                String title = (String) list.get(i).get("MagazineName");

                String year, issue, cover;
                year = (String) list.get(i).get("Year");
                issue = (String) list.get(i).get("Issue");
                // }
                String content = year + "-" + issue;
                String img = (String) list.get(i).get("IconList");
                cover = (String) list.get(i).get("CoverPicList");

                String sql = "replace into magazinelist(_id,title, mid,summ,type,img,cover) values("
                        + System.currentTimeMillis()
                        + ","
                        + "'"
                        + title
                        + "','"
                        + mid
                        + "','"
                        + content
                        + "',"
                        + 0
                        + ",'"
                        + img
                        + "','" + cover + "');";

                db.execSQL(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // 杂志目录添加到数据库 （在线阅读）（上拉显示下一篇，下拉显示上一篇）
    public synchronized void addMagDirectoryMagazineList(
            List<GroupListData> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String delsql = "delete from magazinedirectorylist";
            db.execSQL(delsql);
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(i).getList().size(); j++) {
                    String tid = list.get(i).getList().get(j).getTitleID();
                    String title = list.get(i).getList().get(j).getTitle();
                    String summ = list.get(i).getList().get(j)
                            .getIntroduction();
                    String type = null;
                    if (list.get(i).getList().get(j).getCategoryCode() == null) {
                        type = "";
                    } else {
                        type = list.get(i).getList().get(j).getCategoryCode();
                    }
                    String img = list.get(i).getList().get(j)
                            .getArticleImgList();
                    String mname = list.get(i).getList().get(j)
                            .getMagazineName();
                    String date = list.get(i).getList().get(j)
                            .getPubStartDate();
                    String author = list.get(i).getList().get(j).getAuthor();

                    String width, height;

                    width = list.get(i).getList().get(j).getArticleImgWidth();
                    height = list.get(i).getList().get(j).getArticleImgHeight();

                    int year = 0;
                    int issue = 0;
                    String logo = list.get(i).getList().get(j)
                            .getMagazineLogo();

                    if (list.get(i).getList().get(j).getYear() != null)
                        year = Integer.parseInt(list.get(i).getList().get(j)
                                .getYear());
                    if (list.get(i).getList().get(j).getIssue() != null)
                        issue = Integer.parseInt(list.get(i).getList().get(j)
                                .getIssue());
                    String sql = "replace into magazinedirectorylist(title, tid,summ,type,img,mname,date,author,year,issue,logo,width,height) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

                   /* Log.e("addMagDirectoryMagazineList", "\n" + title + "\n" + tid + "\n" + summ + "\n" + type + "\n" + img + "\n" + mname + "\n" + date + "\n" + author
                            + "\n" + year + "\n" + issue + "\n" + logo + "\n" + width + "\n" + height);*/

                    Object[] mValue = new Object[]{title, tid, summ, type,
                            img, mname, date, author, year, issue, logo, width,
                            height};
                    db.execSQL(sql, mValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 离线杂志目录添加到数据库（上拉显示下一篇，下拉显示上一篇）
    public synchronized void addOfflineMagDirectoryMagazineList(
            List<GroupListData> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.get(i).getList().size(); j++) {

                    String tid = list.get(i).getList().get(j).getTitleID();
                    String title = list.get(i).getList().get(j).getTitle();
                    String column = list.get(i).getList().get(j).getColumn();
                    String summ = list.get(i).getList().get(j)
                            .getIntroduction();
                    String type = null;
                    if (list.get(i).getList().get(j).getCategoryCode() == null) {
                        type = "";
                    } else {
                        type = list.get(i).getList().get(j).getCategoryCode();
                    }
                    String img = list.get(i).getList().get(j)
                            .getArticleImgList();
                    String mname = list.get(i).getList().get(j)
                            .getMagazineName();
                    String date = list.get(i).getList().get(j)
                            .getPubStartDate();
                    String author = list.get(i).getList().get(j).getAuthor();

                    String width, height;

                    width = list.get(i).getList().get(j).getArticleImgWidth();
                    height = list.get(i).getList().get(j).getArticleImgHeight();

                    int year = 0;
                    int issue = 0;
                    String logo = list.get(i).getList().get(j)
                            .getMagazineLogo();

                    if (list.get(i).getList().get(j).getYear() != null)
                        year = Integer.parseInt(list.get(i).getList().get(j)
                                .getYear());
                    if (list.get(i).getList().get(j).getIssue() != null)
                        issue = Integer.parseInt(list.get(i).getList().get(j)
                                .getIssue());
//                    Log.e("addOfflineMagDirectoryMagazineList : ", "\n" + title + "\n" + tid + "\n" + column + "\n" + summ + "\n" + type
//                            + "\n" + img + "\n" + mname + "\n" + date + "\n" + author + "\n" + year + "\n" + issue + "\n" + logo);
                    String sql = "insert into magazineofflinedirectorylist(title, tid, column, summ,type, img, mname, date, author, year, issue, logo, width, height) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                    Object[] mValue = new Object[]{title, tid, column, summ,
                            type, img, mname, date, author, year, issue, logo,
                            width, height};
                    db.execSQL(sql, mValue);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 查询离线杂志目录数据
    public synchronized List<ChildListData> selectAllFromOfflineMagDirectory(
            String magName, String issue, String year) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<ChildListData> list = null;
        try {
            list = new ArrayList<ChildListData>();
            ChildListData childListData = null;

            Cursor cursor = db.rawQuery(
                    "SELECT title,tid,column,mname,issue,year FROM magazineofflinedirectorylist "
                            + "WHERE mname='" + magName + "' AND issue = '"
                            + issue + "' AND year = '" + year + "'", null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    childListData = new ChildListData();

                    String mTitle = cursor.getString(cursor
                            .getColumnIndex("title"));
                    String mTitleid = cursor.getString(cursor
                            .getColumnIndex("tid"));
                    String mName = cursor.getString(cursor
                            .getColumnIndex("mname"));
                    String mColumn = cursor.getString(cursor
                            .getColumnIndex("column"));
                    String mIssue = cursor.getString(cursor
                            .getColumnIndex("issue"));
                    String mYear = cursor.getString(cursor
                            .getColumnIndex("year"));

                    childListData.setTitle(mTitle);
                    childListData.setTitleID(mTitleid);
                    childListData.setMagazineName(mName);
                    childListData.setColumn(mColumn);
                    childListData.setIssue(mIssue);
                    childListData.setYear(mYear);

                    list.add(childListData);
//                    Log.e("", "\n" + mTitle );
                }
                Log.e("", "\n" + magName + "\n" + issue + "\n" +year);

            } else {
                Log.e("selectAllFromOfflineMagDirectory:离线杂志目录数据", "null");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // FIXME 杂志目录已读标记（在线阅读）
    public synchronized void updateToMagDirectoryArticleList(String type,
                                                             String titleId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "update magazinedirectorylist set type='" + type
                    + "' where tid = '" + titleId + "'";
            db.execSQL(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 查询已添加离线杂志架的杂志
    public synchronized List<MagazineDetailListBean> selectFromofflineList(
            String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<MagazineDetailListBean> list = new ArrayList<MagazineDetailListBean>();
        MagazineDetailListBean detailList = null;
        try {
            Cursor cursor = db.rawQuery(
                    "select mname,mid,cover,issue,year from offlinemagazine where user_name = '"
                            + username + "'", null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    detailList = new MagazineDetailListBean();
                    String mName = cursor.getString(cursor
                            .getColumnIndex("mname"));
                    String mId = cursor.getString(cursor.getColumnIndex("mid"));
                    String mCover = cursor.getString(cursor
                            .getColumnIndex("cover"));
                    String mIssue = cursor.getString(cursor
                            .getColumnIndex("issue"));
                    String mYear = cursor.getString(cursor
                            .getColumnIndex("year"));

                    detailList.setMagazineName(mName);
                    detailList.setMagazineId(mId);
                    detailList.setCover(mCover);
                    detailList.setIssue(mIssue);
                    detailList.setYear(mYear);

                    list.add(detailList);
                }
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // 查询杂志已读 (在线阅读)
    public synchronized List<String> selectFromMagDirectoryByType() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db
                    .rawQuery(
                            "select tid,type from magazinedirectorylist where type = 1",
                            null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    cursor.getString(cursor.getColumnIndex("type"));

                    String tid = cursor.getString(cursor.getColumnIndex("tid"));
                    list.add(tid);
                }
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // FIXME 离线杂志架数据
    public synchronized void addOfflineMagazineList(MagazineDetailListBean list,
                                                    String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String magazineName = list.getMagazineName();
            String magazineId = list.getMagazineId();
            String cover = list.getCover();
            String issue = list.getIssue();
            String year = list.getYear();

            String sql = "replace into offlinemagazine(mname, mid, cover, issue, year, user_name) values(?,?,?,?,?,?)";

//            Log.e("", "" + magazineName + " / " + magazineId + " / " + cover + " / " + issue + "/" + year);

            Object[] mValue = new Object[]{magazineName, magazineId, cover,
                    issue, year, username};
            db.execSQL(sql, mValue);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 离线杂志架阅读内容数据
    public synchronized void addOfflineMagazineReaderList(
            MagazineReaderBean list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String mname = list.getMagazineName();
            String title = list.getTitle();
            String titleid = list.getTitleid();
            String content = list.getContent();
            String issue = list.getIssue();
            String year = list.getYear();
            String author = list.getAuthor();
            String previousTitleid = list.getPreviousTitleid();
            String nextTitleid = list.getNextTitleid();
            String type = "0";
            if (author.equals("") || author == null) {
                author = "";
            }

            String sql = "replace into offlinemagazineReaderlist(title, tid, type, mname, content, issue, year, author, beforetid, nexttid) values(?,?,?,?,?,?,?,?,?,?)";

            Object[] mValue = new Object[]{title, titleid, type, mname,
                    content, issue, year, author, previousTitleid, nextTitleid};
            db.execSQL(sql, mValue);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 查询离线杂志架阅读内容数据
    public synchronized List<MagazineReaderBean> selectFromOfflineMagazineReaderList(
            String titleid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<MagazineReaderBean> list = new ArrayList<MagazineReaderBean>();
        MagazineReaderBean info = null;
        try {
            Cursor cursor = db
                    .rawQuery(
                            "select title,tid,mname,content,issue,year,author,beforetid,nexttid from offlinemagazinereaderlist where tid = '"
                                    + titleid + "'", null
                    );
            while (cursor.moveToNext()) {
                info = new MagazineReaderBean();
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String tid = cursor.getString(cursor.getColumnIndex("tid"));
                String mname = cursor.getString(cursor.getColumnIndex("mname"));
                String content = cursor.getString(cursor
                        .getColumnIndex("content"));
                String issue = cursor.getString(cursor.getColumnIndex("issue"));
                String year = cursor.getString(cursor.getColumnIndex("year"));
                String author = cursor.getString(cursor
                        .getColumnIndex("author"));
                String beforetid = cursor.getString(cursor
                        .getColumnIndex("beforetid"));
                String nexttid = cursor.getString(cursor
                        .getColumnIndex("nexttid"));

                info.setTitle(title);
                info.setTitleid(tid);
                info.setMagazineName(mname);
                info.setContent(content);
                info.setIssue(issue);
                info.setYear(year);
                info.setAuthor(author);
                info.setPreviousTitleid(beforetid);
                info.setNextTitleid(nexttid);
                list.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // FIXME 离线杂志目录已读标记
    public synchronized void updateToOfflineMagDirectoryArticleList(
            String type, String titleid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "update offlinemagazineReaderlist set type='" + type
                    + "' where tid = '" + titleid + "'";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 查询离线杂志已读 return List<String>
    public synchronized List<String> selectFromOfflineMagDirectoryByType() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db
                    .rawQuery(
                            "select tid,type from offlinemagazineReaderlist where type = 1",
                            null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    cursor.getString(cursor.getColumnIndex("type"));
                    String tid = cursor.getString(cursor.getColumnIndex("tid"));
                    list.add(tid);
                }
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // FIXME 删除离线目录数据
    public synchronized void deleteFromMagDirectoryList(String mname,
                                                        String issue, String year, String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "delete from magazineofflinedirectorylist where mname= '"
                    + mname
                    + "' and issue = '"
                    + issue
                    + "' and year = '"
                    + year + "'" + "' and user_name = '" + username + "'";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // FIXME 删除离线杂志架中的一本杂志；
    public synchronized void deleteItemFromOfflineList(
            MagazineDetailListBean detailList) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String magazineName = detailList.getMagazineName();
        String year = detailList.getYear();
        String issue = detailList.getIssue();
        try {
            String sql = "delete from offlinemagazine where mname = '"
                    + magazineName + "' and year = '" + year
                    + "' and  issue = '" + issue + "';";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    /**
     * 图书部分
     */

    // 查询离线书架中的数据
    public synchronized List<BookClassifyBean> selectFromBookShelfList(
            String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<BookClassifyBean> list = new ArrayList<BookClassifyBean>();
        BookClassifyBean info = null;
        try {
            Cursor cursor = db.rawQuery(
                    "select * from bookshelflist where username = '" + username
                            + "' order by _id desc", null
            );
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    info = new BookClassifyBean();
                    info.setBookid(cursor.getString(cursor
                            .getColumnIndex("_id")));
                    info.setBookGuid(cursor.getString(cursor
                            .getColumnIndex("bookguid")));
                    info.setBookName(cursor.getString(cursor
                            .getColumnIndex("bookname")));
                    info.setOrderNumber(cursor.getString(cursor
                            .getColumnIndex("ordernumber")));
                    info.setAuthor(cursor.getString(cursor
                            .getColumnIndex("author")));
                    info.setPubDate(cursor.getString(cursor
                            .getColumnIndex("pubdate")));
                    info.setBookPath(cursor.getString(cursor
                            .getColumnIndex("bookpath")));
                    info.setDownloadUrl(cursor.getString(cursor
                            .getColumnIndex("bookdownloadurl")));
                    info.setBookAddTime(cursor.getString(cursor
                            .getColumnIndex("bookaddtime")));
                    info.setBookOpenTime(cursor.getString(cursor
                            .getColumnIndex("bookopentime")));
                    info.setCategory(cursor.getString(cursor
                            .getColumnIndex("bookcategoryname")));
                    info.setBookCover(cursor.getString(cursor
                            .getColumnIndex("bookcover")));
                    info.setBookIsHasDumped(cursor.getString(cursor
                            .getColumnIndex("bookisHasDumped")));
                    info.setBookBeginPosition(cursor.getString(cursor
                            .getColumnIndex("bookbeginposition")));
                    info.setUserName(cursor.getString(cursor
                            .getColumnIndex("username")));
                    list.add(info);
                }
                cursor.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return list;
    }

    // 判断是否有多个用户同时下载该书
    public synchronized boolean selectBookNameFromDdifferentUser(String bookname) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select bookname from bookshelflist", null);
        if (cursor.moveToNext()) {
            Log.e("selectBookNameFromDdifferentUser", "not null");
            return true;
        } else {
            Log.e("selectBookNameFromDdifferentUser", "null");
        }
        return false;
    }

    // 添加到离线书架
    public synchronized void addToBookShelfList(BookClassifyBean list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String bookName = list.getBookName();
            String bookGuid = list.getBookGuid();
            String author = list.getAuthor();
            if (author.equals("") || author == null) {
                author = "";
            }
            String downloadUrl = list.getDownloadUrl();
            String bookPath = list.getBookPath();
            String orderNumber = list.getOrderNumber();
            String opentime = "0";
            String bookAddTime = list.getBookAddTime();
            String pubDate = list.getPubDate();
            String category = list.getCategory();
            String bookCover = list.getBookCover();
            // 图书当前下载状态
            String bookIsHasDumped = "0";
            String bookBeginPosition = list.getBookBeginPosition();
            String userName = list.getUserName();

            String sql = "replace into bookshelflist(bookguid, bookname, ordernumber, author, pubdate, bookpath, bookdownloadurl, bookaddtime, bookopentime, bookcategoryname, bookcover, bookisHasDumped, bookbeginposition, username) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Log.e("addToBookShelfList", bookGuid + "\n" + bookName + "\n"
                    + orderNumber + "\n" + author + "\n" + pubDate + "\n"
                    + bookPath + "\n" + downloadUrl + "\n" + bookAddTime + "\n"
                    + opentime + "\n" + category + "\n" + bookCover + "\n"
                    + bookIsHasDumped + "\n" + bookBeginPosition + "\n"
                    + userName);
            Object[] mValue = new Object[]{bookGuid, bookName, orderNumber,
                    author, pubDate, bookPath, downloadUrl, bookAddTime,
                    opentime, category, bookCover, bookIsHasDumped,
                    bookBeginPosition, userName};
            db.execSQL(sql, mValue);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // 删除书架上的图书
    public synchronized void deleteItemFromBookShelfList(String bookid) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "delete from bookshelflist where _id = '" + bookid
                    + "';";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // 修改图书打开时间
    public synchronized void updateBookOpenTimeList(String bookname, int time) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "update bookshelflist set bookopentime=" + time
                    + " where bookname = '" + bookname + "'";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    // 书架中图书的三种状态(state)：0未下载；1下载中；2下载完成；3最近阅读；
    public synchronized void updateBookState(String bookname, int state) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String sql = "update bookshelflist set bookisHasDumped=" + state
                    + " where bookname = '" + bookname + "'";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
