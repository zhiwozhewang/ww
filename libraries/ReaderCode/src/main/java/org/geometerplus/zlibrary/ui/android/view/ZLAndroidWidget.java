/*
 * Copyright (C) 2007-2014 Geometer Plus <contact@geometerplus.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 */

package org.geometerplus.zlibrary.ui.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;

import org.geometerplus.android.fbreader.FBReader;
import org.geometerplus.android.fbreader.R;
import org.geometerplus.fbreader.fbreader.options.PageTurningOptions;
import org.geometerplus.zlibrary.core.application.ZLApplication;
import org.geometerplus.zlibrary.core.application.ZLKeyBindings;
import org.geometerplus.zlibrary.core.options.Config;
import org.geometerplus.zlibrary.core.options.ZLIntegerOption;
import org.geometerplus.zlibrary.core.view.ZLView;
import org.geometerplus.zlibrary.core.view.ZLViewWidget;
import org.geometerplus.zlibrary.ui.android.library.ZLAndroidLibrary;

public class ZLAndroidWidget extends View implements ZLViewWidget, View.OnLongClickListener {
	private final Paint myPaint = new Paint();
	private final BitmapManager myBitmapManager = new BitmapManager(this);
	private Bitmap myFooterBitmap;
    private Bitmap myHeaderBitmap;

	public ZLAndroidWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ZLAndroidWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ZLAndroidWidget(Context context) {
		super(context);
		init();
	}

	private void init() {
		// next line prevent ignoring first onKeyDown DPad event
		// after any dialog was closed
		setFocusableInTouchMode(true);
		setDrawingCacheEnabled(false);
		setOnLongClickListener(this);
	}

	private volatile boolean myAmendSize = false;
	private volatile int myHDiff = 0;
	private volatile int myHShift = 0;

