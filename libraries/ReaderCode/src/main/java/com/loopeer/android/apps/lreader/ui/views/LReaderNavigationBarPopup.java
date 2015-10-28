package com.loopeer.android.apps.lreader.ui.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.loopeer.android.apps.lreader.utilities.UiUtilities;

import org.geometerplus.android.fbreader.R;
import org.geometerplus.fbreader.fbreader.FBReaderApp;
import org.geometerplus.fbreader.fbreader.options.ColorProfile;
import org.geometerplus.zlibrary.core.options.ZLIntegerRangeOption;

public class LReaderNavigationBarPopup extends PopupWindow implements View.OnClickListener {

    private LinearLayout mBackground;
    private ImageView decrease;
    private ImageView increase;

  //private AnyTextView[] fontTextView = new AnyTextView[4];
  //String[] fonts = {"华文行楷", "黑体", "宋体", "仿宋"};
  //
  //  private View divider;

	public LReaderNavigationBarPopup(Context context){
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}
	
	public LReaderNavigationBarPopup(Context context, int width, int height){
		setFocusable(true);
		setTouchable(true);
		setOutsideTouchable(true);
		setWidth(width);
		setHeight(height);
		setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.PopupAnimation);
		
		setContentView(LayoutInflater.from(context).inflate(R.layout.l_reader_navigation_bar_popup, null));
        mBackground = UiUtilities.getView(getContentView(), R.id.wrapper_reader_navifation_bar_popup);
        decrease = UiUtilities.getView(getContentView(), R.id.image_reader_navigationbar_popup_decrease);
        increase = UiUtilities.getView(getContentView(), R.id.image_reader_navigationbar_popup_increase);
    //fontTextView[0] = UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_xingkai);
    //fontTextView[1] = UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_hei);
    //fontTextView[2] = UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_sun);
    //fontTextView[3] = UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_fang);
       //divider = UiUtilities.getView(getContentView(), R.id.divider);

        UiUtilities.getView(getContentView(), R.id.view_reader_navigationbar_popup_decrease).setOnClickListener(this);
        UiUtilities.getView(getContentView(), R.id.view_reader_navigationbar_popup_increase).setOnClickListener(this);
        //UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_xingkai).setOnClickListener(this);
        //UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_hei).setOnClickListener(this);
        //UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_sun).setOnClickListener(this);
        //UiUtilities.getView(getContentView(), R.id.text_reader_navigationbar_popup_fang).setOnClickListener(this);

    //updateFontTextView();

	}

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.view_reader_navigationbar_popup_decrease) {
            onDecreasePressed();
        } else if (id == R.id.view_reader_navigationbar_popup_increase) {
            onIncreasePressed();
        } /*else if (id == R.id.text_reader_navigationbar_popup_xingkai) {
          changeFont(fonts[0]);
        } else if (id == R.id.text_reader_navigationbar_popup_hei) {
          changeFont(fonts[1]);
        } else if (id == R.id.text_reader_navigationbar_popup_sun) {
          changeFont(fonts[2]);
        } else if (id == R.id.text_reader_navigationbar_popup_fang) {
          changeFont(fonts[3]);
        }*/
    }

    private final int myDelta = 2;
    public void onDecreasePressed() {
        FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
        if (fbReaderApp == null) return;

        final ZLIntegerRangeOption option = fbReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption;
        option.setValue(option.getValue() - myDelta);
        fbReaderApp.clearTextCaches();
        fbReaderApp.getViewWidget().repaint();
    }

    public void onIncreasePressed() {
        FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
        if (fbReaderApp == null) return;

        final ZLIntegerRangeOption option = fbReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle().FontSizeOption;
        option.setValue(option.getValue() + myDelta);
        fbReaderApp.clearTextCaches();
        fbReaderApp.getViewWidget().repaint();
    }

  //private void changeFont(String value) {
  //  FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
  //  if (fbReaderApp == null) return;
  //  ZLTextBaseStyle baseStyle = fbReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle();
  //  baseStyle.FontFamilyOption.setValue(value);
  //
  //  updateFontTextView();
  //
  //  fbReaderApp.clearTextCaches();
  //  fbReaderApp.getViewWidget().repaint();
  //}

  //private void updateFontTextView() {
  //  FBReaderApp fbReaderApp = (FBReaderApp) FBReaderApp.Instance();
  //  if (fbReaderApp == null) return;
  //  ZLTextBaseStyle baseStyle = fbReaderApp.ViewOptions.getTextStyleCollection().getBaseStyle();
  //
  //  int position = -1;
  //  for (int i=0; i<fonts.length; i++) {
  //    if (fonts[i].equals(baseStyle.FontFamilyOption.getValue())) {
  //      position = i;
  //      break;
  //    }
  //  }
  //  if (ColorProfile.DAY.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
  //    updateFontTextView(position, ReaderApp.getAppContext().getResources().getColor(R.color.color_929292));
  //  } else if (ColorProfile.NIGHT.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
  //    updateFontTextView(position, ReaderApp.getAppContext().getResources().getColor(android.R.color.white));
  //  }
  //}

  //private void updateFontTextView(int position, int color) {
  //  for (int i=0; i<fonts.length; i++) {
  //    if (i == position) {
  //      fontTextView[i].setTextColor(ReaderApp.getAppContext().getResources().getColor(R.color.color_5fb7e7));
  //    } else {
  //      fontTextView[i].setTextColor(color);
  //    }
  //  }
  //}

	public void show(Activity activity, View parent){
        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        updateBackground();
    //updateFontTextView();

        int yOffset = frame.top+ activity.getResources().getDimensionPixelOffset(R.dimen.l_reader_navigationbar_height) - 22;//减去阴影宽度，适配UI.
        int xOffset = dip2px(activity, 15f); //设置x方向offset为4dp

        showAtLocation(parent, Gravity.END | Gravity.TOP,
                xOffset, yOffset);
	}


    public void updateBackground() {
        FBReaderApp fbReaderApp = (FBReaderApp)FBReaderApp.Instance();
        if (fbReaderApp != null) {
            if (ColorProfile.DAY.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                mBackground.setBackgroundResource(R.drawable.ic_l_reader_navigation_bar_popup_day);
                increase.setImageResource(R.drawable.ic_l_reader_navigation_bar_popup_increase_day);
                decrease.setImageResource(R.drawable.ic_l_reader_navigation_bar_popup_decrease_day);

              //divider.setBackgroundColor(ReaderApp.getAppContext().getResources().getColor(R.color.color_efefef));
            } else if (ColorProfile.NIGHT.equals(fbReaderApp.ViewOptions.ColorProfileName.getValue())) {
                mBackground.setBackgroundResource(R.drawable.ic_l_reader_navigation_bar_popup_night);
                increase.setImageResource(R.drawable.ic_l_reader_navigation_bar_popup_increase_night);
                decrease.setImageResource(R.drawable.ic_l_reader_navigation_bar_popup_decrease_night);

              //divider.setBackgroundColor(ReaderApp.getAppContext().getResources().getColor(R.color.color_2a2e36));
            }
        }
    }
	
	public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


}
