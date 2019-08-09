/**==================================================================
 *
 *        工程名称:  tszs.system.io
 *        文件名称:  MDirectory.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年6月29日
 *        修 改 人  : 
 *        修改时间: 
 *
===================================================================*/

package tszs.system.io;

import tszs.system.TString;

public class TDirectory
{
	/**
	 * 判断文件夹是否存在
	 * @param path
	 * @return
	 */
	public static Boolean Exits(String path)
	{
		java.io.File dir = new java.io.File(path);
		return dir.exists();
	}
	
	/**
	 * 根据路径创建文件夹
	 * @param path
	 * @return
	 */
	public static Boolean Create(String path)
	{
		if(TString.IsNullOrEmpty(path))
			return true;
		
		if(Exits(path))
			return true;
		
		String parent=GetParent(path);
		
		//父文件夹存在，直接创建当前文件夹
		if(Exits(parent))
		{
			java.io.File dir = new java.io.File(path);
			if (!dir.exists())
				return dir.mkdir();
			else
				return true;
		}
		else
		{
			//先创建父文件夹
			if(Create(parent))
			{
				//创建当前文件夹
				return Create(path);
			}
			return false;
		}
	}
	
	/**
	 * 获取父文件夹
	 * @param path
	 * @return
	 */
	public static String GetParent(String path)
	{
		if(TString.IsNullOrEmpty(path))
			return null;
		
		java.io.File dir = new java.io.File(path);
		String folder= dir.getParent();
		return folder;
	}
}