	public void setPreserveSize(boolean preserve) {
		myAmendSize = preserve;
		if (!preserve) {
			myHDiff = 0;
			myHShift = 0;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (myAmendSize && oldw == w) {
			myHDiff += h - oldh;
			myHShift -= getStatusBarHeight();
		} else {
			myHDiff = 0;
			myHShift = 0;
		}
		getAnimationProvider().terminate();
		if (myScreenIsTouched) {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			myScreenIsTouched = false;
			view.onScrollingFinished(ZLView.PageIndex.current);
		}
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		final Context context = getContext();
		if (context instanceof FBReader) {
			((FBReader)context).createWakeLock();
		} else {
			System.err.println("A surprise: view's context is not an FBReader");
		}
		super.onDraw(canvas);

		if (myHShift != 0) {
			canvas.translate(0, myHShift);
		}

		if (getAnimationProvider().inProgress()) {
			onDrawInScrolling(canvas);
		} else {
			onDrawStatic(canvas);
			ZLApplication.Instance().onRepaintFinished();
		}

		Config.Instance().runOnConnect(new Runnable() {
			public void run() {
				showHint(canvas);
			}
		});
	}

	private void showHint(final Canvas canvas) {
		final Context context = getContext();
		if (!(context instanceof FBReader)) {
			return;
		}

		final ZLAndroidLibrary library = (ZLAndroidLibrary)ZLAndroidLibrary.Instance();
		final ZLIntegerOption stageOption = library.ScreenHintStageOption;
		if (!library.OldShowActionBarOption.getValue()) {
			stageOption.setValue(2);
		}
		if (stageOption.getValue() >= 2) {
			return;
		}

		final FBReader fbReader = (FBReader)context;
		fbReader.runOnUiThread(new Runnable() {
			public void run() {
				String key = null;
				if (!fbReader.barsAreShown()) {
					if (stageOption.getValue() == 0) {
						stageOption.setValue(1);
					}
					if (stageOption.getValue() == 1) {
						key = "message1";
					} else {
						stageOption.setValue(3);
					}
				} else {
					if (stageOption.getValue() == 1) {
						stageOption.setValue(2);
					}
					if (stageOption.getValue() == 2) {
//						key = "message2";
						key = null;
					}
				}

				final ImageView hintView = (ImageView)fbReader.findViewById(R.id.hint_view);
				if (key != null) {
					if (!new PageTurningOptions().Horizontal.getValue()) {
						key = null;
						stageOption.setValue(3);
					}
				}
				if (key != null) {
//					final String text =
//						ZLResource.resource("dialog").getResource("screenHint").getResource(key).getValue();
//					final int w = getWidth();
//					final int h = getHeight();
//					final Paint paint = new Paint();
//					paint.setARGB(192, 51, 102, 153);
//					canvas.drawRect(w / 3, 0, w * 2 / 3, h, paint);
					hintView.setVisibility(View.VISIBLE);
//					hintView.setText(text);
				} else {
					hintView.setVisibility(View.GONE);
				}
			}
		});
	}

	private AnimationProvider myAnimationProvider;
	private ZLView.Animation myAnimationType;
	private int myStoredLayerType = -1;
	private AnimationProvider getAnimationProvider() {
		final ZLView.Animation type = ZLApplication.Instance().getCurrentView().getAnimationType();
		if (myAnimationProvider == null || myAnimationType != type) {
			myAnimationType = type;
			if (myStoredLayerType != -1) {
				setLayerType(myStoredLayerType, null);
			}
			switch (type) {
				case none:
					myAnimationProvider = new NoneAnimationProvider(myBitmapManager);
					break;
				case curl:
					myStoredLayerType = getLayerType();
					myAnimationProvider = new CurlAnimationProvider(myBitmapManager);
					setLayerType(LAYER_TYPE_SOFTWARE, null);
					break;
				case slide:
					myAnimationProvider = new SlideAnimationProvider(myBitmapManager);
					break;
				case shift:
					myAnimationProvider = new ShiftAnimationProvider(myBitmapManager);
					break;
			}
		}
		return myAnimationProvider;
	}

	private void onDrawInScrolling(Canvas canvas) {
		final ZLView view = ZLApplication.Instance().getCurrentView();

//		final int w = getWidth();
//		final int h = getMainAreaHeight();

		final AnimationProvider animator = getAnimationProvider();
		final AnimationProvider.Mode oldMode = animator.getMode();
		animator.doStep();
		if (animator.inProgress()) {
			animator.draw(canvas);
			if (animator.getMode().Auto) {
				postInvalidate();
			}
			drawFooter(canvas);
            drawHeader(canvas);
		} else {
			switch (oldMode) {
				case AnimatedScrollingForward:
				{
					final ZLView.PageIndex index = animator.getPageToScrollTo();
					myBitmapManager.shift(index == ZLView.PageIndex.next);
					view.onScrollingFinished(index);
					ZLApplication.Instance().onRepaintFinished();
					break;
				}
				case AnimatedScrollingBackward:
					view.onScrollingFinished(ZLView.PageIndex.current);
					break;
			}
			onDrawStatic(canvas);
		}
	}

	@Override
	public void reset() {
		myBitmapManager.reset();
	}

	@Override
	public void repaint() {
		postInvalidate();
	}

	@Override
	public void startManualScrolling(int x, int y, ZLView.Direction direction) {
		final AnimationProvider animator = getAnimationProvider();
		animator.setup(direction, getWidth(), getMainAreaHeight(), getMainAreaMarginTop());
		animator.startManualScrolling(x, y);
	}

	@Override
	public void scrollManuallyTo(int x, int y) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final AnimationProvider animator = getAnimationProvider();
		if (view.canScroll(animator.getPageToScrollTo(x, y))) {
			animator.scrollTo(x, y);
			postInvalidate();
		}
	}

