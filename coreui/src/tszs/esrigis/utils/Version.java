/**==================================================================
 *
 *        工程名称:  tszs.coreui.android.utils
 *        文件名称:  VersionClass.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2016年10月6日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.esrigis.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * android 版本操作辅助类，用户获取系统版本信息
 */
public class Version
{
	public String GetVersionName()
	{
		// 获取packagemanager的实例
		PackageManager packageManager = tszs.system.config.ConfigManager.getContext().getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		try
		{
			PackageInfo packInfo = packageManager.getPackageInfo(tszs.system.config.ConfigManager.getContext().getPackageName(), 0);
			return packInfo.versionName;
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		return "";
	}

}
