package com.longyuan.qm.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.longyuan.qm.ConstantsAmount;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author 谭杰 E-mail: tanjie9012@163.com
 * @version 1.0.0
 * @description 文件操作工具类 新建根目录,子目录,删除文件,文件缓存目录
 * @create 2014-2-13 上午09:35:28
 * @company 北京开拓明天科技有限公司 Copyright: 版权所有 (c) 2014
 */
public class FileUtil {

    public static final String TAG = "FileUtil";
    /**
     * 文件复制缓存大小
     */
    public static final int BUFFER_SIZE = 1024;
    /**
     * SD卡最小空闲大小,若低于此值则认为SD卡不可用，单位MB
     */
    private static final int SDCARD_MIN_SIZE = 50;

    /**
     * 判断SD卡是否可用
     *
     * @return true 挂载SD卡并且剩余空间大于SDCARD_MIN_SIZE，否则false
     */
    public static boolean isSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)
                && SDCARD_MIN_SIZE <= getSDCardSize()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取SD卡剩余空间,单位MB
     *
     * @return SD卡剩余空间, 单位MB
     */
    public static long getSDCardSize() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            // 获取单个数据块的大小(Byte)
            long blockSize = statFs.getBlockSize();
            // 空闲的数据块的数量
            long freeBlocks = statFs.getAvailableBlocks();
            // 返回SD卡空闲大小
            // long SDCardSize = freeBlocks * blockSize; //单位Byte
            // long SDCardSize = (freeBlocks * blockSize)/1024; //单位KB
            long SDCardSize = (freeBlocks * blockSize) / 1024 / 1024;
            return SDCardSize; // 单位MB
        }
        return 0;
    }

    /**
     * 获取SD卡根目录
     *
     * @return 如果挂载SD卡, 返回SD卡根目录, 否则返回null
     */
    public static File getSDCard() {
        if (isSDCard()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return null;
        }
    }

    /**
     * 获取手机内存根目录
     *
     * @param context
     * @return 返回手机内存根目录
     */
    public static File getCacheDir(Context context) {
        return context.getCacheDir();
    }

    /**
     * 获取并创建项目存储目录,优先创建至SD卡
     *
     * @param context
     * @param
     * @return 项目资源存储的path目录
     */
    public static File getFileDir(Context context, String dirName) {
        return getFileDir(context, dirName, true);
    }

    /**
     * 获取并创建项目存储目录,指定是否创建至SD卡
     *
     * @param context
     * @param
     * @param isSDCard true 若SD卡挂载则创建至SD卡,未挂载则创建至手机内存。false 直接创建至手机内存
     * @return 项目资源存储的path目录
     */
    public static File getFileDir(Context context, String dirName,
                                  boolean isSDCard) {
        File rootDir = null;
        if (isSDCard) {
            rootDir = getSDCard();
            if (rootDir == null) {
                rootDir = getCacheDir(context);
            }
        } else {
            rootDir = getCacheDir(context);
        }
        File fileDir = new File(rootDir, dirName);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    /**
     * 获取并创建项目资源存储子目录
     *
     * @param
     * @param
     * @return 资源存储子目录
     */
    public static File getFileDir(File parentsDir, String dirName) {
        File fileDir = new File(parentsDir, dirName);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        return fileDir;
    }

    /**
     * 获取缓存文件保存位置
     *
     * @param context
     * @param
     * @return
     * @author 谭杰
     * @create 2014-6-27 下午4:15:42
     */
    public static File getFileCacheDir(Context context) {
        String dirName = ActivityUtil.getSharedPreferences(context).getString(
                ConstantsAmount.Cache.ROOT_DIR_NAME, "root");
        File rootFile = getFileDir(context, dirName);
        return getFileDir(rootFile, ConstantsAmount.FILE_CACHE_NAME);
    }

    /**
     * 获取缓存文件保存位置
     *
     * @param context
     * @param dirName
     * @return
     * @author 谭杰
     * @create 2014-6-27 下午4:15:42
     */
    public static File getFileCacheDir(Context context, String dirName) {
        return FileUtil.getFileDir(getFileCacheDir(context), dirName);
    }

    /**
     * 文件复制
     *
     * @param is 复制源文件流
     * @param os 复制目标文件流
     */
    public static void copyFile(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (true) {
                int count = is.read(buffer, 0, BUFFER_SIZE);
                if (count == -1)
                    break;
                os.write(buffer, 0, count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
                if (os != null)
                    os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteFile(String fineName) {
        File file = new File(fineName);
        file.delete();
    }

    public static void RenameFile(String n1, String n2) {
        File file = new File(n1);
        File f = new File(n2);
        file.renameTo(f);
    }

    /**
     * @param
     * @param @return
     * @return boolean
     * @Title: checkSDCard
     * @Description: 判断sdcard是否存在(这里用一句话描述这个方法的作用)
     */
    public static boolean checkSDCard() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param
     * @param
     * @return boolean
     * @Title: checkFileIsExist
     * @Description: 判断CDCard中是否存在该文件(这里用一句话描述这个方法的作用)
     */
    public static boolean checkFileIsExist(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                FileUtil.deleteFile(childFiles[i]);
            }
            file.delete();
        }
    }
}
