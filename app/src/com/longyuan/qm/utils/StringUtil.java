package com.longyuan.qm.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author
 * @version 1.0.0
 * @description 字符串操作
 * @create 2014-2-21 上午11:40:27
 * @company 北京开拓明天科技有限公司 Copyright: 版权所有 (c) 2014
 */
public class StringUtil {

    public static final String TAG = "StringUtil";

    /**
     * 字符串是否为空
     *
     * @param value
     * @return 字符串为""或Null或"null"返回true
     */
    public static boolean isEmpty(String value) {
        boolean flag = false;
        if (value == null || "".equals(value) || "null".equals(value)) {
            flag = true;
        }
        return flag;
    }

    /**
     * MD5加密
     *
     * @param str
     * @return
     * @author
     * @create 2014-7-2 上午10:10:44
     */
    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(
                        Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    /**
     * 是否包含中文字符
     *
     * @param
     * @return
     * @author
     * @create 2014-7-2 上午10:11:17
     */
    public static boolean isChinese(String str) {
        char[] ch = str.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            if (isChinese(ch[i]) == true) {
                return true;
            }
        }
        return false;
    }

    /**
     * 中文字符判断
     *
     * @param
     * @return
     * @author
     * @create 2014-7-2 上午10:11:40
     */
    public static boolean isChinese(char ch) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 汉字转换为汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        chines = chines.replaceAll("\\s*", "");
        String pinyinName = "";
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(
                            nameChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinName += nameChar[i];
            }
        }
        return pinyinName;
    }

    /**
     * 删除转义符
     *
     * @param str
     * @return
     * @author
     * @create 2014-6-26 上午9:51:50
     */
    public static String deleteEscape(String str) {
        char[] chars = str.toCharArray();
        String newStr = "";
        for (char ch : chars) {
            if (ch != 92) {
                newStr += ch;
            }
        }
        return newStr;
    }

    /**
     * 首字母大写
     *
     * @param colName
     * @return
     */
    public static String szmUpperCase(String colName) {
        // colName = colName.toLowerCase();
        char szm = colName.charAt(0);
        szm = Character.toUpperCase(szm);
        colName = colName.substring(1, colName.length());
        return new String(szm + colName);
    }
}
