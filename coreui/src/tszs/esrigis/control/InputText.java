/**==================================================================
 *
 *        工程名称:  tszs.coreui.android.control
 *        文件名称:  InputTextWidthDelete.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年12月6日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.esrigis.control;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import org.tszs.coreui.R;

import tszs.esrigis.utils.UnitConvert;

/**
 * 扩展EditText 扩展添加输入框左侧支持图标，右侧支持删除操作功能
 * 左侧图标显示和删除功能都为可配置删除
 */
public class InputText extends EditText implements OnFocusChangeListener, TextWatcher
{
	/**
	 * 删除按钮的引用
	 */
	private Drawable mClearDrawable;
	private Boolean showdelete = false;

	private Integer paddingleft = 0;
	private Integer paddingright = 0;
	private Integer paddingtop = 0;
	private Integer paddingbottom = 0;

	/**
	 * 获取是否显示删除图标
	 * 
	 * @return
	 */
	public Boolean getShowDeleteIcon()
	{
		return showdelete;
	}

	/**
	 * 设置是否显示删除图标
	 * 
	 * @return
	 */
	public void setShowDeleteIcon(Boolean showdelete)
	{
		this.showdelete = showdelete;
		if(this.showdelete)
		{
			init();
		}
	}

	/**
	 * 控件是否有焦点
	 */
	private boolean hasFoucs;

	public InputText(Context context)
	{
		super(context);
		init();
	}

	public InputText(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputText, 0, 0);
		if(a ==null)
			return;
		showdelete = a.getBoolean(R.styleable.InputText_showdelete, false);
		if(showdelete != null && showdelete)
			init();
	}

	public InputText(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs,defStyle);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputText, 0, 0);
		if(a ==null)
			return;
		showdelete = a.getBoolean(R.styleable.InputText_showdelete, false);
		if(showdelete != null && showdelete)
			init();
	}

	@SuppressLint("NewApi")
	private void init()
	{
		// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片,getCompoundDrawables()获取Drawable的四个位置的数组
		if(getCompoundDrawables()!=null && getCompoundDrawables().length>1)
			mClearDrawable = getCompoundDrawables()[2];

		if(mClearDrawable == null)
		{
			mClearDrawable = getResources().getDrawable(R.drawable.delete);
		}

		// 设置图标的位置以及大小,getIntrinsicWidth()获取显示出来的大小而不是原图片的带小
		mClearDrawable.setBounds(0, 0, UnitConvert.DpToPx(15), UnitConvert.DpToPx(15));
		// 默认设置隐藏图标
		setClearIconVisible(false);
		// 设置焦点改变的监听
		setOnFocusChangeListener(this);
		// 设置输入框里面内容发生改变的监听
		addTextChangedListener(this);
	}

	/**
	 * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
	 * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(showdelete != null && showdelete)
		{
			if(event.getAction() == MotionEvent.ACTION_UP)
			{
				if(getCompoundDrawables()[2] != null)
				{
					// getTotalPaddingRight()图标左边缘至控件右边缘的距离
					// getWidth() - getTotalPaddingRight()表示从最左边到图标左边缘的位置
					// getWidth() - getPaddingRight()表示最左边到图标右边缘的位置
					boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight()) && (event.getX() < ((getWidth() - getPaddingRight())));

					if(touchable)
					{
						this.setText("");
					}
				}
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		if(showdelete != null && showdelete)
		{
			this.hasFoucs = hasFocus;
			if(hasFocus)
			{
				if(getText().length()>0)
					setPadding(0, 0, UnitConvert.DpToPx(10), 0);
				setClearIconVisible(getText().length() > 0);
			}
			else
			{
				setPadding(paddingleft, paddingtop, paddingright, paddingbottom);
				setClearIconVisible(false);
			}
		}
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible)
	{
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * 当输入框里面内容发生变化的时候回调的方法
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int count, int after)
	{
		if(showdelete != null && showdelete)
		{
			if(hasFoucs)
			{
				if(s.length() > 0)
				{
					setPadding(0, 0, UnitConvert.DpToPx(10), 0);
					setClearIconVisible(true);
				}
				else
				{
					setPadding(paddingleft, paddingtop, paddingright, paddingbottom);
					setClearIconVisible(false);
				}
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after)
	{

	}

	@Override
	public void afterTextChanged(Editable s)
	{

	}

	/**
	 * 设置晃动动画
	 */
	public void setShakeAnimation()
	{
		this.startAnimation(shakeAnimation(5));
	}

	/**
	 * 晃动动画
	 * 
	 * @param counts
	 *            1秒钟晃动多少下
	 * @return
	 */
	public static Animation shakeAnimation(int counts)
	{
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}

}
