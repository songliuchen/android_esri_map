/**=======================================================
 *
 *        工程名称:  tszs.system.config
 *        文件名称:  ConfigManager.java
 *        创 建 人  :  宋 刘 陈
 *        联系方式:  756519755
 *        个人网址:  http://www.songliuchen.com
 *        创建时间:  2015年6月27日
 *        修 改 人  : 
 *        修改时间: 
 *
=======================================================*/

package tszs.system.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import tszs.system.TString;
import tszs.system.io.TDirectory;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

/**
 * 配置信息管理
 * @author song
 */
public class ConfigManager
{
	public final static String CONFIGDATABASENODENAME="configdatabase"; 
	
	/**
	 * 单例设置全局Context对象
	 */
	private static Context context;
	public static void setContext(Context context)
	{
		ConfigManager.context = context;
	}
	
	/**
	 * 单例获取全局Context对象
	 */
	public static Context getContext()
	{
		return context;
	}
	
	private static HashMap<String, Object> innerConfig=new HashMap<String, Object>();
	public static void setConfig(String key,Object config)
	{
		innerConfig.put(key, config);
	}
	
	/**
	 * 单例获取全局Context对象
	 */
	public static Object getConfig(String key)
	{
		if(innerConfig.containsKey(key))
			return innerConfig.get(key);
		return null;
	}
	
	/**
	 * 判断数据库是否存在
	 * 
	 * @return false or true
	 */
	public Boolean IsFileExist(String file)
	{
		try
		{
            File f=new File(file);
            return f.exists();
	    }
		catch (Exception e)
		{
	    	return false;
		}
	}
	
	/**
	 * 复制数据库到手机指定文件夹下
	 */
	public void CopyDataBase(String source,String des)
	{
		try
		{
			// 判断文件夹是否存在，不存在就新建一个
			TDirectory.Create(TDirectory.GetParent(des));
			
			// 得到数据库文件的写入流
			FileOutputStream os = new FileOutputStream(des);
			
			// 得到数据库文件的数据流
			InputStream is = context.getAssets().open(source);
			byte[] buffer = new byte[8192];
			int count = 0;
			while ((count = is.read(buffer)) > 0)
			{
				os.write(buffer, 0, count);
				os.flush();
			}
			is.close();
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取配置库数据库路径
	 * @param context
	 * @return
	 */
	public static String GetConfigDataBasePath()
	{
		if(context == null)
			return null;
		ApplicationInfo appInfo = null;
		try
		{
			appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}

		if(appInfo == null)
			return null;
		
		//如果路径为系统安装路径标示{bin} 则按系统默认配置进行
		String configdatabase=appInfo.metaData.getString(CONFIGDATABASENODENAME);
		configdatabase = TString.Replace(configdatabase, "\\{bin\\}",
				GetDefaultConfigDataBasePath());
		
		return configdatabase;
	}
	
	private static String temppath="";
	/**
	 * 获取配置库完整路径
	 * @return
	 */
	public static String GetConfigDataFullPath()
	{
		return GetConfigDataBasePath()+GetConfigDataBaseName();
	}
	
	public static String GetDefaultConfigDataBasePath()
	{
		if(context == null)
			return null;
		
		String sdcardpath= Environment.getExternalStorageDirectory().getPath();  
		temppath= sdcardpath+"/data/"+context.getPackageName()+"/databases/";
		
		return temppath;
	}
	
	public static String GetDefaultConfigDataBaseFullPath()
	{
		return temppath;
	}
	
	public static void SetDefaultConfigDataBaseFullPath(String path)
	{
		temppath =path;
	}
	
	
	/**
	 * 获取配置库默认名称
	 * @return
	 */
	public static String GetConfigDataBaseName()
	{
		if(TString.IsNullOrEmpty(temppath))
			return  GetDefaultConfigDataBaseFullPath()+ "tszs.db";
		else
			return GetDefaultConfigDataBaseFullPath();
	}
	
	
}