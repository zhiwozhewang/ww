package com.longyuan.qm.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * SQLiteOpenHelper是一个辅助类，用来管理数据库的创建和版本他，它提供两个方面的功能
 * 第一，getReadableDatabase()、getWritableDatabase
 * ()可以获得SQLiteDatabase对象，通过该对象可以对数据库进行操作
 * 第二，提供了onCreate()、onUpgrade()两个回调函数，允许我们再创建和升级数据库时，进行自己的操作
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    /**
     * 在SQLiteOpenHelper的子类当中，必须有该构造函数
     *
     * @param context 上下文对象
     * @param name    数据库名称
     * @param factory
     * @param version 当前数据库的版本，值必须是整数并且是递增的状态
     */
    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        // 必须通过super调用父类当中的构造函数
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    // 该函数是在第一次创建的时候执行，实际上是第一次得到SQLiteDatabase对象的时候才会调用这个方法
    @Override
    public void onCreate(SQLiteDatabase db) {
        HashMap<String, String> map = new HashMap<String, String>();
        Cursor cursor = db
                .rawQuery(
                        "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name;",
                        null);
        while (cursor.moveToNext()) {
            map.put(cursor.getString(0), "==");
        }
        cursor.close();

//		if (map.get("CategoryList") == null)
//			db.execSQL("create table CategoryList(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,  cid TEXT NOT NULL,sortid INTEGER NOT NULL)");
        // 存放杂志图片
//		if (map.get("Magazines") == null)
//			db.execSQL("create table Magazines(mid TEXT PRIMARY KEY, name TEXT NOT NULL,  img TEXT NOT NULL)");
        if (map.get("articlelist") == null)
            db.execSQL("create table articlelist(_id INTEGER NOT NULL,title TEXT NOT NULL,tid TEXT PRIMARY KEY,summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL,mname TEXT NOT NULL,date TEXT NOT NULL,author TEXT NOT NULL,year INTEGER NOT NULL,issue INTEGER NOT NULL,logo TEXT, width TEXT, height TEXT)");
        // 存放杂志目录数据
        if (map.get("magazinedirectorylist") == null)
            db.execSQL("create table magazinedirectorylist(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,tid TEXT NOT NULL,summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL,mname TEXT NOT NULL,date TEXT NOT NULL,author TEXT NOT NULL,year INTEGER NOT NULL,issue INTEGER NOT NULL,logo TEXT, width TEXT, height TEXT)");
        // 书架
        if (map.get("bookshelflist") == null)
            db.execSQL("create table bookshelflist(_id INTEGER PRIMARY KEY AUTOINCREMENT, bookguid TEXT, bookname TEXT, ordernumber TEXT, author TEXT, pubdate TEXT, bookpath TEXT, bookdownloadurl TEXT, bookaddtime TEXT, bookopentime TEXT, bookcategoryname TEXT, bookcover TEXT, bookisHasDumped INTEGER DEFAULT (0), bookbeginposition INTEGER DEFAULT (0), username TEXT)");
        // 存放离线杂志的目录数据
        if (map.get("magazineofflinedirectorylist") == null)
            db.execSQL("create table magazineofflinedirectorylist(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,tid TEXT NOT NULL, column TEXT, summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL,mname TEXT NOT NULL,date TEXT NOT NULL,author TEXT NOT NULL,year INTEGER NOT NULL,issue INTEGER NOT NULL,logo TEXT, width TEXT, height TEXT)");
        // 杂志关注
//		if (map.get("magazineattentioninfo") == null)
//			db.execSQL("create table magazineattentioninfo(_id INTEGER PRIMARY KEY AUTOINCREMENT, magguid TEXT, magname TEXT, magcategoryname TEXT, magcover TEXT, magishasdumped INTEGER DEFAULT (0), magbeginposition INTEGER DEFAULT (0), username TEXT)");
//		if (map.get("article") == null)
//			db.execSQL("create table article( title TEXT NOT NULL, content TEXT NOT NULL, tid TEXT PRIMARY KEY, Author TEXT NOT NULL, MagazineName TEXT NOT NULL, YEAR TEXT NOT NULL,ISSUE TEXT NOT NULL, mid TEXT NOT NULL,isread INTEGER,imgs TEXT,url TEXT,readed INTEGER,para TEXT,desc TEXT, icon TEXT)");
//		if (map.get("collection") == null)
//			db.execSQL("create table collection( title TEXT NOT NULL, content TEXT NOT NULL, tid TEXT PRIMARY KEY, Author TEXT NOT NULL, MagazineName TEXT NOT NULL, YEAR TEXT NOT NULL,ISSUE TEXT, mid TEXT ,flag INTEGER ,CATECODE TEXT,RESTYPE INTEGER,BTYPE INTEGER)");
        if (map.get("magazinelist") == null)
            db.execSQL("create table magazinelist(_id INTEGER NOT NULL,title TEXT NOT NULL,mid TEXT PRIMARY KEY,summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL,cover TEXT)");
        if (map.get("submagazinelist") == null)
            db.execSQL("create table submagazinelist(_id INTEGER NOT NULL,title TEXT NOT NULL,mid TEXT PRIMARY KEY,summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL)");
//		if (map.get("defaultlist") == null)
//			db.execSQL("create table defaultlist(_id INTEGER NOT NULL,title TEXT NOT NULL,tid TEXT PRIMARY KEY,summ TEXT NOT NULL,type TEXT NOT NULL,img TEXT NOT NULL,mname TEXT NOT NULL,date TEXT NOT NULL,author TEXT NOT NULL,year INTEGER NOT NULL,issue INTEGER NOT NULL,logo TEXT, width TEXT, height TEXT)");
        if (map.get("offlinemagazine") == null)
            db.execSQL("create table offlinemagazine(_id INTEGER PRIMARY KEY,mname TEXT ,mid TEXT ,cover TEXT ,issue INTEGER,year INTEGER, offlineurl TEXT, offlinecontent TEXT, user_name TEXT)");
        // 存放离线杂志架的杂志内容数据
        if (map.get("offlinemagazinereaderlist") == null)
            db.execSQL("create table offlinemagazinereaderlist(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL,tid TEXT NOT NULL, type TEXT, mname TEXT, content TEXT,issue TEXT, year TEXT, author TEXT, beforetid TEXT, nexttid TEXT)");
        // db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        System.out.println("upgrade a database");
    }
}