	@Override
	public void startAnimatedScrolling(ZLView.PageIndex pageIndex, int x, int y, ZLView.Direction direction, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
			return;
		}
		final AnimationProvider animator = getAnimationProvider();
		animator.setup(direction, getWidth(), getMainAreaHeight(), getMainAreaMarginTop());
		animator.startAnimatedScrolling(pageIndex, x, y, speed);
		if (animator.getMode().Auto) {
			postInvalidate();
		}
	}

	@Override
	public void startAnimatedScrolling(ZLView.PageIndex pageIndex, ZLView.Direction direction, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
			return;
		}
		final AnimationProvider animator = getAnimationProvider();
		animator.setup(direction, getWidth(), getMainAreaHeight(), getMainAreaMarginTop());
		animator.startAnimatedScrolling(pageIndex, null, null, speed);
		if (animator.getMode().Auto) {
			postInvalidate();
		}
	}

	@Override
	public void startAnimatedScrolling(int x, int y, int speed) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final AnimationProvider animator = getAnimationProvider();
		if (!view.canScroll(animator.getPageToScrollTo(x, y))) {
			animator.terminate();
			return;
		}
		animator.startAnimatedScrolling(x, y, speed);
		postInvalidate();
	}

	void drawOnBitmap(Bitmap bitmap, ZLView.PageIndex index) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (view == null) {
			return;
		}

		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
			new Canvas(bitmap),
			getWidth(),
			getMainAreaHeight(),
			view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
		);
		view.paint(context, index);
	}

	private void drawFooter(Canvas canvas) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final ZLView.FooterArea footer = view.getFooterArea();

		if (footer == null) {
			myFooterBitmap = null;
			return;
		}

		if (myFooterBitmap != null &&
			(myFooterBitmap.getWidth() != getWidth() ||
			 myFooterBitmap.getHeight() != footer.getHeight())) {
			myFooterBitmap = null;
		}
		if (myFooterBitmap == null) {
			myFooterBitmap = Bitmap.createBitmap(getWidth(), footer.getHeight(), Bitmap.Config.RGB_565);
		}
		final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
			new Canvas(myFooterBitmap),
			getWidth(),
			footer.getHeight(),
			view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
		);
		footer.paint(context);
		canvas.drawBitmap(myFooterBitmap, 0, getHeight() - myHDiff - footer.getHeight(), myPaint);
	}

    private void drawHeader(Canvas canvas) {
        final ZLView view = ZLApplication.Instance().getCurrentView();
        final ZLView.HeaderArea header = view.getHeaderArea();

        if (header == null) {
            myHeaderBitmap = null;
            return;
        }

        if (myHeaderBitmap != null &&
                (myHeaderBitmap.getWidth() != getWidth() ||
                        myHeaderBitmap.getHeight() != header.getHeight())) {
            myHeaderBitmap = null;
        }
        if (myHeaderBitmap == null) {
            myHeaderBitmap = Bitmap.createBitmap(getWidth(), header.getHeight(), Bitmap.Config.RGB_565);
        }
        final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
                new Canvas(myHeaderBitmap),
                getWidth(),
                header.getHeight(),
                view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
        );
        header.paint(context);
        canvas.drawBitmap(myHeaderBitmap, 0, 0, myPaint);
    }

	private void onDrawStatic(final Canvas canvas) {
		myBitmapManager.setSize(getWidth(), getMainAreaHeight());
        final ZLView.HeaderArea header = ZLApplication.Instance().getCurrentView().getHeaderArea();

        canvas.drawBitmap(myBitmapManager.getBitmap(ZLView.PageIndex.current), 0, header != null ? header.getHeight() : 0, myPaint);
		drawFooter(canvas);
        drawHeader(canvas);
		new Thread() {
			@Override
			public void run() {
				final ZLView view = ZLApplication.Instance().getCurrentView();
				final ZLAndroidPaintContext context = new ZLAndroidPaintContext(
					canvas,
					getWidth(),
					getMainAreaHeight(),
					view.isScrollbarShown() ? getVerticalScrollbarWidth() : 0
				);
				view.preparePage(context, ZLView.PageIndex.next);
			}
		}.start();
	}

	public void turnPageStatic(boolean next) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		final ZLView.PageIndex pageIndex = next ? ZLView.PageIndex.next : ZLView.PageIndex.previous;
		if (pageIndex == ZLView.PageIndex.current || !view.canScroll(pageIndex)) {
			return;
		}
		myBitmapManager.shift(next);
		view.onScrollingFinished(pageIndex);
		repaint();
		ZLApplication.Instance().onRepaintFinished();
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, null);
		} else {
			ZLApplication.Instance().getCurrentView().onTrackballRotated((int)(10 * event.getX()), (int)(10 * event.getY()));
		}
		return true;
	}

	private class LongClickRunnable implements Runnable {
		@Override
		public void run() {
			if (performLongClick()) {
				myLongClickPerformed = true;
			}
		}
	}
	private volatile LongClickRunnable myPendingLongClickRunnable;
	private volatile boolean myLongClickPerformed;

	private void postLongClickRunnable() {
		myLongClickPerformed = false;
		myPendingPress = false;
		if (myPendingLongClickRunnable == null) {
			myPendingLongClickRunnable = new LongClickRunnable();
		}
		postDelayed(myPendingLongClickRunnable, 2 * ViewConfiguration.getLongPressTimeout());
	}

	private class ShortClickRunnable implements Runnable {
		@Override
		public void run() {
			final ZLView view = ZLApplication.Instance().getCurrentView();
			view.onFingerSingleTap(myPressedX, myPressedY);
			myPendingPress = false;
			myPendingShortClickRunnable = null;
		}
	}
	private volatile ShortClickRunnable myPendingShortClickRunnable;

	private volatile boolean myPendingPress;
	private volatile boolean myPendingDoubleTap;
	private int myPressedX, myPressedY;
	private boolean myScreenIsTouched;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();

		final ZLView view = ZLApplication.Instance().getCurrentView();
		switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if (myPendingDoubleTap) {
					view.onFingerDoubleTap(x, y);
				} else if (myLongClickPerformed) {
					view.onFingerReleaseAfterLongPress(x, y);
				} else {
					if (myPendingLongClickRunnable != null) {
						removeCallbacks(myPendingLongClickRunnable);
						myPendingLongClickRunnable = null;
					}
					if (myPendingPress) {
						if (view.isDoubleTapSupported()) {
							if (myPendingShortClickRunnable == null) {
								myPendingShortClickRunnable = new ShortClickRunnable();
							}
							postDelayed(myPendingShortClickRunnable, ViewConfiguration.getDoubleTapTimeout());
						} else {
							view.onFingerSingleTap(x, y);
						}
					} else {
						view.onFingerRelease(x, y);
					}
				}
				myPendingDoubleTap = false;
				myPendingPress = false;
				myScreenIsTouched = false;
				break;
			case MotionEvent.ACTION_DOWN:
				if (myPendingShortClickRunnable != null) {
					removeCallbacks(myPendingShortClickRunnable);
					myPendingShortClickRunnable = null;
					myPendingDoubleTap = true;
				} else {
					postLongClickRunnable();
					myPendingPress = true;
				}
				myScreenIsTouched = true;
				myPressedX = x;
				myPressedY = y;
				break;
			case MotionEvent.ACTION_MOVE:
			{
				final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
				final boolean isAMove =
					Math.abs(myPressedX - x) > slop || Math.abs(myPressedY - y) > slop;
				if (isAMove) {
					myPendingDoubleTap = false;
				}
				if (myLongClickPerformed) {
					view.onFingerMoveAfterLongPress(x, y);
				} else {
					if (myPendingPress) {
						if (isAMove) {
							if (myPendingShortClickRunnable != null) {
								removeCallbacks(myPendingShortClickRunnable);
								myPendingShortClickRunnable = null;
							}
							if (myPendingLongClickRunnable != null) {
								removeCallbacks(myPendingLongClickRunnable);
							}
							view.onFingerPress(myPressedX, myPressedY);
							myPendingPress = false;
						}
					}
					if (!myPendingPress) {
						view.onFingerMove(x, y);
					}
				}
				break;
			}
		}

		return true;
	}

	@Override
	public boolean onLongClick(View v) {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		return view.onFingerLongPress(myPressedX, myPressedY);
	}

	private int myKeyUnderTracking = -1;
	private long myTrackingStartTime;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		final ZLApplication application = ZLApplication.Instance();
		final ZLKeyBindings bindings = application.keyBindings();

		if (bindings.hasBinding(keyCode, true) ||
			bindings.hasBinding(keyCode, false)) {
			if (myKeyUnderTracking != -1) {
				if (myKeyUnderTracking == keyCode) {
					return true;
				} else {
					myKeyUnderTracking = -1;
				}
			}
			if (bindings.hasBinding(keyCode, true)) {
				myKeyUnderTracking = keyCode;
				myTrackingStartTime = System.currentTimeMillis();
				return true;
			} else {
				return application.runActionByKey(keyCode, false);
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (myKeyUnderTracking != -1) {
			if (myKeyUnderTracking == keyCode) {
				final boolean longPress = System.currentTimeMillis() >
					myTrackingStartTime + ViewConfiguration.getLongPressTimeout();
				ZLApplication.Instance().runActionByKey(keyCode, longPress);
			}
			myKeyUnderTracking = -1;
			return true;
		} else {
			final ZLKeyBindings bindings = ZLApplication.Instance().keyBindings();
			return
				bindings.hasBinding(keyCode, false) ||
				bindings.hasBinding(keyCode, true);
		}
	}

	@Override
	protected int computeVerticalScrollExtent() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		final AnimationProvider animator = getAnimationProvider();
		if (animator.inProgress()) {
			final int from = view.getScrollbarThumbLength(ZLView.PageIndex.current);
			final int to = view.getScrollbarThumbLength(animator.getPageToScrollTo());
			final int percent = animator.getScrolledPercent();
			return (from * (100 - percent) + to * percent) / 100;
		} else {
			return view.getScrollbarThumbLength(ZLView.PageIndex.current);
		}
	}

	@Override
	protected int computeVerticalScrollOffset() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		final AnimationProvider animator = getAnimationProvider();
		if (animator.inProgress()) {
			final int from = view.getScrollbarThumbPosition(ZLView.PageIndex.current);
			final int to = view.getScrollbarThumbPosition(animator.getPageToScrollTo());
			final int percent = animator.getScrolledPercent();
			return (from * (100 - percent) + to * percent) / 100;
		} else {
			return view.getScrollbarThumbPosition(ZLView.PageIndex.current);
		}
	}

	@Override
	protected int computeVerticalScrollRange() {
		final ZLView view = ZLApplication.Instance().getCurrentView();
		if (!view.isScrollbarShown()) {
			return 0;
		}
		return view.getScrollbarFullSize();
	}

	private int getMainAreaHeight() {
		final ZLView.FooterArea footer = ZLApplication.Instance().getCurrentView().getFooterArea();
        final ZLView.HeaderArea header = ZLApplication.Instance().getCurrentView().getHeaderArea();
        final int headerHeight = header != null ? header.getHeight() : 0;
        final int footerrHeight = footer != null ? footer.getHeight() : 0;
		final int height = getHeight() - headerHeight - footerrHeight;
		return height - myHDiff;
	}

    private int getMainAreaMarginTop() {
        final ZLView.HeaderArea header = ZLApplication.Instance().getCurrentView().getHeaderArea();
        return header != null ? header.getHeight() : 0;
    }

	private int getStatusBarHeight() {
		final Resources res = getContext().getResources();
		int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
		return resourceId > 0 ? res.getDimensionPixelSize(resourceId) : 0;
	}
}
