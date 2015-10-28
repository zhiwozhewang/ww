package com.longyuan.qm.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.longyuan.qm.R;

/**
 * @author
 * @version 1.0.0
 * @description 加载弹出框
 * @create 2014-2-18 上午10:43:40
 * @company
 */
public class LoadingDialog extends Dialog {
    private static LoadingDialog mLoadingDialog;
    /**
     * 加载提示
     */
    private TextView tvText;
    /**
     * 加载提示内容
     */
    private String title;

    public LoadingDialog(Context context, boolean cancelable,
                         OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public LoadingDialog(Context context, int theme, String title) {
        super(context, theme);
        this.title = title;
    }

    /**
     * 显示加载框
     *
     * @param
     * @param
     * @author
     * @create 2014-6-30 上午10:02:21
     */
    public static void showDialog(Context context, String title) {
        mLoadingDialog = new LoadingDialog(context, R.style.LoadingDialogTheme,
                title);
        mLoadingDialog.setCanceledOnTouchOutside(true);
        mLoadingDialog.setCancelable(true);
        mLoadingDialog.show();
    }

    /**
     * 显示加载框
     *
     * @param
     * @param
     * @author
     * @create 2014-6-30 上午10:02:21
     */
    public static void showDialog(Context context, String title,
                                  OnCancelListener listener) {
        showDialog(context, title);
        if (listener != null) {
            mLoadingDialog.setOnCancelListener(listener);
        }
    }

    /**
     * 取消加载框
     *
     * @param
     * @param
     * @author
     * @create 2014-6-30 上午10:02:21
     */
    public static void dissmissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.cancel();
            mLoadingDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dialog_loading);
        tvText = (TextView) findViewById(R.id.tv_loading_text);
        tvText.setText(title + "");
    }
}
