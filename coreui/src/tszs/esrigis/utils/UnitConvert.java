/**==================================================================
 *
 *        工程名称:  tszs.coreui.android.utils
 *        文件名称:  UnitConvert.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年9月6日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.esrigis.utils;

import android.app.Activity;
import android.view.WindowManager;
import tszs.system.config.ConfigManager;

public class UnitConvert
{
	private static float scale = ConfigManager.getContext().getResources().getDisplayMetrics().density;
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int DpToPx(float dpValue)
	{
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int PxToDp(float pxValue)
	{
		return (int)(pxValue / scale + 0.5f);
	}
	
	private static WindowManager wm =null;
	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public static Integer GetScreenWidth()
	{
		if(wm==null)
			wm = ((Activity)tszs.system.config.ConfigManager.getContext()).getWindowManager();
		int width = wm.getDefaultDisplay().getWidth();
		return UnitConvert.PxToDp(width);
	}
	
	/**
	 * 获取屏幕高度
	 * @return
	 */
	public static Integer GetScreenHeight()
	{
		if(wm!=null)
			wm = ((Activity)tszs.system.config.ConfigManager.getContext()).getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();
		return UnitConvert.PxToDp(height);
	}
}
