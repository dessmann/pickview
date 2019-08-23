package com.pickerview.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.pickerview.R;
import com.pickerview.listener.OnDismissListener;
import com.pickerview.utils.PickerViewAnimateUtil;

public class BasePickerView {
	private final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);

	private final Context context;
	ViewGroup contentContainer;
	private ViewGroup decorView;// activity的根View
	private ViewGroup rootView;// 附加View 的 根View

	private OnDismissListener onDismissListener;
	private boolean isDismissing;

	private Animation outAnim;
	private Animation inAnim;
	private final int gravity = Gravity.BOTTOM;

	BasePickerView(Context context) {
		this.context = context;

		initViews();
		init();
		initEvents();
	}

	private void initViews() {
		LayoutInflater layoutInflater = LayoutInflater.from(context);
		//decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
		decorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
		rootView = (ViewGroup) layoutInflater.inflate(R.layout.layout_basepickerview, decorView, false);
		/*rootView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/

		FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			View navigationView = decorView.findViewById(android.R.id.navigationBarBackground);
			if (navigationView != null) {
				params1.setMargins(0, 0, 0, navigationView.getHeight());
			}
		}
		rootView.setLayoutParams(params1);

		contentContainer = (ViewGroup) rootView.findViewById(R.id.content_container);
		contentContainer.setLayoutParams(params);
	}

	private void init() {
		inAnim = getInAnimation();
		outAnim = getOutAnimation();
	}

	private void initEvents()
	{
	}

	/**
	 * show的时候调用
	 * 
	 * @param view
	 *            这个View
	 */
	private void onAttached(View view) {
		decorView.addView(view);
		contentContainer.startAnimation(inAnim);
	}

	/**
	 * 添加这个View到Activity的根视图
	 */
	public void show() {
		if (isShowing()) {
			return;
		}
		onAttached(rootView);
	}

	/**
	 * 检测该View是不是已经添加到根视图
	 * 
	 * @return 如果视图已经存在该View返回true
	 */
	private boolean isShowing() {
		View view = decorView.findViewById(R.id.outmost_container);
		return view != null;
	}

	void dismiss() {
		if (isDismissing) {
			return;
		}

		// 消失动画
		outAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation)	{
				decorView.post(new Runnable() {
					@Override
					public void run() {
						// 从activity根视图移除
						decorView.removeView(rootView);
						isDismissing = false;
						if (onDismissListener != null) {
							onDismissListener.onDismiss(BasePickerView.this);
						}
					}
				});
			}

			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
		});
		
		contentContainer.startAnimation(outAnim);
		isDismissing = true;
	}

	private Animation getInAnimation() {
		int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, true);
		return AnimationUtils.loadAnimation(context, res);
	}

	private Animation getOutAnimation() {
		int res = PickerViewAnimateUtil.getAnimationResource(this.gravity, false);
		return AnimationUtils.loadAnimation(context, res);
	}

	public BasePickerView setOnDismissListener(OnDismissListener onDismissListener)	{
		this.onDismissListener = onDismissListener;
		return this;
	}

	public BasePickerView setCancelable(boolean isCancelable) {
		View view = rootView.findViewById(R.id.outmost_container);

		if (isCancelable) {
			view.setOnTouchListener(onCancelableTouchListener);
		} else {
			view.setOnTouchListener(null);
		}
		return this;
	}

	/**
	 * Called when the user touch on black overlay in order to dismiss the
	 * dialog
	 */
	private final View.OnTouchListener onCancelableTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event){
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				dismiss();
			}
			return false;
		}
	};

	View findViewById(int id) {
		return contentContainer.findViewById(id);
	}
}
