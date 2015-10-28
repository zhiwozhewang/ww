package com.longyuan.qm.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.ClipboardManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.longyuan.qm.ConstantsAmount;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import it.sauronsoftware.base64.Base64;

public class Utils {

    private static long lastClickTime;
    private static InputMethodManager imm;
    private static Resources res;
    private static SharedPreferences preferences;

    public static void getHttpRequestHeader() {
        ConstantsAmount.UNITBASEURL_REQUESTHEADER = "http://" + ConstantsAmount.BASEURL_UNIT + "/api/";
    }

    public static String getHtmlData(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!sumHTML.equals("")) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/style.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/style.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
    }

    public static String getHtmlDataBig(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!sumHTML.equals("")) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylebig.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylebig.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
    }

    public static String getHtmlDataSmall(String bodyHTML, String sumHTML) {
        String head = "";
        String summary = "";
        if (!sumHTML.equals("")) {
            //FIXME
//          <meta name=viewport content=target-densitydpi=medium-dpi, width=device-width/><style>p{text-indent:2em}</style><style>img{max-width: 100%; width:auto; height: auto;}</style>
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylesmall.css' type='text/css'/></head>";
            summary = "<hr/>" + sumHTML + "<hr/>";
            return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
        } else {
            summary = "<hr/><hr/>";
            head = "<head><link rel='stylesheet' href='file:///android_asset/stylesmall.css' type='text/css'/></head>";
        }
        return "<html>" + head + "<body>" + summary + bodyHTML + "</body></html>";
    }

    // 截取图片路径集合中-l.jpg结尾的URL;
    public static String getImageListUrlString(String urlList) {
        int urlLength = urlList.substring(0, urlList.lastIndexOf("-z.jpg") + 6)
                .length();
        return urlList.substring(urlList.lastIndexOf("-z.jpg") + 7,
                urlList.lastIndexOf("-z.jpg") + 7 + urlLength);
    }

    // TODO 去掉简介信息中的<p>、</p>、<br>、&nbsp;等标签;
    public static String replaceHtmlTag(String content) {
        String replace = content.replace("<p>", "");
        String replaced1 = replace.replace("</p>", "");
        String replaced2 = replaced1.replace("<P>", "");
        String replaced3 = replaced2.replace("</P>", "");
        String replaced4 = replaced3.replace("&nbsp;", " ");
        String replaced5 = replaced4.replace("&lt;P&gt;&lt;FONT face=\"Times New Roman\"&gt;", "");
        String replaced6 = replaced5.replace("&ldquo;", "\"").replace("&rdquo;", "\"").replace("&#39;", "'").replace("&acute;", "'").replace("&mdash;", "—").replace("&ndash;", "-")
                .replace("&bdquo;","\"").replace("&lsquo;", "'").replace("&rsquo;", "'").replace("&sbquo;", "'").replace("&circ;", "^").replace("&amp;", "&").replace("&quot;", "\"\"\"")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&hellip;", "……").replace("", "");
        return replaced6.replace("<br>", "");
    }

    // TODO 截取“：”后面的字符串，以“，”分割存为String[]；
    public static String[] menuValueArray() {
        String value = ConstantsAmount.MENUBEANLIST.get(ConstantsAmount.MENUPOSITION).getMenuValue();
        String classifyValue = "";
        String[] classifyArray = null;
        int length = value.length();
        for (int l = 0; l < length; l++) {
            if (value.substring(l, l + 1).equals(":")) {
                classifyValue = value.substring(l + 1, length);
                classifyArray = classifyValue.split(",");
                break;
            }
        }
        return classifyArray;
    }

    public static String createAppToken() {
        String str2u8 = "";
        try {
            byte[] b = DESUtil.encode(ConstantsAmount.sKey, ConstantsAmount.APPTOKEN_BASE + System.currentTimeMillis());
            String str = new String(Base64.encode(b));
            str2u8 = Utils.string2U8(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str2u8;
    }

    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: checkRequestCode
     * @Description: 判断Code是否为1(这里用一句话描述这个方法的作用)
     */
    public static String checkRequestCode(JSONObject json) {
        return json.optString("Code");
    }

    /**
     * @param @param  json
     * @param @return
     * @return String
     * @Title: jsonMessageParser
     * @Description: message信息(这里用一句话描述这个方法的作用)
     */
    public static String jsonMessageParser(JSONObject json) {
        if (json.optString("Message").equals("") || json.optString("Message").equals("null")) {
            return "";
        } else {
            return json.optString("Message");
        }
    }

    /**
     * encode by Base64
     */
    public static String encodeBase64(byte[] input) throws Exception {

        Class clazz = Class
                .forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("encode", byte[].class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, new Object[]{input});
        return (String) retObj;
    }

    /**
     * decode by Base64
     */
    public static byte[] decodeBase64(String input) throws Exception {
        Class clazz = Class
                .forName("com.sun.org.apache.xerces.internal.impl.dv.util.Base64");
        Method mainMethod = clazz.getMethod("decode", String.class);
        mainMethod.setAccessible(true);
        Object retObj = mainMethod.invoke(null, input);
        return (byte[]) retObj;
    }

    public static String string2U8(String str) {
        String encoded = "";
        try {
            encoded = URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encoded;
    }

    public static String MagazineCycleParser(String str) {
        if (str != null && !str.equals("null")) {
            if (str.equals("1")) {
                return "周刊";
            } else if (str.equals("2")) {
                return "半月刊";
            } else if (str.equals("3")) {
                return "月刊";
            } else if (str.equals("4")) {
                return "双月刊";
            } else if (str.equals("5")) {
                return "季刊";
            } else if (str.equals("6")) {
                return "旬刊";
            } else if (str.equals("7")) {
                return "双周刊";
            } else if (str.equals("8")) {
                return "半年刊";
            } else if (str.equals("9")) {
                return "年刊";
            } else {
                return "";
            }
        } else {
            return "";
        }
    }

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            lastClickTime = 0;
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 控制字体颜色改变的方法 一个控件上显示不同的颜色
     *
     * @param num   = 123qwerewr
     * @param over  = 3， num.length-over 止
     * @param start 从第几位开始
     * @see
     */
    public static void controlColorCenter(TextView textView, String num,
                                          int start, int colorID, int over) {

        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), start, num.length()
                - over, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3 num.length-over
     * @see
     * @see
     */
    public static void controlColor(TextView textView, String num, int over,
                                    int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), 0, num.length() - over,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3
     * @see
     * @see
     */
    public static void controlColor2(TextView textView, String num, int over,
                                     int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), 0, over,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * @param num  = 123qwerewr
     * @param over = 3
     * @see
     * @see
     */
    public static void controlColor3(TextView textView, String num, int over,
                                     int colorID) {
        SpannableStringBuilder style = new SpannableStringBuilder(num);
        style.setSpan(new ForegroundColorSpan(colorID), over, num.length(),
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        textView.setText(style);
    }

    /**
     * 对于提示语的封装，主要是针对“暂无咨询记录\n\n登录可同步您未登录状态下的问题，再次查看需要登录”
     *
     * @param tv
     *            展示文字的textview，一般为tvHint
     * @param text
     *            展示的内容，必须包含"\n"
     */
    // public static void tvHintsetText(Context context, TextView tv, String
    // text) {
    // int idx = text.lastIndexOf("\n");
    // if (res == null)
    // res = context.getResources();
    // Utils.controlColor3(tv, text, idx + 1,
    // res.getColor(R.color.text_color_888888));
    // }

    /**
     * 全角与半角的转化 将textview中的字符全角化
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 去除特殊字符或将所有中文标号替换为英文标号。利用正则表达式将所有特殊字符过滤，或利用replaceAll（）将中文标号替换为英文标号。则转化之后，
     * 则可解决排版混乱问题。
     *
     * @param str
     * @return
     * @throws java.util.regex.PatternSyntaxException 替换、过滤特殊字符
     */

    public static String StringFilter(String str) throws PatternSyntaxException {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
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
     * 键盘隐藏
     *
     * @param
     * @param e
     */
    public static void demissKeyBoard(Context c, EditText e) {
        if (imm == null)
            imm = (InputMethodManager) c
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(e.getWindowToken(), 0);
        e.clearFocus();
    }

    /**
     * 键盘显示
     *
     * @param
     * @param e
     */
    public static void showKeyBoard(Context c, EditText e) {
        if (imm == null)
            imm = (InputMethodManager) c
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
        // 如果开启
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void copyTOClipboard(final Context c, final String param) {
        new AlertDialog.Builder(c)
                .setTitle("操作文本")
                .setItems(new String[]{"复制"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                ClipboardManager clipboard = (ClipboardManager) c
                                        .getSystemService(Context.CLIPBOARD_SERVICE);
                                clipboard.setText(param);
                                ToastUtils.showToastShort(c, "内容已复制到剪切板上");
                            }
                        }
                ).show();

    }

    public static void setListViewAttr(ListView lv) {
        Class<? extends ListView> cls_lv = lv.getClass();
        try {
            Method setOverscrollFooter = cls_lv.getMethod(
                    "setOverscrollFooter", Drawable.class);
            try {
                Drawable drawable = null;
                setOverscrollFooter.invoke(lv, drawable);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static SharedPreferences getSharedPre(Context context) {

        if (preferences == null) {
            preferences = context.getSharedPreferences("count",
                    Context.MODE_WORLD_READABLE);
        }
        return preferences;
    }

    /**
     * 判断是否安装了某应用
     *
     * @param c
     * @return
     */
    public static int hasInstalledApp(Context c, String packageName) {
        List<PackageInfo> packs = c.getPackageManager().getInstalledPackages(
                PackageManager.PERMISSION_GRANTED);
        // Sysout.i(packs.size());
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (p == null) {
                continue;
            }
            if (packageName.equals(p.packageName))
                return 1;
        }
        return 0;
    }

    /**
     * 判断图片格式
     *
     * @param path
     * @return
     */
    public static boolean isImageFormat(String path) {

        if (path.endsWith("png") || path.endsWith("jpg")
                || path.endsWith("bmp") || path.endsWith("jpeg")) {

            return true;
        }
        return false;
    }

    /**
     * 获取设备网卡MAC地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String macAddress = null;
        try {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiManager ? null : wifiManager
                    .getConnectionInfo());
            if (null != info) {
                macAddress = info.getMacAddress();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (macAddress != null) {
            macAddress = macAddress.toLowerCase();
            return macAddress.replaceAll(":", "");
        }
        return macAddress;
    }

    public static int string2int(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long string2long(String s) {
        try {
            return Long.parseLong(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static float string2float(String s) {
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0F;
        }
    }

    public static double string2double(String s) {
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            e.printStackTrace();
            return 0D;
        }
    }

    public static void hiddenNeterroe(TextView tvHint, TextView tvNetError,
                                      ViewGroup contentLayout) {
        tvHint.setVisibility(View.GONE);
        tvNetError.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
    }

    public static int getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
