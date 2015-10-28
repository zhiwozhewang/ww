package com.longyuan.qm.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtil {
    /**
     * 电话号码前几位的分段
     */
    final static String[] PHONENUMBER_PREFIX = {"130", "131", "132", "133",
            "134", "135", "136", "137", "138", "139", "147", "150", "151",
            "152", "153", "155", "156", "157", "158", "159", "180", "181",
            "182", "183", "185", "186", "187", "188", "189"};

    /**
     * 匹配电话号码
     *
     * @param number
     * @return
     */
    public static boolean patternPhoneNumber(String number) {
        int len = PHONENUMBER_PREFIX.length;
        if (number != null) {
            for (int i = 0; i < len; i++) {
                Pattern p = Pattern.compile(PHONENUMBER_PREFIX[i] + "\\d{8}");
                if (p.matcher(number).matches()) {
                    // if (number.startsWith("1349")) {// 134开头的不包括1349
                    // return false;
                    // }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 匹配邮箱
     *
     * @param emailStr
     * @return
     */
    public static boolean patternEmail(String emailStr) {
        if (emailStr.contains(" ") || emailStr.contains("__")
                || emailStr.contains("_.") || emailStr.contains("._")
                || emailStr.contains("..")) {
            return false;
        }
        // String
        // check="^([a-z0-9A-Z]+[-|_\\.]?)+[a-z0-9A-Z]@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        String check = "^[a-z0-9A-Z]{1}[\\w\\.]{2,14}[a-z0-9A-Z]{1}@[a-z0-9A-Z]{1}[a-z0-9A-Z\\.]{1,28}[a-zA-Z]{1}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(emailStr);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            String[] temp = emailStr.split("@")[1].split("\\.");
            if (temp.length < 2 || temp.length > 5) {// @后至少有1个最多有4个"."
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 话题评论规则
     *
     * @param str
     * @return
     */
    public static boolean patternCircleInput(String str) {
        String check = "^((.*)([a-zA-Z0-9\\u4e00-\\u9fa5]+)(.*)){3,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(str);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        }
        return false;
    }

    /**
     * 匹配6-20密码,数字、字母、下划线
     *
     * @param number
     * @return true | false
     */
    public static boolean passWordNumber(String number) {
        String check = "^[a-z0-9A-Z_]{6,20}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(number);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串中是不是包含空格
     *
     * @param str
     * @return true:存在，false：不存在
     */
    public static boolean isExistSpaceInString(String str) {
        char[] psw = str.toCharArray();
        for (int i = 0; i < psw.length; i++) {
            if (psw[i] == ' ')
                return true;
        }
        return false;
    }

    /**
     * 匹配数字
     *
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        String check = "^[0-9]*$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(number);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        }
        return false;
    }

    /**
     * 验证码判断
     *
     * @param code
     * @return
     */
    public static boolean isCode(String code) {
        String check = "^[0-9]{6}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(code);
        boolean isMatched = matcher.matches();
        if (isMatched) {
            return true;
        }
        return false;
    }

}
