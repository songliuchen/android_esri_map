/**==================================================================
 *
 *        工程名称:  tszs.coreui.android.control
 *        文件名称:  Message.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年10月4日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.esrigis.control;

import android.widget.Toast;

/**
 * 封装提示功能，用户后续统一提示
 */
public class TMessage
{
	public static void Show(String content)
	{
		Toast.makeText(tszs.system.config.ConfigManager.getContext(), content, Toast.LENGTH_SHORT).show();
	}
}
